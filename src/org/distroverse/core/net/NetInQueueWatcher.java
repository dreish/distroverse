/*
 * <copyleft>
 *
 * Copyright 2007-2009 Dan Reish
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or
 * combining it with Clojure (or a modified version of that program)
 * or clojure-contrib (or a modified version of that library),
 * containing parts covered by the terms of the Eclipse Public
 * License, the licensors of this Program grant you additional
 * permission to convey the resulting work. {Corresponding Source for
 * a non-source form of such a combination shall include the source
 * code for the parts of Clojure and clojure-contrib used as well as
 * that of the covered work.}
 *
 * </copyleft>
 */
package org.distroverse.core.net;

import java.io.IOException;
import java.net.ProtocolException;
import java.nio.channels.ClosedChannelException;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.distroverse.core.Log;

/**
 * A thread that watches a NetInQueue< T >, calling the abstract method
 * handleNetObject() with every object that appears in any of the
 * watched queues.
 * @author dreish
 * @param <T>
 */
public abstract class NetInQueueWatcher< T > extends Thread
   {
   public NetInQueueWatcher()
      {
      super();
      mWatchedQueues = new LinkedList< NetInQueue< T > >();
      mShuttingDown  = false;
      }
   
   /* (non-Javadoc)
    * @see java.lang.Thread#run()
    */
   @Override
   public void run()
      {
      try
         {
         while ( ! mShuttingDown )
            {
            clearAllQueues();
            sleepUntilInterrupted();
            }
         }
      catch ( ClosedChannelException e )
         {
         // Finished; let the thread end now.
         }
      catch ( IOException e )
         {
         Log.p( "NetInQueueWatcher died: " + e, Log.NET, 100 );
         Log.p( e, Log.NET, 60 );
         }
      }
   
   /**
    * Shuts down the watcher thread, and returns whether the thread was
    * already shutting down.
    * @return
    */
   public boolean shutdown()
      {
      boolean ret = mShuttingDown;
      mShuttingDown = true;
      return ret;
      }

   @SuppressWarnings("unchecked")
   private void clearAllQueues() throws IOException
      {
      Log.p( "clearAllQueues() called", Log.NET, -150 );
      
      Object[] qs;

      synchronized ( mWatchedQueues )
         {
         qs = mWatchedQueues.toArray();
         }
      
      for ( Object niq_o : qs )
         {
         NetInQueue< T > niq = (NetInQueue< T >) niq_o;
         try
            {
            while ( true )
               {
               T net_in_object = niq.remove();
               handleNetInObject( net_in_object, niq );
               }
            }
         catch ( ProtocolException e )
            {
            Log.p( e, Log.NET, 10 );
            niq.getRemote().close();
            synchronized ( mWatchedQueues )
               {
               mWatchedQueues.remove( niq );
               }
            niq.removeQueueWatcher( this );
            }
         catch ( NoSuchElementException e )
            {  /* Ignore; finished with this queue. */  }
         catch ( ClosedChannelException e )
            {
            synchronized ( mWatchedQueues )
               {
               mWatchedQueues.remove( niq );
               }
            niq.removeQueueWatcher( this );
            throw e;
            }
         }
      }

   /**
    * The specialization of this method will be called by
    * NetInQueueWatcher.clearAllQueues() for every object that is
    * received.
    * @param net_in_object
    * @param queue
    * @throws IOException
    */
   protected abstract void handleNetInObject( T net_in_object,
                                              NetInQueue< T > queue )
   throws IOException;

   /**
    * Actually sleeps for at most two minutes.
    */
   private void sleepUntilInterrupted()
      {
      try
         {  Thread.sleep( 120000 );  }
      catch ( InterruptedException e )
         {  /* Interrupted; work on the queues now. */  }
      }
   
   public void addQueue( NetInQueue< T > niq )
      {
      synchronized ( mWatchedQueues )
         {
         mWatchedQueues.add( niq );
         }
      niq.addQueueWatcher( this );
      }

   private final LinkedList< NetInQueue< T > > mWatchedQueues;
   private boolean                             mShuttingDown;
   }

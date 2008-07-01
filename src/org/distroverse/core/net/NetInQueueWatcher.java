/*
 * Copyright (c) 2007-2008 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * General Public License (GPL) version 3 or later</a>
 */
package org.distroverse.core.net;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.distroverse.core.*;

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
      }
   
   /* (non-Javadoc)
    * @see java.lang.Thread#run()
    */
   @Override
   public void run()
      {
      try
         {
         while ( true )
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


   private void clearAllQueues() throws IOException
      {
      // FIXME Is it safe to do this without synchronizing?
      // FIXME Looks like this is better done with an int indexed vector
      Log.p( "clearAllQueues() called", Log.NET, -50 );
      for ( NetInQueue< T > niq : mWatchedQueues )
         {
         try
            {
            while ( true )
               {
               T net_in_object = niq.remove();
               handleNetInObject( net_in_object, niq );
               }
            }
         catch ( NoSuchElementException e )
            {  /* Ignore; finished with this queue. */  }
         catch ( ClosedChannelException e )
            {
            synchronized ( mWatchedQueues )
               {
               // FIXME Is it safe to do this while iterating?
               mWatchedQueues.remove( niq );
               niq.removeQueueWatcher( this );
               throw e;
               }
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
      mWatchedQueues.add( niq );
      niq.addQueueWatcher( this );
      }

   private LinkedList< NetInQueue< T > > mWatchedQueues;
   }

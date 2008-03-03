package org.distroverse.core.net;

import java.nio.channels.ClosedChannelException;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public abstract class NetInQueueWatcher< T > extends Thread
   {
   public NetInQueueWatcher()
      {
      super();
      }
   
   /* (non-Javadoc)
    * @see java.lang.Thread#run()
    */
   @Override
   public void run()
      {
      while ( true )
         {
         clearAllQueues();
         sleepUntilInterrupted();
         }
      }

   private void clearAllQueues()
      {
      // FIXME Is it safe to do this without synchronizing?
      // FIXME Looks like this is better done with an int indexed vector
      for ( NetInQueue< T > niq : mWatchedQueues )
         {
         try
            {
            T net_in_object = niq.remove();
            handleNetInObject( net_in_object, niq );
            }
         catch ( NoSuchElementException e )
            {  /* Ignore; someone else cleared queue. */  }
         catch ( ClosedChannelException e )
            {
            synchronized ( mWatchedQueues )
               {
               // FIXME Is it safe to do this while iterating?
               mWatchedQueues.remove( niq );
               }
            }
         }
      }

   protected abstract void handleNetInObject( T net_in_object,
                                              NetInQueue< T > queue );

   /**
    * Actually sleeps for at most two minutes.
    */
   private void sleepUntilInterrupted()
      {
      try
         {  Thread.sleep( 120 );  }
      catch ( InterruptedException e )
         {  /* Interrupted; work on the queues now. */  }
      }

   private LinkedList< NetInQueue< T > > mWatchedQueues;
   }

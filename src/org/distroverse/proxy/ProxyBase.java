package org.distroverse.proxy;

import java.util.concurrent.BlockingQueue;

import org.distroverse.core.Log;
import org.distroverse.dvtp.CompactUlong;
import org.distroverse.dvtp.DvtpProxy;
import org.distroverse.dvtp.MoveSeq;
import org.distroverse.dvtp.ProxySendable;
import org.distroverse.dvtp.Shape;

/**
 * Provides some handy utility classes and implements the fairly obvious
 * setQueue() method.
 * @author dreish
 */
public abstract class ProxyBase implements DvtpProxy
   {
   public ProxyBase()
      {
      super();
      }

   public void setQueue( BlockingQueue< ProxySendable > queue )
      {
      mClientQueue = queue;
      }

   /**
    * Add an object to the client's queue.  In most cases, there is
    * another method in ProxyBase that provides a convenient alternative
    * to calling this one.
    * 
    * For now, this is just going to log and keep retrying if it gets
    * interrupted.  This is probably a very bad strategy, but we'll see.
    * @param o
    */
   protected void putQueue( ProxySendable o )
      {
      while ( true )
         {
         try
            {
            mClientQueue.put( o );
            return;
            }
         catch ( InterruptedException e )
            {
            Log.p( "mClientQueue.put() interrupted:", Log.UNHANDLED,
                   0 );
            Log.p( e, Log.UNHANDLED, 0 );
            }
         }
      }
   
   protected void addObject( Shape s, CompactUlong id, CompactUlong pid,
                             MoveSeq m )
      {
      
      }

   protected void addObject( Shape s, long id,
                             double x, double y, double z )
      {
      // TODO Auto-generated method stub
      
      }

   
   private BlockingQueue< ProxySendable > mClientQueue;
   }
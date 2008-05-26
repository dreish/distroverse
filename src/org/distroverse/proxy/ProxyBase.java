package org.distroverse.proxy;

import java.util.concurrent.BlockingQueue;

import org.distroverse.core.Log;
import org.distroverse.core.net.DvtpProxyInQueueObjectWatcher;
import org.distroverse.core.net.NetInQueue;
import org.distroverse.core.net.NetInQueueWatcher;
import org.distroverse.core.net.NetOutQueue;
import org.distroverse.dvtp.ClientSendable;
import org.distroverse.dvtp.DvtpExternalizable;
import org.distroverse.dvtp.DvtpProxy;
import org.distroverse.dvtp.ProxySendable;

/**
 * Provides a useful base upon which to build proxy classes that
 * communicate with a server.
 * @author dreish
 */
public abstract class ProxyBase implements DvtpProxy
   {
   public ProxyBase()
      {
      mWatcher = new DvtpProxyInQueueObjectWatcher( this );
      mWatcher.start();
      }
   
   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpProxy#offer(org.distroverse.dvtp.ClientSendable)
    */
   public void offer( ClientSendable o )
      {
      // TODO Auto-generated method stub

      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpProxy#run()
    */
   public void run()
      {
      // TODO Auto-generated method stub

      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpProxy#setQueue(java.util.concurrent.BlockingQueue)
    */
   public void setQueue( BlockingQueue< ProxySendable > queue )
      {
      mClientQueue = queue;
      }
   
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

   /**
    * This method is called by the NetInQueueWatcher every time an
    * object is sent from the server.
    * @param o
    */
   public abstract void receiveFromServer( Object o );

   private BlockingQueue< ProxySendable > mClientQueue;
   private NetOutQueue< DvtpExternalizable > mToServerQueue;
   private NetInQueue< DvtpExternalizable > mFromServerQueue;
   private NetInQueueWatcher< Object > mWatcher;
   }

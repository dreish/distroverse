package org.distroverse.proxy;

import java.util.concurrent.BlockingQueue;

import org.distroverse.dvtp.ClientSendable;
import org.distroverse.dvtp.DvtpProxy;
import org.distroverse.dvtp.ProxySendable;

/**
 * Provides a useful base upon which to build proxy classes.
 * @author dreish
 */
public abstract class ProxyBase implements DvtpProxy
   {

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
   throws InterruptedException
      {
      mClientQueue.put( o );
      }

   /**
    * This method is called by ProxyBase every time an object is sent
    * from the server.
    * @param o
    * @throws InterruptedException
    */
   protected abstract void receiveFromServer( Object o )
   throws InterruptedException;

   private BlockingQueue< ProxySendable > mClientQueue;
   }

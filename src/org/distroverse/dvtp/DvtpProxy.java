package org.distroverse.dvtp;

import java.util.concurrent.BlockingQueue;

/**
 * Any proxy must implement this interface.
 * @author dreish
 */
public interface DvtpProxy
   {
   /**
    * Called by the client to set the queue through which the proxy will
    * feed objects to the client.
    * @param queue
    */
   public void setQueue( BlockingQueue< ProxySendable > queue );

   /**
    * Called by the client to start the proxy.  Does not return.
    */
   public void run();

   /**
    * Called by the client to feed an object to the proxy.
    * @param o
    */
   public void offer( ClientSendable o );
   }

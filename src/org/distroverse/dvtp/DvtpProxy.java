package org.distroverse.dvtp;

import java.util.concurrent.BlockingQueue;

public interface DvtpProxy
   {
   /**
    * Sets the queue through which the proxy will feed objects to the
    * client.
    * @param queue
    */
   public void setQueue( BlockingQueue< ProxySendable > queue );

   /**
    * Starts the proxy.  Does not return.
    */
   public void run();

   /**
    * Feeds an object from the client to the proxy.
    * @param o
    */
   public void offer( ClientSendable o );
   }

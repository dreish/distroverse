package org.distroverse.dvtp;

import java.util.Queue;

public interface DvtpProxy
   {
   /**
    * Sets the queue through which the proxy will feed objects to the
    * client.
    * @param queue
    */
   public void setQueue( Queue< DvtpExternalizable > queue );

   /**
    * Starts the proxy.  Does not return.
    */
   public void run();

   /**
    * Feeds an object from the client to the proxy.
    * @param o
    */
   public void offer( DvtpExternalizable o );
   }

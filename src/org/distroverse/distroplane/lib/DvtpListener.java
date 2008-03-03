/**
 * 
 */
package org.distroverse.distroplane.lib;

/**
 * @author dreish
 */
public abstract class DvtpListener
   {
   /**
    * Waits for incoming connections and handles them.  Does not return.
    */
   public abstract void serve();

   public DvtpServer getServer()
      {  return mServer;  }

   public void setServer( DvtpServer server )
      {  mServer = server;  }

   DvtpServer mServer;
   }
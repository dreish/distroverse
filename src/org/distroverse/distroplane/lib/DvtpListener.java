/**
 * 
 */
package org.distroverse.distroplane.lib;

import org.distroverse.core.net.NetInQueueWatcher;

/**
 * @author dreish
 */
public abstract class DvtpListener
   {
   /**
    * Waits for incoming connections and handles them.  Does not return
    * unless the server aborts for some reason.
    */
   public abstract void serve();

   public DvtpServer getServer()
      {  return mServer;  }

   public void setServer( DvtpServer server )
      {  mServer = server;  }

   public void setWatcher( NetInQueueWatcher< Object > watcher_thread )
      {
      mWatcher = watcher_thread;
      }

   protected DvtpServer                  mServer;
   protected NetInQueueWatcher< Object > mWatcher;
   }

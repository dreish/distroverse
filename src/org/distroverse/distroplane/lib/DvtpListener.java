/*
 * Copyright (c) 2007-2008 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * General Public License (GPL) version 3 or later</a>
 */
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
      {  mWatcher = watcher_thread;  }

   public void setGreeting( String greeting )
      {  mGreeting = greeting;  }

   protected DvtpServer                  mServer;
   protected NetInQueueWatcher< Object > mWatcher;
   protected String                      mGreeting;
   }

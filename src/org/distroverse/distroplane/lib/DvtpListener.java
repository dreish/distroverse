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
 * A DvtpListener's serve() method opens a server socket, listens for
 * incoming connections, receives DVTP commands and sends responses as
 * defined by a DvtpServer.
 * @author dreish
 */
public interface DvtpListener
   {
   /**
    * Waits for incoming connections and handles them.  Does not return
    * unless the server aborts for some reason.
    */
   public abstract void serve();

   public DvtpServer getServer();

   public void setServer( DvtpServer server );

   public void setWatcher( NetInQueueWatcher< Object > watcher_thread );

   public void setGreeting( String greeting );
   }

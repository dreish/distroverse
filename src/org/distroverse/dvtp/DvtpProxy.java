/*
 * Copyright (c) 2007-2008 Dan Reish.
 * 
 * For license details, see the file COPYING-L in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/lgpl.html">GNU
 * Lesser General Public License (LGPL) version 3 or later</a>
 */
package org.distroverse.dvtp;

import java.io.IOException;
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
    * @throws IOException 
    */
   public void offer( ClientSendable o ) throws IOException;
   }

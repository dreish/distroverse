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
 * Any envoy must implement this interface.
 * @author dreish
 */
public interface DvtpEnvoy
   {
   /**
    * Called by the client to set the queue through which the envoy will
    * feed objects to the client.
    * @param queue
    */
   public void setQueue( BlockingQueue< EnvoySendable > queue );

   /**
    * Called by the client to start the envoy.  Does not return.
    */
   public void run();

   /**
    * Called by the client to feed an object to the envoy.
    * @param o
    * @throws IOException 
    */
   public void offer( ClientSendable o ) throws IOException;
   }

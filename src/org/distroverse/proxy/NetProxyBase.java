/*
 * Copyright (c) 2007-2008 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * General Public License (GPL) version 3 or later</a>
 */
package org.distroverse.proxy;

import java.nio.channels.ClosedChannelException;

import org.distroverse.core.net.DvtpProxyInQueueObjectWatcher;
import org.distroverse.core.net.NetInQueue;
import org.distroverse.core.net.NetInQueueWatcher;
import org.distroverse.core.net.NetOutQueue;
import org.distroverse.dvtp.ClientSendable;
import org.distroverse.dvtp.DvtpExternalizable;

/**
 * Provides a useful base upon which to build proxy classes that
 * communicate with a server using pure-object DVTP (i.e., no simple
 * CRLF-terminated strings).
 * @author dreish
 */
public abstract class NetProxyBase extends ProxyBase
   {
   public NetProxyBase()
      {
      mWatcher = new DvtpProxyInQueueObjectWatcher( this );
      mWatcher.start();
      }
   
   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpProxy#offer(org.distroverse.dvtp.ClientSendable)
    */
   public void offer( ClientSendable o )
      {
      // XXX Auto-generated method stub

      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpProxy#run()
    */
   public void run()
      {
      // XXX Auto-generated method stub

      }
   
   public void sendToServer( DvtpExternalizable o )
   throws ClosedChannelException
      {
      mToServerQueue.add( o );
      }
   
   /**
    * This method is called by the NetInQueueWatcher every time an
    * object is sent from the server.
    * @param o
    */
   public abstract void receiveFromServer( DvtpExternalizable o );

   private NetOutQueue< DvtpExternalizable > mToServerQueue;
   private NetInQueue< DvtpExternalizable > mFromServerQueue;
   private NetInQueueWatcher< DvtpExternalizable > mWatcher;
   }

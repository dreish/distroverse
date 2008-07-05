/*
 * Copyright (c) 2007-2008 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * General Public License (GPL) version 3 or later</a>
 */
package org.distroverse.proxy;

import java.nio.channels.ClosedChannelException;

import org.distroverse.core.net.NetSession;
import org.distroverse.dvtp.ClientSendable;
import org.distroverse.dvtp.DvtpExternalizable;
import org.distroverse.dvtp.ProxySendable;

public class PassThroughProxy extends SingleServerProxyBase
   {
   @Override
   public void receiveFromServer( NetSession< Object > s,
                                  DvtpExternalizable o )
      {
      this.putQueue( (ProxySendable) o );
      }
   
   @Override
   protected void receiveFromClient( ClientSendable o )
   throws ClosedChannelException
      {
      this.sendToServer( o );
      }
   }

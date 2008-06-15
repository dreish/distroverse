package org.distroverse.proxy;

import java.nio.channels.ClosedChannelException;

import org.distroverse.dvtp.ClientSendable;
import org.distroverse.dvtp.DvtpExternalizable;
import org.distroverse.dvtp.ProxySendable;


public class PassThroughProxy extends NetProxyBase
   {
   @Override
   public void receiveFromServer( DvtpExternalizable o )
      {
      this.putQueue( (ProxySendable) o );
      }
   
   protected void receiveFromClient( ClientSendable o )
   throws ClosedChannelException
      {
      this.sendToServer( o );
      }
   }

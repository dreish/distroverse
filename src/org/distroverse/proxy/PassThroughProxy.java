package org.distroverse.proxy;

import org.distroverse.dvtp.ProxySendable;


public class PassThroughProxy extends ProxyBase
   {
   @Override
   protected void receiveFromServer( Object o )
   throws InterruptedException
      {
      this.putQueue( (ProxySendable) o );
      }
   }

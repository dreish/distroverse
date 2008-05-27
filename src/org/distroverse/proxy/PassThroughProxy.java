package org.distroverse.proxy;

import org.distroverse.dvtp.ProxySendable;


public class PassThroughProxy extends NetProxyBase
   {
   @Override
   public void receiveFromServer( Object o )
      {
      this.putQueue( (ProxySendable) o );
      }
   
   protected void receiveFromClient( Object o )
      {
      
      }
   }

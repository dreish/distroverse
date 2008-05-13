/**
 * 
 */
package org.distroverse.dvtp;

import java.net.ProtocolException;

import org.distroverse.viewer.ProxyErrorException;

/**
 * Subclasses of this class handle incoming DVTP objects from a proxy
 * for a particular client.
 * @author dreish
 */
public abstract class ClientDispatcher
   {
   public void dispatchObject( DvtpExternalizable o ) 
   throws ProtocolException, ProxyErrorException
      {
      switch ( o.getClassNumber() )
         {
         case 10:
            dispatchDisplayUrl( (DisplayUrl) o );
            break;
         case 11:
            dispatchRedirectUrl( (RedirectUrl) o );
            break;
         default:
            if ( o.isSendableByProxy() )
               throw new RuntimeException( "ClientDispatcher does not"
                            + " know how to handle a valid class, "
                            + o.getClass().getCanonicalName() );
            throw new ProtocolException( "Proxy sent an illegal"
                            + " DvtpExternalizable subclass, "
                            + o.getClass().getCanonicalName() );
         }
      }
   
   protected abstract void dispatchDisplayUrl( DisplayUrl o )
   throws ProxyErrorException;
   protected abstract void dispatchRedirectUrl( RedirectUrl o )
   throws ProxyErrorException;
   }

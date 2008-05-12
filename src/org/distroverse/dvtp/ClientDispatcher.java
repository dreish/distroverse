/**
 * 
 */
package org.distroverse.dvtp;

import java.net.ProtocolException;

/**
 * Subclasses of this class handle incoming DVTP objects from a proxy
 * for a particular client.
 * @author dreish
 */
public abstract class ClientDispatcher
   {
   public void dispatchObject( DvtpExternalizable o ) 
   throws ProtocolException
      {
      switch ( o.getClassNumber() )
         {
         case 9:
            dispatchSetUrl( (SetUrl) o );
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
   
   protected abstract void dispatchSetUrl( SetUrl o );
   }

/**
 * 
 */
package org.distroverse.dvtp;

import org.distroverse.viewer.ProxyErrorException;

/**
 * Subclasses of this class handle incoming DVTP objects from a proxy
 * for a particular client.
 * @author dreish
 */
public abstract class ClientDispatcher
   {
   public void dispatchObject( ProxySendable o ) 
   throws ProxyErrorException
      {
      switch ( o.getClassNumber() )
         {
         case 10:
            dispatchDisplayUrl( (DisplayUrl) o );
            break;
         case 11:
            dispatchRedirectUrl( (RedirectUrl) o );
            break;
         case 12:
            dispatchAddObject( (AddObject) o );
            break;
         case 14:
            dispatchMoveObject( (MoveObject) o );
            break;
         default:
            throw new RuntimeException( "ClientDispatcher does not"
                            + " know how to handle a valid class, "
                            + o.getClass().getCanonicalName() );
         }
      }
   
   protected abstract void dispatchDisplayUrl( DisplayUrl o )
   throws ProxyErrorException;
   protected abstract void dispatchRedirectUrl( RedirectUrl o )
   throws ProxyErrorException;
   protected abstract void dispatchAddObject( AddObject o )
   throws ProxyErrorException;
   protected abstract void dispatchMoveObject( MoveObject o )
   throws ProxyErrorException;
   }

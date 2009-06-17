/*
 * Copyright (c) 2007-2008 Dan Reish.
 * 
 * For license details, see the file COPYING-L in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/lgpl.html">GNU
 * Lesser General Public License (LGPL) version 3 or later</a>
 */
/**
 * 
 */
package org.distroverse.dvtp;

import org.distroverse.viewer.EnvoyErrorException;

/**
 * Subclasses of this class handle incoming DVTP objects from an envoy
 * for a particular client.
 * @author dreish
 */
public abstract class ClientDispatcher
   {
   public void dispatchObject( EnvoySendable o ) 
   throws EnvoyErrorException
      {
      switch ( o.getClassNumber() )
         {
         case 7:
            dispatchDisplayUrl( (DisplayUrl) o );
            break;
         case 8:
            dispatchRedirectUrl( (RedirectUrl) o );
            break;
         case 12:
            dispatchAddObject( (AddObject) o );
            break;
         case 14:
            dispatchMoveObject( (MoveObject) o );
            break;
         case 17:
            dispatchDeleteObject( (DeleteObject) o );
            break;
         default:
            // XXX Probably should just ignore this
            throw new RuntimeException( "ClientDispatcher does not"
                            + " know how to handle a valid class, "
                            + o.getClass().getCanonicalName() );
         }
      }
   
   protected abstract void dispatchDisplayUrl( DisplayUrl o )
   throws EnvoyErrorException;
   protected abstract void dispatchRedirectUrl( RedirectUrl o )
   throws EnvoyErrorException;
   protected abstract void dispatchAddObject( AddObject o )
   throws EnvoyErrorException;
   protected abstract void dispatchMoveObject( MoveObject o )
   throws EnvoyErrorException;
   protected abstract void dispatchDeleteObject( DeleteObject o )
   throws EnvoyErrorException;
   }

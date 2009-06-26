/*
 * Copyright (c) 2007-2008 Dan Reish.
 *
 * For license details, see the file COPYING-L in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/lgpl.html">GNU
 * Lesser General Public License (LGPL) version 3 or later</a>
 */
package org.distroverse.dvtp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.distroverse.core.Util;

//immutable

/**
 * A mouseclick with the primary (index finger) mouse button, or a
 * one-fingered touch.  Typically represents a touch, grab, or pull.
 * @author dreish
 */
public class Click implements ClientSendable
   {
   public Click( InputStream in ) throws IOException
      {
      super();
      mDirection = new Vec( in );
      mForce = new Flo( in );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings("unused")
   private Click()
      {
      super();
      mDirection = null;
      mForce = null;
      }

   public Click( Vec dir, Flo force )
      {
      super();
      mDirection = dir;
      mForce     = force;
      }

   public int getClassNumber()
      {  return 22;  }

   @Override
   public boolean equals( Object o )
      {
      if ( o.getClass().equals( this.getClass() ) )
         {
         Click c = (Click) o;
         return (mDirection.equals( c.mDirection )
                 &&  mForce.equals( c.mForce ));
         }
      return false;
      }

   @Override
   public int hashCode()
      {
      return mDirection.hashCode()
             ^ mForce.hashCode()
             ^ this.getClass().hashCode();
      }

   public Vec getDirection()
      {  return mDirection;  }
   public Flo getForce()
      {  return mForce;  }

   public void writeExternal( OutputStream out ) throws IOException
      {
      mDirection.writeExternal( out );
      mForce.writeExternal( out );
      }

   public String prettyPrint()
      {
      return "(org.distroverse.dvtp.Click. "
             + Util.prettyPrintList( mDirection, mForce ) + ")";
      }

   private final Vec mDirection;
   private final Flo mForce;
   }

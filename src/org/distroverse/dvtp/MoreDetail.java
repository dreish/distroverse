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

//immutable

/**
 * A signal from the client that it is rendering quickly and can handle
 * a more detailed scene, or (for values less than 1) that it is
 * rendering slowly and needs a less detailed scene.  The number
 * indicates the factor above the target frames/second rate at which the
 * client is currently rendering.  It should always be a positive
 * number.  Envoy or server behavior for values less than or equal to
 * zero is undefined.
 * @author dreish
 */
public class MoreDetail extends Flo implements ClientSendable
   {
   public MoreDetail( InputStream in ) throws IOException
      {
      super( in );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings("unused")
   private MoreDetail()
      {
      super( 0 );
      }

   public MoreDetail( float f )
      {
      super( f );
      }

   @Override
   public int getClassNumber()
      {  return 24;  }

   public float getAmount()
      {  return asFloat();  }

   @Override
   public String prettyPrint()
      {
      return "(org.distroverse.dvtp.MoreDetail. " + getAmount() + ")";
      }
   }

/*
 * Copyright (c) 2007-2008 Dan Reish.
 *
 * For license details, see the file COPYING-L in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/lgpl.html">GNU
 * Lesser General Public License (LGPL) version 3 or later</a>
 */
package org.distroverse.dvtp;

/**
 * A signal from the client that it is rendering quickly and can handle
 * a more detailed scene, or (for values less than 1) that it is
 * rendering slowly and needs a less detailed scene.  The number
 * indicates the factor above the target frames/second rate at which the
 * client is currently rendering.  It should always be a positive
 * number.  Client behavior for values less than or equal to zero are
 * undefined.
 * @author dreish
 */
public class MoreDetail extends Flo implements ClientSendable
   {
   public MoreDetail()
      {
      super();
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
      return "(MoreDetail " + getAmount() + ")";
      }
   }

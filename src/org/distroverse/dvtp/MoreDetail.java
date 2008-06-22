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
 * a more detailed scene.  The number indicates the factor above the
 * target frames/second rate at which the client is currently rendering.
 * It should always be greater than 1.
 * @author dreish
 */
public class MoreDetail extends Flo implements ClientSendable
   {
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

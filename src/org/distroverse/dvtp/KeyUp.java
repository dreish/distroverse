/*
 * Copyright (c) 2007-2008 Dan Reish.
 * 
 * For license details, see the file COPYING-L in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/lgpl.html">GNU
 * Lesser General Public License (LGPL) version 3 or later</a>
 */
package org.distroverse.dvtp;

public class KeyUp extends Keystroke
   {
   public KeyUp()
      {  super();  }

   public KeyUp( int kn )
      {  super( kn );  }

   @Override
   public int getClassNumber()
      {  return 21;  }

   @Override
   public String prettyPrint()
      {
      return "(KeyUp " + getKey() + ")";
      }
   }

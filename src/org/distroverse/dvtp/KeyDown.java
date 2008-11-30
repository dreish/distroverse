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

public class KeyDown extends Keystroke
   {
   public KeyDown( InputStream in ) throws IOException
      {
      super( in );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings("unused")
   private KeyDown()
      {  super( 0 );  }

   public KeyDown( int kn )
      {  super( kn );  }

   @Override
   public int getClassNumber()
      {  return 20;  }
   
   @Override
   public String prettyPrint()
      {
      return "(KeyDown " + getKey() + ")";
      }
   }

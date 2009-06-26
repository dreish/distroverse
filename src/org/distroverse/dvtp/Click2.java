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
 * A mouseclick with the secondary mouse button, or a two-fingered
 * touch.  Typically calls a context-dependent menu, so "force" may not
 * be relevant in most cases, but it's available.
 * @author dreish
 */
public class Click2 extends Click
   {
   public Click2( InputStream in ) throws IOException
      {
      super( in );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings("unused")
   private Click2() throws IOException
      { 
      super( null );
      }
   
   public Click2( Vec dir, Flo force )
      {  super( dir, force );  }

   @Override
   public int getClassNumber()
      {  return 23;  }

   @Override
   public String prettyPrint()
      {
      // XXX don't do this
      return super.prettyPrint().replaceFirst( "k", "k2" );
      }
   }

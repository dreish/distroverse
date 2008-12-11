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

public class Keystroke implements ClientSendable
   {
   public Keystroke( InputStream in ) throws IOException
      {
      super();
      mKeyNum = Util.safeInt( ULong.externalAsLong( in ) );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings("unused")
   private Keystroke()
      {
      super();
      mKeyNum = 0;
      }

   public Keystroke( int kn )
      {
      super();
      mKeyNum = kn;
      }

   public int getClassNumber()
      {  return 19;  }

   @Override
   public boolean equals( Object o )
      {
      return (o.getClass().equals( this.getClass() )
              &&  mKeyNum == ((Keystroke) o).mKeyNum);
      }

   @Override
   public int hashCode()
      {
      return mKeyNum ^ this.getClass().hashCode();
      }

   public int getKey()
      {  return mKeyNum;  }

   public void writeExternal( OutputStream out ) throws IOException
      {
      ULong.longAsExternal( out, mKeyNum );
      }

   public String prettyPrint()
      {
      return "(KeyStroke " + mKeyNum + ")";
      }

   private final int mKeyNum;
   }

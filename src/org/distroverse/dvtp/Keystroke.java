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

public class Keystroke implements ClientSendable
   {
   public Keystroke()
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
   public void setKey( int kn )
      {  mKeyNum = kn;  }

   public void readExternal( InputStream in ) throws IOException
      {
      mKeyNum = Util.safeInt( CompactUlong.externalAsLong( in ) );
      }

   public void writeExternal( OutputStream out ) throws IOException
      {
      CompactUlong.longAsExternal( out, mKeyNum );
      }

   public String prettyPrint()
      {
      return "(KeyStroke " + mKeyNum + ")";
      }

   private int mKeyNum;
   }

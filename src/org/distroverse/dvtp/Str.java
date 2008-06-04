/*
 * Copyright (c) 2007 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * Lesser General Public License (GPL) version 3 or later</a>
 */
package org.distroverse.dvtp;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import org.distroverse.core.Util;

/**
 * N.B.: A Str is prefixed with the length of the UTF-8 encoded version
 * of the string in bytes, not the number of characters in the string.
 * @author dreish
 */
public class Str implements DvtpExternalizable
   {
   public Str( String s )
      {  mVal = s;  }
   public Str()
      {  mVal = null;  }

   /**
    * Returns the Str as a String.
    */
   @Override
   public String toString()
      {  return mVal;  }

   public static String externalAsString( ObjectInput in )
   throws IOException
      {
      int    len = Util.safeInt( CompactUlong.externalAsLong( in ) );
      byte[] buf = new byte[ len ];
      in.readFully( buf );
      return Charset.forName( "UTF-8" )
                    .decode( ByteBuffer.wrap( buf ) )
                    .toString();
      }
   
   public static void stringAsExternal( ObjectOutput out, String val )
   throws IOException
      {
      ByteBuffer bb = Charset.forName( "UTF-8" )
                             .encode( CharBuffer.wrap( val ) );
      CompactUlong.longAsExternal( out, bb.limit() );
      out.write( bb.array() );
      return;
      }

   public int getClassNumber()
      {  return 2;  }

   public void readExternal( ObjectInput in ) throws IOException
      {
      mVal = externalAsString( in );
      }

   public void writeExternal( ObjectOutput out ) throws IOException
      {
      stringAsExternal( out, mVal );
      }

   private String mVal;
   }

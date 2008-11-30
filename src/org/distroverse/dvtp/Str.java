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
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import org.distroverse.core.Util;

//immutable

/**
 * N.B.: A Str is prefixed with the length of the UTF-8 encoded version
 * of the string in bytes, not the number of characters in the string.
 * @author dreish
 */
public class Str implements DvtpExternalizable
   {
   public Str( String s )
      {  mVal = s;  }
   public Str( InputStream in ) throws IOException
      {
      super();
      readExternal( in );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings( "unused" )
   private Str()
      {  mVal = null;  }

   /**
    * Returns the Str as a String.
    */
   @Override
   public String toString()
      {  return mVal;  }

   public static String externalAsString( InputStream in )
   throws IOException
      {
      int    len = Util.safeInt( ULong.externalAsLong( in ) );
      if ( len == 0 )
         return "";
      byte[] buf = new byte[ len ];
      if ( in.read( buf ) != len )
         throw new IOException( "Could not read full Str" );
      return Charset.forName( "UTF-8" )
                    .decode( ByteBuffer.wrap( buf ) )
                    .toString();
      }

   public static void stringAsExternal( OutputStream out, String val )
   throws IOException
      {
      ByteBuffer bb = Charset.forName( "UTF-8" )
                             .encode( CharBuffer.wrap( val ) );
      ULong.longAsExternal( out, bb.limit() );
      out.write( bb.array(), 0, bb.limit() );
      return;
      }

   public int getClassNumber()
      {  return 2;  }

   @Override
   public boolean equals( Object o )
      {
      return (o.getClass().equals( getClass() )
              &&  mVal.equals( ((Str) o).mVal ));
      }

   @Override
   public int hashCode()
      {
      return mVal.hashCode() ^ getClass().hashCode();
      }

   private void readExternal( InputStream in ) throws IOException
      {
      mVal = externalAsString( in );
      }

   public void writeExternal( OutputStream out ) throws IOException
      {
      stringAsExternal( out, mVal );
      }

   public String prettyPrint()
      {
      return "(Str "
             + Util.prettyPrintList( mVal ) + ")";
      }

   private String mVal;
   }

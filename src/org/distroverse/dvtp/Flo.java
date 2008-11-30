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

/**
 * A float is just sent in IEEE 754 format, which is trivial enough in
 * Java.
 * @author dreish
 */
public class Flo implements DvtpExternalizable
   {
   public Flo( InputStream in ) throws IOException
      {
      super();
      readExternal( in );
      }

   @SuppressWarnings("unused")
   private Flo()
      {  super();  }

   public Flo( float f )  { mF = f; }
   
   public float asFloat()  {  return mF;  }

   public int getClassNumber()
      {  return 15;  }

   @Override
   public boolean equals( Object o )
      {
      return ( o.getClass().equals( this.getClass() )
               &&  ((Flo) o).mF == mF );
      }

   @Override
   public int hashCode()
      {
      return ((Float) mF).hashCode();
      }

   public static float externalAsFloat( InputStream in )
   throws IOException
      {
      byte[] float_buf = new byte[ 4 ];
      if ( in.read( float_buf ) != 4 )
         throw new IOException( "Could not read full float" );
      int i =   (float_buf[ 0 ] & 255)
              | (float_buf[ 1 ] & 255) << 8
              | (float_buf[ 2 ] & 255) << 16
              | (float_buf[ 3 ] & 255) << 24;
      return Float.intBitsToFloat( i );
      }

   public static void floatAsExternal( OutputStream out, float f )
   throws IOException
      {
      int i = Float.floatToIntBits( f );
      byte[] float_buf = new byte[] { (byte) i,
                                      (byte) (i >> 8),
                                      (byte) (i >> 16),
                                      (byte) (i >> 24) };
      out.write( float_buf );
      }

   private void readExternal( InputStream in ) throws IOException
      {  mF = externalAsFloat( in );  }

   public void writeExternal( OutputStream out ) throws IOException
      {  floatAsExternal( out, mF );  }

   public String prettyPrint()
      {
      return "(Flo " + mF + ")";
      }

   private float mF;
   }

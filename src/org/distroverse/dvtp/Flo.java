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
   public Flo()           { mF = 0; }
   public Flo( float f )  { mF = f; }
   
   public float asFloat()  {  return mF;  }
   
   public int getClassNumber()
      {  return 15;  }

   public static float externalAsFloat( InputStream in ) 
   throws IOException
      {
      byte[] float_buf = new byte[ 4 ];
      if ( in.read( float_buf ) != 4 )
         throw new IOException( "Could not read full float" );
      int i =   float_buf[ 0 ]
              + float_buf[ 1 ] << 8
              + float_buf[ 2 ] << 16
              + float_buf[ 3 ] << 24;
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
   
   public void readExternal( InputStream in ) throws IOException
      {  mF = externalAsFloat( in );  }

   public void writeExternal( OutputStream out ) throws IOException
      {  floatAsExternal( out, mF );  }

   public String prettyPrint()
      {
      return "(Flo " + mF + ")";
      }

   private float mF;
   }

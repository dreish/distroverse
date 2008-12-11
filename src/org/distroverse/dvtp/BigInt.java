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
import java.math.BigInteger;
import java.util.Random;

import org.distroverse.core.Util;

//immutable

/**
 * This class adds externalization to BigInteger.  The format is very
 * simple: a length/sign byte -- the &128 bit indicates sign, and &127
 * indicates the number of bytes that follow.  Unlike ULong, the
 * first byte is high.
 * @author dreish
 */
public class BigInt implements DvtpExternalizable,
                               Comparable< BigInt >
   {
   public static final BigInt ZERO = new BigInt( 0 );
   public static final BigInt ONE  = new BigInt( 1 );
   
   // My constructors.
   public BigInt( long longVal )
      {  mVal = BigInteger.valueOf( longVal );  }
   public BigInt( BigInteger bi )
      {  mVal = bi;  }
   public BigInt( InputStream in ) throws IOException
      {
      int length = in.read();
      int sign   = 1;
      if ( length == 0 )
         {
         mVal = BigInteger.ZERO;
         }
      else
         {
         if ( length < 0 )
            {
            length = -length;
            sign   = -1;
            }
         byte[] data = new byte[length];
         in.read( data );
         mVal = new BigInteger( sign, data ); 
         }
      }

   // Pass-through constructors from BigInteger.
   public BigInt( byte[] val )
      {  mVal = new BigInteger( val );  }
   public BigInt( String val )
      {  mVal = new BigInteger( val );  }
   public BigInt( int signum, byte[] magnitude )
      {  mVal = new BigInteger( signum, magnitude );  }
   public BigInt( String val, int radix )
      {  mVal = new BigInteger( val, radix );  }
   public BigInt( int numBits, Random rnd )
      {  mVal = new BigInteger( numBits, rnd );  }
   public BigInt( int bitLength, int certainty, Random rnd )
      {  mVal = new BigInteger( bitLength, certainty, rnd );  }
   
   // Basic get and set methods.
   public BigInteger get()  {  return mVal;  }
   
   /* Pass-through methods.  I'd love to inherit from MutableBigInteger,
    * but for some reason my Java is broken -- it claims there's no such
    * class.
    * (non-Javadoc)
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   public int compareTo( BigInt o )
      {  return mVal.compareTo( o.mVal );  }
   public int intValue()
      {  return mVal.intValue();  }

   /* (non-Javadoc)
    * @see java.io.Externalizable#writeExternal(java.io.OutputStream)
    */
   public void writeExternal( OutputStream out ) throws IOException
      {
      byte[] output;
      if ( mVal.signum() >= 0 )
         output = mVal.toByteArray();
      else
         output = BigInteger.ZERO.subtract( mVal ).toByteArray();
      
      out.write( Util.safeByte( output.length * mVal.signum() ) );
      out.write( output );
      }

   public int getClassNumber()
      {  return 3;  }

   public String prettyPrint()
      {
      return "(BigInt " + Util.prettyPrintList( mVal ) + ")";
      }

   private final BigInteger mVal;
   }

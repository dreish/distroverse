/**
 * 
 */
package org.distroverse.dvtp;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.math.BigInteger;
import java.util.Random;

/**
 * This class adds externalization to BigInteger.  The format is very
 * simple: a string of bytes, only the last of which has the high bit
 * (128) set.  The remaining 7 bits per byte make up the value of the
 * integer; the first seven are low.  Negative numbers are not defined.
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
    * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
    */
   public void readExternal( ObjectInput in ) throws IOException,
                                             ClassNotFoundException
      {
      int shift = 0;
      BigInteger newval = BigInteger.ZERO;
      while ( true )
         {
         byte b = in.readByte();
         BigInteger big_b = BigInteger.valueOf( b );
         newval = newval.or( big_b.shiftLeft( shift ) );
         shift += 7;
         if ( (b & 128) == 128 )
            {
            mVal = newval;
            return;
            }
         }
      }

   /* (non-Javadoc)
    * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
    */
   public void writeExternal( ObjectOutput out ) throws IOException
      {
      // TODO Auto-generated method stub

      }

   public BigInt getClassNumber()
      {  return new BigInt( 0 );  }

   BigInteger mVal;
   }

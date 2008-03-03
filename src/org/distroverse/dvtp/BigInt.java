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
 * integer; the first seven are high.
 * @author dreish
 */
public class BigInt extends BigInteger implements DvtpExternalizable
   {

   /**
    * @param val
    */
   public BigInt( byte[] val )
      {
      super( val );
      }

   /**
    * @param val
    */
   public BigInt( String val )
      {
      super( val );
      }

   /**
    * @param signum
    * @param magnitude
    */
   public BigInt( int signum, byte[] magnitude )
      {
      super( signum, magnitude );
      }

   /**
    * @param val
    * @param radix
    */
   public BigInt( String val, int radix )
      {
      super( val, radix );
      }

   /**
    * @param numBits
    * @param rnd
    */
   public BigInt( int numBits, Random rnd )
      {
      super( numBits, rnd );
      }

   /**
    * @param bitLength
    * @param certainty
    * @param rnd
    */
   public BigInt( int bitLength, int certainty, Random rnd )
      {
      super( bitLength, certainty, rnd );
      }

   /* (non-Javadoc)
    * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
    */
   public void readExternal( ObjectInput in ) throws IOException,
                                             ClassNotFoundException
      {
      // TODO Auto-generated method stub
      
      }

   /* (non-Javadoc)
    * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
    */
   public void writeExternal( ObjectOutput out ) throws IOException
      {
      // TODO Auto-generated method stub

      }

   public BigInt getClassNumber()
      {  return (BigInt) BigInteger.ZERO;  }

   }

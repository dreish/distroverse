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
 * Compact fractional part class. Range is 0.0 to just less than 1. The
 * number is any nonnegative long divided by the next highest power of
 * 128. Externalization breaks the number into septets, which are packed
 * into the low bits of a series of octets, lowest septet first, the
 * high (sign) bit in each octet being used as a flag to indicate that
 * the number has been completely streamed. An unset high bit on the
 * seventh octet is an error.
 *
 * @author dreish
 */
public final class Frac implements DvtpExternalizable
   {
   /**
    * Default constructor: value is zero.
    */
   public Frac()
      {
      mNumerator       = 0;
      mDenominatorBits = 7;
      }

   /**
    * Constructor with initial numerator and denomionator (specified as
    * a number of bits, between 1 and 63).
    * @param val - initial value
    */
   public Frac( long numerator, int denominator_bits )
      {
      init( numerator, denominator_bits );
      }

   private void init( long numerator, int denominator_bits )
      {
      if ( numerator < 0 )
         throw new IllegalArgumentException(
                                           "Frac must be nonnegative" );
      if ( denominator_bits < 1 || denominator_bits > 63 )
         throw new IllegalArgumentException(
                          "denominator_bits must be between 1 and 63" );
      if ( numerator >> denominator_bits > 0 )
         throw new IllegalArgumentException(
                     "numerator must be less than 2^denominator_bits" );
      mNumerator       = numerator;
      mDenominatorBits = denominator_bits;
      if ( denominator_bits % 7 != 0 )
         {
         mNumerator       <<= 7 - (denominator_bits % 7);
         mDenominatorBits +=  7 - (denominator_bits % 7);
         }
      }

   public Frac( double x, int denominator_bits )
      {
      if ( x >= 1.0 || x < 0.0 )
         throw new IllegalArgumentException( "double out of range in"
                                             + " Frac constructor" );
      long numerator
         = (long) (x * (1L << (denominator_bits - 1)));
      init( numerator, denominator_bits );
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   public int getClassNumber()
      {  return 32;  }

   public float toFloat()
      {
      return mNumerator
             / ((float) Math.pow( 2.0, mDenominatorBits ));
      }

   public double toDouble()
      {
      return mNumerator / Math.pow( 2.0, mDenominatorBits );
      }

   @Override
   public boolean equals( Object o )
      {
      return (o instanceof Frac
              &&  ((Frac) o).mNumerator       == mNumerator
              &&  ((Frac) o).mDenominatorBits == mDenominatorBits );
      }

   @Override
   public int hashCode()
      {
      return ((Long) mNumerator).hashCode()
             ^ Frac.class.hashCode() * mDenominatorBits;
      }

   /* (non-Javadoc)
    * @see java.io.Externalizable#readExternal(java.io.InputStream)
    */
   public void readExternal( InputStream in ) throws IOException
      {
      mDenominatorBits = 0;

      while ( true )
         {
         byte b = (byte) in.read();
         mNumerator |= (((long) (b & 127)) << mDenominatorBits);
         mDenominatorBits += 7;
         if ( (b & 128) == 128 )
            return;
         if ( mDenominatorBits == 63 )
            throw new IOException( "Malformed Frac in input" );
         }
      }

   /* (non-Javadoc)
    * @see java.io.Externalizable#writeExternal(java.io.OutputStream)
    */
   public void writeExternal( OutputStream out ) throws IOException
      {
      long val = mNumerator;
      int shift = 0;
      long mask = 0x7F;

      while ( shift < mDenominatorBits )
         {
         long bits = val & mask;
         byte b = (byte) (bits >> shift);
         val ^= bits;
         shift += 7;
         if ( shift == mDenominatorBits )
            b |= 0x80;
         else
            mask <<= 7;
         out.write( b );
         }
      }

   public String prettyPrint()
      {
      return "(Frac " + mNumerator + " " + mDenominatorBits + ")";
      }

   private long mNumerator;
   private int  mDenominatorBits;
   }

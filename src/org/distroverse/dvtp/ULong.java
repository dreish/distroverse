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

//immutable

/**
 * Compact ulong (unsigned long) class.  Range is 0 to 2^63-1, so this
 * is compatible with a Java long.  Externalization breaks the number
 * into septets, which are packed into the low bits of a series of
 * octets, lowest septet first, the high (sign) bit in each octet being
 * used as a flag to indicate that the number has been completely
 * streamed.  An unset high bit on the seventh octet is an error.
 *
 * A future upgrade path for DVTP will involve first giving clients the
 * capability to accept larger ULongs, and then deploying proxies that
 * use this capability.
 * @author dreish
 */
public final class ULong implements DvtpExternalizable
   {
   public static final long MAX_VALUE = Long.MAX_VALUE;
   public static final long MIN_VALUE = 0;

   /**
    * Default constructor: value is zero.
    */
   public ULong( InputStream in ) throws IOException
      {
      super();
      readExternal( in );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings( "unused" )
   private ULong()
      {
      mVal = 0;
      }

   /**
    * Constructor with initial value.
    * @param val - initial value
    */
   public ULong( long val )
      {
      if ( val < 0 )
         throw new IllegalArgumentException(
                           "ULong must be nonnegative" );
      mVal = val;
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   public int getClassNumber()
      {  return 0;  }

   public long toLong()
      {  return mVal;  }

   @Override
   public boolean equals( Object o )
      {
      return (o instanceof ULong
              &&  ((ULong) o).mVal == mVal);
      }

   @Override
   public int hashCode()
      {
      return ((Long) mVal).hashCode() ^ ULong.class.hashCode();
      }

   public static long externalAsLong( InputStream in )
   throws IOException
      {
      int shift = 0;
      long ret = 0;

      while ( true )
         {
         byte b = (byte) in.read();
         ret |= (((long) (b & 127)) << shift);
         if ( (b & 128) == 128 )
            return ret;
         shift += 7;
         if ( shift == 63 )
            throw new IOException( "Malformed ULong in input" );
         }
      }

   public static void longAsExternal( OutputStream out, long l )
   throws IOException
      {
      long val = l;
      int shift = 0;
      long mask = 0x7F;

      if ( val < 0 )
         throw new IllegalArgumentException(
                           "ULong must be nonnegative" );
      if ( val == 0 )
         out.write( (byte) -128 );

      while ( val != 0 )
         {
         long bits = val & mask;
         byte b = (byte) (bits >> shift);
         val ^= bits;
         if ( val == 0 )
            b |= 0x80;
         else
            {
            mask <<= 7;
            shift += 7;
            }
         out.write( b );
         }
      }

   /* (non-Javadoc)
    * @see java.io.Externalizable#readExternal(java.io.InputStream)
    */
   private void readExternal( InputStream in ) throws IOException
      {
      mVal = externalAsLong( in );
      }

   /* (non-Javadoc)
    * @see java.io.Externalizable#writeExternal(java.io.OutputStream)
    */
   public void writeExternal( OutputStream out ) throws IOException
      {
      longAsExternal( out, mVal );
      }

   public String prettyPrint()
      {
      return "(ULong " + mVal + ")";
      }

   long mVal;
   }

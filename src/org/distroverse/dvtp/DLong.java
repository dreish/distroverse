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
 * Compact long (signed long) class.  Range is -2^63 to 2^63-1, so this
 * is compatible with a Java long.  Externalization breaks the number
 * into an initial sextet followed by septets, which are packed into the
 * low bits of a series of octets, lowest bits first, the high (sign)
 * bit in each octet being used as a flag to indicate that the number
 * has been completely streamed.  The second highest bit in the first
 * octet indicates the sign of the number.  If it is set, the value of
 * the DLong is -1 minus the value of the remaining bits decoded in the
 * same manner as a ULong.  An unset high bit on the eighth octet is an
 * error, as is an eighth octet with any bit other than the highest or
 * lowest set.  (In other words, any DLong with an eighth octet with a
 * value other than 0x81 is malformed.)
 *
 * A future upgrade path for DVTP will involve first giving clients the
 * capability to accept larger DLongs, and then deploying proxies that
 * use this capability.
 * @author dreish
 */
public final class DLong implements DvtpExternalizable
   {
   public static final long MAX_VALUE = Long.MAX_VALUE;
   public static final long MIN_VALUE = Long.MIN_VALUE;

   /**
    * Default constructor: value is zero.
    */
   public DLong( InputStream in ) throws IOException
      {
      super();
      mVal = externalAsLong( in );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings( "unused" )
   private DLong()
      {
      mVal = 0;
      }

   /**
    * Constructor with initial value.
    * @param val - initial value
    */
   public DLong( long val )
      {
      mVal = val;
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   public int getClassNumber()
      {  return 31;  }

   public long toLong()
      {  return mVal;  }

   @Override
   public boolean equals( Object o )
      {
      return (o instanceof DLong
              &&  ((DLong) o).mVal == mVal);
      }

   @Override
   public int hashCode()
      {
      return ((Long) mVal).hashCode() ^ DLong.class.hashCode();
      }

   public static long externalAsLong( InputStream in )
   throws IOException
      {
      int shift = 0;
      long ret = 0;
      boolean negative = false;

      byte b = (byte) in.read();
      ret = (b & 63);
      if ( (b & 64) == 64 )
         negative = true;
      if ( (b & 128) == 128 )
         return (negative ? (-1L - ret) : ret);
      shift = 6;

      while ( true )
         {
         b = (byte) in.read();
         if ( shift == 62  &&  b != (byte) 0x81 )
            throw new IOException( "Malformed DLong in input" );
         ret |= (((long) (b & 127)) << shift);
         if ( (b & 128) == 128 )
            return (negative ? (-1L - ret) : ret);
         shift += 7;
         }
      }

   public static void longAsExternal( OutputStream out, long l )
   throws IOException
      {
      long val = l;
      int shift = 0;
      long mask = 0x3F;

      long bits = 0;
      if ( val < 0 )
         {
         bits = 0x40;
         val = -1L - val;
         }
      bits |= val & mask;
      byte b = (byte) bits;
      val ^= bits & mask;
      if ( val == 0 )
         b |= 0x80;
      else
         {
         mask = (0x7F << 6);
         shift = 6;
         }
      out.write( b );

      while ( val != 0 )
         {
         bits = val & mask;
         b = (byte) (bits >> shift);
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
    * @see java.io.Externalizable#writeExternal(java.io.OutputStream)
    */
   public void writeExternal( OutputStream out ) throws IOException
      {
      longAsExternal( out, mVal );
      }

   public String prettyPrint()
      {
      return "(DLong " + mVal + ")";
      }

   private final long mVal;
   }

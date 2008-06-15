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

/**
 * Compact ulong (unsigned long) class.  Range is 0 to 2^63, so this is
 * compatible with a Java long.
 * @author dreish
 */
public class CompactUlong implements DvtpExternalizable
   {
   /**
    * Default constructor: value is zero.
    */
   public CompactUlong()
      {
      mVal = 0;
      }
   
   /**
    * Constructor with initial value.
    * @param val - initial value
    */
   public CompactUlong( long val )
      {
      if ( val < 0 )
         throw new IllegalArgumentException( 
                           "CompactUlong must be nonnegative" );
      mVal = val;
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   public int getClassNumber()
      {  return 0;  }

   public long toLong()
      {  return mVal;  }

   public static long externalAsLong( ObjectInput in )
   throws IOException
      {
      int shift = 0;
      long ret = 0;
      
      while ( true )
         {
         byte b = in.readByte();
         ret |= (b << shift);
         if ( (b & 128) == 128 )
            return ret;
         shift += 7;
         if ( shift == 63 )
            throw new IOException( "Malformed CompactUlong in input" );
         }
      }
   
   public static void longAsExternal( ObjectOutput out, long l )
   throws IOException
      {
      long val = l;
      int shift = 0;
      long mask = 0x7F;

      if ( val < 0 )
         throw new IllegalArgumentException( 
                           "CompactUlong must be nonnegative" );
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
    * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
    */
   public void readExternal( ObjectInput in ) throws IOException
      {
      mVal = externalAsLong( in );
      }

   /* (non-Javadoc)
    * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
    */
   public void writeExternal( ObjectOutput out ) throws IOException
      {
      longAsExternal( out, mVal );
      }

   long mVal;
   }

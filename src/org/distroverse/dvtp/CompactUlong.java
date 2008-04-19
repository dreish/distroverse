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
    * 
    */
   public CompactUlong()
      {
      // TODO Auto-generated constructor stub
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   public int getClassNumber()
      {
      return 0;
      }
   
   public static long externalAsLong( ObjectInput in )
   throws IOException
      {
      int shift = 0;
      long ret = 0;
      while ( true )
         {
         byte b = in.readByte();
         ret |= (b << shift);
         shift += 7;
         if ( (b & 128) == 128 )
            return ret;
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
      // TODO Auto-generated method stub

      }

   long mVal;
   }

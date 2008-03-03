/**
 * 
 */
package org.distroverse.dvtp;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * @author dreish
 *
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
   public BigInt getClassNumber()
      {
      // TODO Auto-generated method stub
      return null;
      }

   /* (non-Javadoc)
    * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
    */
   public void readExternal( ObjectInput in ) throws IOException,
                                             ClassNotFoundException
      {
      int shift = 0;
      mVal = 0;
      while ( true )
         {
         byte b = in.readByte();
         mVal |= (b << shift);
         shift += 7;
         if ( (b & 128) == 128 )
            return;
         }

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

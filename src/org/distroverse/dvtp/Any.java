/**
 * 
 */
package org.distroverse.dvtp;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * This class is actually NOT part of DVTP, the protocol -- just dvtp,
 * the package.  The DvtpFlexiParser and DvtpFlexiStreamer will not send
 * or receive this class, but the ExtraFlexiParser and
 * ExtraFlexiStreamer will.
 * 
 * @author dreish
 */
public class Any implements DvtpExternalizable
   {

   /**
    * 
    */
   public Any()
      {
      // TODO Auto-generated constructor stub
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   public BigInt getClassNumber()
      {
      // TODO Auto-generated method stub
      return DvtpObject.getSerializedClassNumber();
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

   private Object mContents;
   }
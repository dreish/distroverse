/**
 * 
 */
package org.distroverse.dvtp;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.math.BigInteger;

/**
 * A pair of DvtpExternalizable objects.  They get externalized
 * together.
 * @author dreish
 */
public class Pair implements DvtpExternalizable
   {
   /**
    * 
    */
   public Pair()
      {  mFirst = mSecond = null;  }
   
   public Pair( DvtpExternalizable first,
                DvtpExternalizable second )
      {
      mFirst = first;
      mSecond = second;
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   public BigInt getClassNumber()
      {  return (BigInt) BigInteger.ONE;  }

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

   private DvtpExternalizable mFirst;
   private DvtpExternalizable mSecond;
   }

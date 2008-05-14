package org.distroverse.dvtp;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Defines movement and rotation in three dimensions:
 * - Movement
 *   - A polynomial degree (N)
 *   - N+1 vectors
 *   - A sinusoidal count (M)
 *   - M vectors
 *   - M period floats
 *   - M offset floats
 * - Rotation
 *   - A polynomial degree (N)
 *   - N+1 quaternions
 *   - A sinusoidal count (M)
 *   - M quaternions
 *   - M period floats
 *   - M offset floats
 * - A period
 * 
 * Time is measured in seconds, and vectors are in meters.
 * 
 * @author dreish
 */
public class Movement implements DvtpExternalizable
   {
   public int getClassNumber()
      {  return 13;  }
   public boolean isSendableByClient()
      {  return false;  }
   public boolean isSendableByProxy()
      {  return false;  }

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

   private int mDegree;
   }

package org.distroverse.dvtp;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class MoveObject extends MoveObject implements
                                          DvtpExternalizable
   {
   public int getClassNumber()
      {  return 14;  }
   public boolean isSendableByClient()
      {  return false;  }
   public boolean isSendableByProxy()
      {  return true;  }

   public void readExternal( ObjectInput in ) throws IOException,
                                             ClassNotFoundException
      {
      // TODO Auto-generated method stub

      }

   public void writeExternal( ObjectOutput out ) throws IOException
      {
      // TODO Auto-generated method stub

      }

   }

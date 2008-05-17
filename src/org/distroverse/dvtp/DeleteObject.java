package org.distroverse.dvtp;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class DeleteObject implements ProxySendable
   {
   public int getClassNumber()
      {  return 17;  }

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

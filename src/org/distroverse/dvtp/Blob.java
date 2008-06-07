package org.distroverse.dvtp;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Blob implements DvtpExternalizable
   {
   public Blob( byte[] bytes, int n_read, String resource,
                long pos, long file_length )
      {
      // TODO Auto-generated constructor stub
      }

   public Blob()
      {
      // TODO Auto-generated constructor stub
      }

   public int getClassNumber()
      {  return 26;  }

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

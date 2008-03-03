package org.distroverse.dvtp;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Str implements DvtpExternalizable
   {
   public Str( String s )
      {  mVal = s;  }

   public void readExternal( ObjectInput in ) throws IOException,
                                             ClassNotFoundException
      {
      // TODO Auto-generated method stub

      }

   public void writeExternal( ObjectOutput out ) throws IOException
      {
      // TODO Auto-generated method stub

      }

   String mVal;

   public BigInt getClassNumber()
      {
      // TODO Auto-generated method stub
      return null;
      }
   }

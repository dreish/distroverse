package org.distroverse.dvtp;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.jme.math.Vector3f;

public class Vec implements DvtpExternalizable
   {
   public Vec()
      {
      super();
      }

   public Vec( Vector3f v )
      {
      super();
      mVec = v;
      }

   public int getClassNumber()
      {  return 11;  }

   public void readExternal( ObjectInput in ) throws IOException
      {
      // TODO Auto-generated method stub

      }

   public void writeExternal( ObjectOutput out ) throws IOException
      {
      // TODO Auto-generated method stub

      }
   
   Vector3f mVec;
   }

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
      mVec = new Vector3f();
      }

   public Vec( Vector3f v )
      {
      super();
      mVec = v;
      }

   public int getClassNumber()
      {  return 11;  }

   public Vector3f asVector3f()
      {
      return mVec;
      }

   public void readExternal( ObjectInput in ) throws IOException
      {
      mVec.x = Flo.externalAsFloat( in );
      mVec.y = Flo.externalAsFloat( in );
      mVec.z = Flo.externalAsFloat( in );
      }

   public void writeExternal( ObjectOutput out ) throws IOException
      {
      Flo.floatAsExternal( out, mVec.x );
      Flo.floatAsExternal( out, mVec.y );
      Flo.floatAsExternal( out, mVec.z );
      }
   
   private Vector3f mVec;
   }

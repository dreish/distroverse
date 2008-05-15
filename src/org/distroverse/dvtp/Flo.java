package org.distroverse.dvtp;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * A float is just sent in IEEE 754 format, which is trivial enough in
 * Java.
 * @author dreish
 */
public class Flo implements DvtpExternalizable
   {
   public Flo()           { mF = 0; }
   public Flo( float f )  { mF = f; }
   
   public float asFloat()  {  return mF;  }
   
   public int getClassNumber()
      {  return 15;  }

   public static float externalAsFloat( ObjectInput in ) 
   throws IOException
      {  return in.readFloat();  }

   public static void floatAsExternal( ObjectOutput out, float f )
   throws IOException
      {  out.writeFloat( f );  }
   
   public void readExternal( ObjectInput in ) throws IOException
      {  mF = externalAsFloat( in );  }

   public void writeExternal( ObjectOutput out ) throws IOException
      {  floatAsExternal( out, mF );  }

   private float mF;
   }

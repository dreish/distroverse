package org.distroverse.dvtp;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.distroverse.core.Util;

public class Keystroke implements ClientSendable
   {
   public Keystroke()
      {
      super();
      mKeyNum = 0;
      }

   public Keystroke( int kn )
      {
      super();
      mKeyNum = kn;
      }

   public int getClassNumber()
      {  return 19;  }
   
   public int getKey()
      {  return mKeyNum;  }
   public void setKey( int kn )
      {  mKeyNum = kn;  }

   public void readExternal( ObjectInput in ) throws IOException
      {
      mKeyNum = Util.safeInt( CompactUlong.externalAsLong( in ) );
      }

   public void writeExternal( ObjectOutput out ) throws IOException
      {
      CompactUlong.longAsExternal( out, mKeyNum );
      }

   private int mKeyNum;
   }

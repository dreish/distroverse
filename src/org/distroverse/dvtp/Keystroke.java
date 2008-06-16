package org.distroverse.dvtp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

   public void readExternal( InputStream in ) throws IOException
      {
      mKeyNum = Util.safeInt( CompactUlong.externalAsLong( in ) );
      }

   public void writeExternal( OutputStream out ) throws IOException
      {
      CompactUlong.longAsExternal( out, mKeyNum );
      }

   public String prettyPrint()
      {
      return "(KeyStroke " + mKeyNum + ")";
      }

   private int mKeyNum;
   }

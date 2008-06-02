package org.distroverse.dvtp;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class DeleteObject implements ProxySendable
   {
   public int getClassNumber()
      {  return 17;  }

   public void readExternal( ObjectInput in ) throws IOException
      {
      mId = CompactUlong.externalAsLong( in );
      }

   public void writeExternal( ObjectOutput out ) throws IOException
      {
      CompactUlong.longAsExternal( out, mId );
      }

   public Long getId()
      {  return mId;  }

   private Long mId;
   }

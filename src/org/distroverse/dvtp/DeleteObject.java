package org.distroverse.dvtp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class DeleteObject implements ProxySendable
   {
   public int getClassNumber()
      {  return 17;  }

   public Long getId()
      {  return mId;  }

   public void readExternal( InputStream in ) throws IOException
      {
      mId = CompactUlong.externalAsLong( in );
      }

   public void writeExternal( OutputStream out ) throws IOException
      {
      CompactUlong.longAsExternal( out, mId );
      }

   public String prettyPrint()
      {
      return "(DeleteObject " + mId + ")";
      }

   private Long mId;
   }

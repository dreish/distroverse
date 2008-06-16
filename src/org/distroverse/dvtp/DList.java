package org.distroverse.dvtp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.distroverse.core.Util;

public class DList implements DvtpExternalizable
   {
   public DList()
      {
      super();
      mContents = null;
      }
   
   public DList( DvtpExternalizable[] f )
      {
      super();
      mContents = f;
      }
   
   public int getClassNumber()
      {  return 128;  }
   /**
    * Returns a mutable array, so this can be used for fetching and
    * setting values.
    * @return
    */
   public DvtpExternalizable[] getContents()
      {  return mContents;  }

   public void readExternal( InputStream in ) throws IOException,
                                             ClassNotFoundException
      {
      int length = Util.safeInt( CompactUlong.externalAsLong( in ) );
      mContents = new DvtpExternalizable[ length ];
      for ( int i = 0; i < length; ++i )
         mContents[ i ] = DvtpObject.parseObject( in );
      }

   public void writeExternal( OutputStream out ) throws IOException
      {
      CompactUlong.longAsExternal( out, mContents.length );
      for ( DvtpExternalizable o : mContents )
         DvtpObject.writeInnerObject( out, o );
      }
   
   public String prettyPrint()
      {
      return "(DList " 
             + Util.prettyPrintList( (Object[]) mContents ) + ")";
      }

   private DvtpExternalizable[] mContents;
   }

package org.distroverse.dvtp;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.distroverse.core.Util;

public class DList implements DvtpExternalizable
   {
   public DList()
      {
      super();
      mFunction = null;
      }
   
   public DList( DvtpExternalizable[] f )
      {
      super();
      mFunction = f;
      }
   
   public int getClassNumber()
      {  return 128;  }
   /**
    * Returns a mutable array, so this can be used for fetching and
    * setting values.
    * @return
    */
   public DvtpExternalizable[] getFunction()
      {  return mFunction;  }

   public void readExternal( ObjectInput in ) throws IOException,
                                             ClassNotFoundException
      {
      int length = Util.safeInt( CompactUlong.externalAsLong( in ) );
      mFunction = new DvtpExternalizable[ length ];
      for ( int i = 0; i < length; ++i )
         mFunction[ i ] = DvtpObject.parseObject( in );
      }

   public void writeExternal( ObjectOutput out ) throws IOException
      {
      CompactUlong.longAsExternal( out, mFunction.length );
      for ( DvtpExternalizable o : mFunction )
         DvtpObject.writeInnerObject( out, o );
      }

   private DvtpExternalizable[] mFunction;
   }
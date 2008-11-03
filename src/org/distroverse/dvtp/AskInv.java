/**
 *
 */
package org.distroverse.dvtp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.distroverse.core.Util;

/**
 * @author dreish
 *
 */
public class AskInv implements ProxySendable
   {
   /**
    *
    */
   public AskInv()
      {
      mType = null;
      mKey  = null;
      }

   public AskInv( String t, DvtpExternalizable k )
      {
      mType = t;
      mKey  = k;
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   public int getClassNumber()
      {  return 134;  }

   @Override
   public boolean equals( Object o )
      {
      if ( o.getClass().equals( getClass() ) )
         {
         return ((AskInv) o).mType.equals( mType )
                && ((AskInv) o).mKey.equals( mKey );
         }
      return false;
      }

   @Override
   public int hashCode()
      {
      return mType.hashCode()
             ^ mKey.hashCode()
             ^ getClass().hashCode();
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#prettyPrint()
    */
   public String prettyPrint()
      {
      return "(AskInv "
             + Util.prettyPrintList( mType, mKey )
             + ")";
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#readExternal(java.io.InputStream)
    */
   public void readExternal( InputStream in ) throws IOException,
                                             ClassNotFoundException
      {
      mType = Str.externalAsString( in );
      mKey  = DvtpObject.parseObject( in );
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#writeExternal(java.io.OutputStream)
    */
   public void writeExternal( OutputStream out ) throws IOException
      {
      Str.stringAsExternal( out, mType );
      DvtpObject.writeInnerObject( out, mKey );
      }

   String             mType;
   DvtpExternalizable mKey;
   }

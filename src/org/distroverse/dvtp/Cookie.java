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
public class Cookie implements ClientSendable
   {
   /**
    *
    */
   public Cookie()
      {
      mKey    = null;
      mExists = false;
      mValue  = null;
      }

   public Cookie( DvtpExternalizable key )
      {
      mKey    = key;
      mExists = false;
      mValue  = new False();
      }

   public Cookie( DvtpExternalizable key, DvtpExternalizable value )
      {
      mKey    = key;
      mExists = true;
      mValue  = value;
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   public int getClassNumber()
      {  return 27;  }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#prettyPrint()
    */
   public String prettyPrint()
      {
      return "(" + getClass().getSimpleName() + " "
             + (mExists ? Util.prettyPrintList( mKey, mExists, mValue )
                        : Util.prettyPrintList( mKey, mExists ))
             + ")";
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#readExternal(java.io.InputStream)
    */
   public void readExternal( InputStream in ) throws IOException,
                                             ClassNotFoundException
      {
      mKey    = DvtpObject.parseObject( in );
      mExists = Bool.externalAsBoolean( in );
      if ( mExists )
         mValue  = DvtpObject.parseObject( in );
      else
         mValue  = new False();
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#writeExternal(java.io.OutputStream)
    */
   public void writeExternal( OutputStream out ) throws IOException
      {
      DvtpObject.writeInnerObject( out, mKey );
      Bool.booleanAsExternal( out, mExists );
      if ( mExists )
         DvtpObject.writeInnerObject( out, mValue );
      }

   @Override
   public boolean equals( Object o )
      {
      if ( o.getClass().equals( getClass() ) )
         {
         Cookie c = (Cookie) o;
         return (   c.mKey.equals( mKey )
                 && c.mExists == mExists
                 && c.mValue.equals( mValue ));
         }
      return false;
      }

   @Override
   public int hashCode()
      {
      return (this.getClass().hashCode()
              ^ mKey.hashCode()
              ^ mValue.hashCode()) * (mExists ? 13 : 17);
      }

   private DvtpExternalizable mKey;
   private boolean            mExists;
   private DvtpExternalizable mValue;
   }

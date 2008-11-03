/**
 *
 */
package org.distroverse.dvtp;


/**
 * @author dreish
 *
 */
public class ReplyInv extends Cookie
   {
   /**
    *
    */
   public ReplyInv()
      {
      super();
      }

   public ReplyInv( DvtpExternalizable key )
      {
      super( key );
      }

   public ReplyInv( DvtpExternalizable key, DvtpExternalizable value )
      {
      super( key, value );
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   @Override
   public int getClassNumber()
      {  return 135;  }
   }

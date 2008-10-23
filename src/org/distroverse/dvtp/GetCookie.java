/**
 *
 */
package org.distroverse.dvtp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author dreish
 *
 */
public class GetCookie implements DvtpExternalizable
   {

   /**
    *
    */
   public GetCookie()
      {
      // TODO Auto-generated constructor stub
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   public int getClassNumber()
      {  return 26;  }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#prettyPrint()
    */
   public String prettyPrint()
      {
      // TODO Auto-generated method stub
      return null;
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#readExternal(java.io.InputStream)
    */
   public void readExternal( InputStream in ) throws IOException,
                                             ClassNotFoundException
      {
      // TODO Auto-generated method stub

      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#writeExternal(java.io.OutputStream)
    */
   public void writeExternal( OutputStream out ) throws IOException
      {
      // TODO Auto-generated method stub

      }

   }

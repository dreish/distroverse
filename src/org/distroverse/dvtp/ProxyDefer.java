/**
 * 
 */
package org.distroverse.dvtp;

/**
 * This trivial subclass of Str specifies the name of a site that should
 * be consulted instead of this one, in response to a LOCATION query.
 * It should contain a valid hostname -- nothing more.
 * @author dreish
 */
public class ProxyDefer extends Str
   {
   public ProxyDefer()
      {  super();  }
   public ProxyDefer( String hostname )
      {  super( hostname );  }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   @Override
   public int getClassNumber()
      {  return 8;  }

   /**
    * @return - the name of the server to query instead of this one
    */
   public String getHostname()
      {  return toString();  }
   }

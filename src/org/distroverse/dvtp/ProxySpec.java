package org.distroverse.dvtp;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * This class is one of two valid responses to a LOCATION query, the
 * other being ProxyDefer.  It gives the URL of a proxy to use to
 * connect to this site at the requested location, a Bool that specifies
 * whether the same proxy should be used for all location URIs with
 * resource names matching the resource regular expression and machine
 * names matching *.<this-machine-name> (otherwise, the same proxy
 * instance will only be used for location URIs with resource names
 * matching the resource regular expression and with the same machine
 * name as the one being connected to), and a resource regular
 * expression.
 * 
 * Note that it is almost certainly a mistake for the Bool to be false
 * if the location URI being responded to names a machine other than the
 * one being connected to.
 * @author dreish
 */
public class ProxySpec implements DvtpExternalizable
   {
   public ProxySpec()
      {
      mCoversSubdomains = null;
      mProxyUrl = mResourceRegexp = null;
      }
   
   public ProxySpec( Str proxy_url, Bool covers_subdomains, 
                     Str resource_regexp )
      {
      mProxyUrl = proxy_url;
      mCoversSubdomains = covers_subdomains;
      mResourceRegexp = resource_regexp;
      }
   
   public ProxySpec( String proxy_url, boolean covers_subdomains, 
                     String resource_regexp )
      {
      mProxyUrl = new Str( proxy_url );
      mCoversSubdomains = Bool.newInstance( covers_subdomains );
      mResourceRegexp = new Str( resource_regexp );
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   public int getClassNumber()
      {  return 7;  }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#isSendableByProxy()
    */
   public boolean isSendableByProxy()
      {  return false;  }

   /* (non-Javadoc)
    * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
    */
   public void readExternal( ObjectInput in ) 
   throws IOException, ClassNotFoundException, ClassCastException
      {
      (mProxyUrl = new Str()).readExternal( in );
      mCoversSubdomains = (Bool) DvtpObject.parseObject( in );
      (mResourceRegexp = new Str()).readExternal( in );
      }

   /* (non-Javadoc)
    * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
    */
   public void writeExternal( ObjectOutput out ) throws IOException
      {
      mProxyUrl.writeExternal( out );
      DvtpObject.writeObject( out, mCoversSubdomains );
      mResourceRegexp.writeExternal( out );
      }
   
   public Str  getProxyUrl()  {  return mProxyUrl;  }
   public Bool getCoversSubdomains()  {  return mCoversSubdomains;  }
   public Str  getResourceRegexp()  {  return mResourceRegexp;  }

   private Str  mProxyUrl;
   private Bool mCoversSubdomains;
   private Str  mResourceRegexp;
   }

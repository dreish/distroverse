/**
 * 
 */
package org.distroverse.viewer;

import org.distroverse.core.Util.Pair;

/**
 * @author dreish
 *
 */
public class ProxyClientConnection
   {
   /**
    * Loads a proxy and creates a connection to it.
    * @param url - The Location URL
    * @param proxy_url - The Proxy resource URL
    * @param location_regexp 
    */
   public ProxyClientConnection( String url, String proxy_url, 
                                 String location_regexp )
      {
      // TODO Call out some common init function to be shared with the
      // other constructor
      }
   
   public ProxyClientConnection( String url,
                                 Pair< String, String > proxy_info )
      {
      // TODO
      }

   public void setUrl( String url )
      {
      // TODO No real proxies yet, so nothing to do here.
      }
   
   /**
    * Checks the given URL against the regexp returned by the server
    * when this proxy URL was specified.
    * @param url
    * @return
    */
   public boolean handlesUrl( String url )
      {
      if ( mLocationRegexp == null )
         return false;
      return url.matches( mLocationRegexp );
      }

   public void close()
      {
      // TODO Auto-generated method stub
      
      }

   public String getProxyUrl()
      {
      return mUrl;
      }
   
   private String mUrl;
   private String mLocationRegexp;
   }

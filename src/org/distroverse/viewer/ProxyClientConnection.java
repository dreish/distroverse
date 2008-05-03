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
    * @param location_regexp - Matches what Locations the proxy handles
    */
   public ProxyClientConnection( String url, String proxy_url, 
                                 String location_regexp )
      {
      init( url, proxy_url, location_regexp );
      }
   
   /**
    * Loads a proxy and creates a connection to it
    * @param url - The Location URL
    * @param proxy_info - a (proxy_url, location_regexp) Pair
    */
   public ProxyClientConnection( String url,
                                 Pair< String, String > proxy_info )
      {
      init( url, proxy_info.a, proxy_info.b );
      }
   
   private void init( String url, String proxy_url,
                      String location_regexp )
      {
      mLocationRegexp = location_regexp;
      mUrl = url;
      getProxy( proxy_url );
      }

   private void getProxy( String proxy_url )
      {
      // TODO Auto-generated method stub
      
      }

   /**
    * Tells the proxy to change URLs.
    * @param url - New location URL
    * @param location_regexp - New regexp, or null to leave unchanged
    */
   public void setUrl( String url, String location_regexp )
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

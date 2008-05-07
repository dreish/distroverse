/**
 * 
 */
package org.distroverse.viewer;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

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
    * @throws MalformedURLException 
    * @throws ClassNotFoundException 
    */
   public ProxyClientConnection( String url, String proxy_url, 
                                 String location_regexp )
   throws Exception
      {
      init( url, proxy_url, location_regexp );
      }
   
   /**
    * Loads a proxy and creates a connection to it
    * @param url - The Location URL
    * @param proxy_info - a (proxy_url, location_regexp) Pair
    * @throws MalformedURLException 
    * @throws ClassNotFoundException
    */
   public ProxyClientConnection( String url,
                                 Pair< String, String > proxy_info )
   throws Exception
      {
      init( url, proxy_info.a, proxy_info.b );
      }
   
   private void init( String url, String proxy_url,
                      String location_regexp )
   throws Exception
      {
      mLocationRegexp = location_regexp;
      mUrl = url;
      runProxy( proxy_url );
      }

   private void runProxy( String proxy_url )
   throws Exception
      {
      String cache_url
         = ResourceCache.internalizeResourceUrl( proxy_url );
      URL[] urls = { new URL( cache_url ) };
      URLClassLoader loader = new URLClassLoader( urls );
      // XXX I have a feeling this will only work once:
      Class< ? > proxy = loader.loadClass( "Proxy" );
      // FIXME Add a SecurityManager to prevent proxy from doing stuff
      Object proxy_instance = proxy.newInstance();
      Method m = proxy.getMethod( "runMe" );
      String ret = (String) m.invoke( proxy_instance );

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

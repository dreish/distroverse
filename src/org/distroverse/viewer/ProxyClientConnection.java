/**
 * 
 */
package org.distroverse.viewer;

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
    */
   public ProxyClientConnection( String url, String proxy_url )
      {
      
      // TODO Probably want to create a thread here or something.
      // And a flag!  And a constant!  And a variable!
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
      if ( mHandledUrlRegexp == null )
         return false;
      return url.matches( mHandledUrlRegexp );
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
   private String mHandledUrlRegexp;
   }

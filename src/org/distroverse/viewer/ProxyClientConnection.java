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
      // TODO Probably want to create a thread here. 
      }
   
   public void setUrl( String url )
      {
      // TODO No real proxies yet, so nothing to do here.
      }
   
   public boolean handlesUrl( String url )
      {
      
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
   }

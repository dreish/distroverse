/**
 * 
 */
package org.distroverse.viewer;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.distroverse.core.Util.Pair;
import org.distroverse.dvtp.DvtpExternalizable;
import org.distroverse.dvtp.DvtpProxy;

/**
 * @author dreish
 *
 */
public class ProxyClientConnection implements Runnable
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
      mQueue = new LinkedBlockingQueue< DvtpExternalizable >();
      runProxy( proxy_url );
      mListener = newListener();
      }
   
   private Thread newListener()
      {
      Thread ret = new Thread( this );
      ret.start();
      return ret;
      }
   
   /**
    * Running a ProxyClientConnection causes it to listen to its queue
    * for objects, and to act on them.  run() does not return unless
    * interrupted.
    */
   public void run()
      {
      try
         {
         while ( true )
            {
            DvtpExternalizable o = mQueue.take();
            if ( o.isSendableByProxy() )
               // XXX - Dispatch the object
            }
         }
      catch ( InterruptedException e )
         {  /* Fall through. */  }
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
      final DvtpProxy proxy_instance = (DvtpProxy) proxy.newInstance();
      proxy_instance.setQueue( mQueue );
      Thread t = new Thread( new Runnable()
         {
         public void run() { proxy_instance.run(); }
         } );
      t.start();
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
      mListener.interrupt();
      }

   public String getProxyUrl()
      {
      return mUrl;
      }
   
   private String mUrl;
   private String mLocationRegexp;
   private BlockingQueue< DvtpExternalizable > mQueue;
   private Thread mListener;
   }

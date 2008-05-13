/**
 * 
 */
package org.distroverse.viewer;

import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.distroverse.core.Log;
import org.distroverse.core.Util.Pair;
import org.distroverse.dvtp.DvtpExternalizable;
import org.distroverse.dvtp.DvtpProxy;
import org.distroverse.dvtp.SetUrl;

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
                                 String location_regexp,
                                 ViewerWindow window )
   throws Exception
      {
      init( url, proxy_url, location_regexp, window );
      }
   
   /**
    * Loads a proxy and creates a connection to it
    * @param url - The Location URL
    * @param proxy_info - a (proxy_url, location_regexp) Pair
    * @throws MalformedURLException 
    * @throws ClassNotFoundException
    */
   public ProxyClientConnection( String url,
                                 Pair< String, String > proxy_info,
                                 ViewerWindow window )
   throws Exception
      {
      init( url, proxy_info.a, proxy_info.b, window );
      }
   
   private void init( String url, String proxy_url,
                      String location_regexp, ViewerWindow window )
   throws Exception
      {
      mLocationRegexp = location_regexp;
      mUrl = url;
      mQueue = new LinkedBlockingQueue< DvtpExternalizable >();
      runProxy( proxy_url );
      mListener = newListener();
      mDispatcher = new ViewerDispatcher( window );
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
               {
               try
                  {
                  mDispatcher.dispatchObject( o );
                  }
               catch ( ProtocolException e )
                  {
                  // TODO probably want to tell the user graphically
                  Log.p( "Proxy violated DVTP protocol:", Log.DVTP, 0 );
                  Log.p( e, Log.DVTP, 0 );
                  }
               catch ( ProxyErrorException e )
                  {
                  // TODO probably want to tell the user graphically
                  Log.p( "Proxy did something erroneous:", 
                         Log.PROXY, 0 );
                  Log.p( e, Log.PROXY, 0 );
                  }
               }
            else
               {
               // TODO probably want to tell the user graphically
               Log.p( "Proxy sent non-proxy object of class "
                      + o.getClass().getCanonicalName(), 0, 0 );
               }
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
      mProxyInstance = proxy_instance;
      proxy_instance.setQueue( mQueue );
      Thread t = new Thread( new Runnable()
         {
         public void run() { proxy_instance.run(); }
         } );
      t.start();
      }

   /**
    * Tells the proxy to change URLs.  This is used in response to a
    * RedirectUrl message when the site being redirected to is handled
    * by the same proxy, and is also called as part of a client-side
    * requestUrl().
    * @param url - New location URL
    * @param location_regexp - New regexp, or null to leave unchanged
    */
   public void setUrl( String url, String location_regexp )
      {
      offer( new SetUrl( url ) );
      if ( location_regexp != null )
         mLocationRegexp = location_regexp;
      }

   public void offer( DvtpExternalizable o )
      {
      mProxyInstance.offer( o );
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
   private ViewerDispatcher mDispatcher;
   private DvtpProxy mProxyInstance;
   }

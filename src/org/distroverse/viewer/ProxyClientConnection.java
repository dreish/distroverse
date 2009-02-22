/*
 * <copyleft>
 *
 * Copyright 2007-2009 Dan Reish
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or
 * combining it with Clojure (or a modified version of that program)
 * or clojure-contrib (or a modified version of that library),
 * containing parts covered by the terms of the Eclipse Public
 * License, the licensors of this Program grant you additional
 * permission to convey the resulting work. {Corresponding Source for
 * a non-source form of such a combination shall include the source
 * code for the parts of Clojure and clojure-contrib used as well as
 * that of the covered work.}
 *
 * </copyleft>
 */
/**
 *
 */
package org.distroverse.viewer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.distroverse.core.Log;
import org.distroverse.dvtp.ClientSendable;
import org.distroverse.dvtp.DvtpProxy;
import org.distroverse.dvtp.ProxySendable;
import org.distroverse.dvtp.ProxySpec;
import org.distroverse.dvtp.SetUrl;

/**
 * A ProxyClientConnection listens for objects from a proxy and
 * dispatches them.  It also handles messages that the client may need
 * to send to the proxy.
 *
 * @author dreish
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
                                 String proxy_name,
                                 String location_regexp,
                                 ProxyControllerPipeline pipeline )
   throws Exception
      {
      init( url, proxy_url, proxy_name, location_regexp, pipeline );
      }

   /**
    * Loads a proxy and creates a connection to it
    * @param url - The Location URL
    * @param proxy_spec - a (proxy_url, location_regexp) Pair
    * @throws MalformedURLException
    * @throws ClassNotFoundException
    */
   public ProxyClientConnection( String url,
                                 ProxySpec proxy_spec,
                                 ProxyControllerPipeline pipeline )
   throws Exception
      {
      init( url,
            proxy_spec.getProxyUrl().toString(),
            proxy_spec.getProxyName().toString(),
            proxy_spec.getResourceRegexp().toString(),
            pipeline );
      }

   private void init( String url, String proxy_url, String proxy_name,
                      String location_regexp, 
                      ProxyControllerPipeline pipeline )
   throws Exception
      {
      mLocationRegexp = location_regexp;
      mUrl = url;
      mName = proxy_name;
      mQueue = new LinkedBlockingQueue< ProxySendable >();
      runProxy( proxy_url, proxy_name );
      mListener = newListener();
//      mDispatcher = new ViewerDispatcher( pipeline, this );
      mPipeline = pipeline;
      setUrl( url, location_regexp );
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
            ProxySendable o = mQueue.take();
            try
               {
               mPipeline.dispatchObject( o );
               }
            catch ( ProxyErrorException e )
               {
               // TODO probably want to tell the user graphically
               Log.p( "Proxy did something erroneous:",
                      Log.PROXY, 0 );
               Log.p( e, Log.PROXY, 0 );
               }
            }
         }
      catch ( InterruptedException e )
         {
         Log.p( "ProxyClientConnection interrupted:",
                Log.CLIENT, -50 );
         Log.p( e, Log.CLIENT, -50 );
         /* Fall through. */
         }
      }

   private void runProxy( String proxy_url, String class_name )
   throws Exception
      {
      String cache_url
         = ResourceCache.internalizeResourceUrl( proxy_url );
      URL[] urls = { new URL( cache_url ) };
      URLClassLoader loader = new URLClassLoader( urls );
      // FIXME I have a feeling this will not work for different classes
      // with the same name:
      // XXX This does not seem to actually load classes from 'urls':
      Class< ? > proxy = loader.loadClass( class_name );

      // FIXME Add a SecurityManager to prevent proxy from doing stuff
      final DvtpProxy proxy_instance = (DvtpProxy) proxy.newInstance();
      mProxyInstance = proxy_instance;
      proxy_instance.setQueue( mQueue );

      Thread t = new Thread( new Runnable()
         {
         public void run()
            {
            proxy_instance.run();
            }
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
    * @throws IOException
    */
   public void setUrl( String url, String location_regexp )
   throws IOException
      {
      offer( new SetUrl( url ) );
      if ( location_regexp != null )
         mLocationRegexp = location_regexp;
      }

   public void offer( ClientSendable o ) throws IOException
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
      {  return mUrl;  }
   public String getProxyName()
      {  return mName;  }

   private String mUrl;
   private String mName;
   private String mLocationRegexp;
   private BlockingQueue< ProxySendable > mQueue;
   private Thread mListener;
   private DvtpProxy mProxyInstance;
   private ProxyControllerPipeline mPipeline;
   }

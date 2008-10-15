/*
 * Copyright (c) 2007-2008 Dan Reish.
 *
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * General Public License (GPL) version 3 or later</a>
 */
package org.distroverse.viewer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.distroverse.dvtp.ProxySendable;
import org.distroverse.dvtp.ProxySpec;
import org.distroverse.dvtp.SetUrl;

public class ProxyControllerPipeline extends ControllerPipeline
   {
   /**
    * Note that the 'proxy' passed to this constructor, which may be
    * null, is the one already loaded and running, if any.  If that
    * proxy can handle the given URL, it will be used.
    * @param url - the dvtp:// URL this pipeline should be connected to
    * @param window - the ViewerWindow on which to display
    * @param proxy - the already-running proxy, if any
    * @throws URISyntaxException
    * @throws IOException
    * @throws ClassNotFoundException
    */
   public ProxyControllerPipeline( String url, ViewerWindow window )
   throws URISyntaxException, IOException, ClassNotFoundException,
          Exception
      {
      mProxy = new ProxyClientConnection( url,
                             ProxyControllerPipeline.getProxyUrl( url ),
                                          this );
      mDispatcher = new ViewerDispatcher( window, this );
      requestUrl( url );
      }

   public static ProxySpec getProxyUrl( String url )
   throws URISyntaxException, IOException
      {
      URI place_uri = new URI( url );
      DvtpServerConnection dvtp_server
         = new DvtpServerConnection( place_uri );
      return dvtp_server.location( place_uri );
      }

   @Override
   public void close()
      {
      if ( mProxy != null )
         mProxy.close();
      }

   @Override
   public void requestUrl( String location_url )
   throws Exception
      {
      if ( mProxy.handlesUrl( location_url ) )
         mProxy.offer( new SetUrl( location_url ) );
      else
         redirectUrl( location_url );
      }

   private void redirectUrl( String location_url )
   throws Exception
      {
      ProxySpec proxy_info = getProxyUrl( location_url );
      String proxy_url  = proxy_info.getProxyUrl().toString();
      String proxy_name = proxy_info.getProxyName().toString();
      String loc_regexp = proxy_info.getResourceRegexp().toString();
      if ( mProxy != null
           &&  proxy_url  == mProxy.getProxyUrl()
           &&  proxy_name == mProxy.getProxyName() )
         mProxy.setUrl( location_url, loc_regexp );
      else
         {
         mProxy.close();
         mProxy = new ProxyClientConnection( location_url, proxy_url,
                                      proxy_name, loc_regexp, this );
         }
      }

   public void dispatchObject( ProxySendable o )
   throws ProxyErrorException
      {
      mDispatcher.dispatchObject( o );
      }

   private ProxyClientConnection mProxy;
   private ViewerDispatcher      mDispatcher;
   }

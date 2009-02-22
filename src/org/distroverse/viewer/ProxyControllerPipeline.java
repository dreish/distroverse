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

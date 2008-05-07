package org.distroverse.viewer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.distroverse.core.Util;

import com.jmex.game.StandardGame;

public class ProxyControllerPipeline extends ControllerPipeline
   {
   /**
    * Note that the 'proxy' passed to this constructor, which may be
    * null, is the one already loaded and running, if any.  If that
    * proxy can handle the given URL, it will be used.
    * @param url - the dvtp:// URL this pipeline should be connected to
    * @param game - the jME StandardGame
    * @param window - the ViewerWindow on which to display
    * @param proxy - the already-running proxy, if any
    * @throws URISyntaxException 
    * @throws IOException 
    * @throws ClassNotFoundException 
    */
   public ProxyControllerPipeline( String url, StandardGame game,
                                   ViewerWindow window,
                                   ProxyClientConnection proxy ) 
   throws URISyntaxException, IOException, ClassNotFoundException,
          Exception
      {
      mProxy = proxy;
      setUrl( url );
      }

   public static Util.Pair< String, String > getProxyUrl( String url ) 
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
      mProxy.close();
      }
   
   @Override
   public void setUrl( String location_url )
   throws Exception
      {
      Util.Pair< String, String > proxy_info =
         getProxyUrl( location_url );
      String proxy_url       = proxy_info.a;
      String location_regexp = proxy_info.b;
      if ( mProxy != null
           &&  proxy_url == mProxy.getProxyUrl() )
         mProxy.setUrl( location_url, location_regexp );
      else
         {
         mProxy.close();
         mProxy = new ProxyClientConnection( location_url, proxy_url,
                                             location_regexp );
         }
      }

   private ProxyClientConnection mProxy;
   }

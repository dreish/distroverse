package org.distroverse.viewer;

import java.net.URI;
import java.net.URISyntaxException;

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
    */
   public ProxyControllerPipeline( String url, StandardGame game,
                                   ViewerWindow window,
                                   ProxyClientConnection proxy ) 
   throws URISyntaxException
      {
      String proxy_url = GetProxyUrl( url );
      }

   private String GetProxyUrl( String url ) throws URISyntaxException
      {
      DvtpServerConnection dvtp_server
         = new DvtpServerConnection( new URI( url ) );
      
      return null;
      }

   @Override
   public void close()
      {
      // TODO Auto-generated method stub

      }

   private ProxyClientConnection mProxy;
   }

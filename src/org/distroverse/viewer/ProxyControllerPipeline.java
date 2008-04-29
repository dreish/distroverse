package org.distroverse.viewer;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;

import org.distroverse.dvtp.Str;

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
    */
   public ProxyControllerPipeline( String url, StandardGame game,
                                   ViewerWindow window,
                                   ProxyClientConnection proxy ) 
   throws URISyntaxException, IOException
      {
      mProxy = proxy;
      setUrl( url );
      }

   public static String getProxyUrl( String url ) 
   throws URISyntaxException, IOException
      {
      URI place_uri = new URI( url );
      DvtpServerConnection dvtp_server
         = new DvtpServerConnection( place_uri );
      Object response = dvtp_server.location( place_uri );
      dvtp_server.close();
      if ( response instanceof Str )
         return response.toString();
      if ( response instanceof String )
         return (String) response;
      
      throw new ProtocolException( "Server did not return a string "
                         + "in response to a LOCATION query" );
      }

   @Override
   public void close()
      {
      // TODO Auto-generated method stub

      }
   
   @Override
   public void setUrl( String url )
   throws URISyntaxException, IOException
      {
      String proxy_url = getProxyUrl( url );
      if ( mProxy != null
           &&  proxy_url == mProxy.getProxyUrl() )
         mProxy.setUrl( url );
      else
         {
         mProxy.close();
         mProxy = new ProxyClientConnection( url, proxy_url );
         }
      }

   private ProxyClientConnection mProxy;
   }

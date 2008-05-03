package org.distroverse.viewer;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;

import org.distroverse.core.Util;
import org.distroverse.dvtp.Pair;
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

   public static Util.Pair< String, String > getProxyUrl( String url ) 
   throws URISyntaxException, IOException
      {
      URI place_uri = new URI( url );
      DvtpServerConnection dvtp_server
         = new DvtpServerConnection( place_uri );
      
      Object response = null;
      try
         {  response = dvtp_server.location( place_uri );  }
      catch ( ClassNotFoundException e )
         {
         /* Let it fall through and throw it as a protocol exception,
          * the same as if it returns something other than a Pair of
          * Strs.
          */
         }
      dvtp_server.close();
      if ( response instanceof Pair )
         {
         Pair response_pair = (Pair) response;
         if ( response_pair.getFirst() instanceof Str
              &&  response_pair.getSecond() instanceof Str )
            {
            // I'll bet this is what he meant by "halfway to Lisp":
            return new Util.Pair< String, String >
                     (((Str) response_pair.getFirst()).toString(),
                      ((Str) response_pair.getSecond()).toString());
            // I want my money back.
            }
         }
      
      throw new ProtocolException( "Server did not return a pair of"
                        + " strings in response to a LOCATION query" );
      }

   @Override
   public void close()
      {
      // TODO Auto-generated method stub

      }
   
   @Override
   public void setUrl( String location_url )
   throws URISyntaxException, IOException
      {
      Util.Pair< String, String > proxy_info =
         getProxyUrl( location_url );
      String proxy_url       = proxy_info.a;
      String location_regexp = proxy_info.b;
      if ( mProxy != null
           &&  proxy_url == mProxy.getProxyUrl() )
         mProxy.setUrl( location_url );
      else
         {
         mProxy.close();
         mProxy = new ProxyClientConnection( location_url, proxy_url,
                                             location_regexp );
         }
      }

   private ProxyClientConnection mProxy;
   }

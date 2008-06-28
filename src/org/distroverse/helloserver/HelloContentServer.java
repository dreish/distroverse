package org.distroverse.helloserver;

import java.io.IOException;

import org.distroverse.core.net.NetOutQueue;
import org.distroverse.core.net.NetSession;
import org.distroverse.distroplane.lib.DvtpListener;
import org.distroverse.distroplane.lib.DvtpServer;
import org.distroverse.distroplane.lib.SUtil;
import org.distroverse.dvtp.ConPerm;
import org.distroverse.dvtp.Err;
import org.distroverse.dvtp.ProxySpec;

public class HelloContentServer extends DvtpServer
   {
   public HelloContentServer( DvtpListener listener )
      {
      super( listener );
      }

   @Override
   public void handleGet( String url, NetOutQueue< Object > noq )
   throws IOException
      {
      if ( url.matches( "drtp://.*/PassThroughProxy.jar" ) )
         {
         SUtil.sendFile( "PassThroughProxy.jar", url, noq );
         }
      else
         {
         noq.add( new Err( "Resource not found: " + url, 404 ) );
         }
      }

   @Override
   public void handleKnock( String location, NetOutQueue< Object > noq )
   throws IOException
      {
      /* Only proxies from this server are ever allowed to connect to
       * this server:
       */
      noq.add( new ConPerm( false, "" ) );
      }

   @Override
   public void handleLocation( String location,
                               NetOutQueue< Object > noq )
   throws IOException
      {
      noq.add( new ProxySpec( "drtp://localhost/PassThroughProxy.jar",
                              ".*",
                          "org.distroverse.proxy.PassThroughProxy" ) );
      }

   @Override
   public void handleProxyObject( Object net_in_object,
                                  NetSession< Object > session )
   throws IOException
      {
      // TODO Auto-generated method stub

      }

   @Override
   public void handleProxyOpen( String token, NetOutQueue< Object > noq )
   throws IOException
      {
      // TODO Auto-generated method stub

      }

   /**
    * @param args
    */
   public static void main( String[] args )
      {
      createServer( HelloContentServer.class,
                    "DVTP/0.01 HelloContentServer 1.0.0" );
      }

   }

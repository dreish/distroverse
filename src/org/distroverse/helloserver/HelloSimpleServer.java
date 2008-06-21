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

public class HelloSimpleServer extends DvtpServer
   {
   public HelloSimpleServer( DvtpListener l )
      {
      super( l );
      }

   @Override
   public void handleGet( String url, NetOutQueue< Object > noq )
   throws IOException
      {
      if ( url.equals( "drtp://localhost/HelloSimpleProxy.jar" ) )
         {
         SUtil.sendFile( "HelloSimpleProxy.jar", url, noq );
         }
      else
         {
         noq.add( new Err( "Resource not found: " + url, 404 ) );
         }
      }

   @Override
   public void handleLocation( String location,
                               NetOutQueue< Object > noq )
   throws IOException
      {
      noq.add( new ProxySpec( "drtp://localhost/HelloSimpleProxy.jar",
                              ".*",
                          "org.distroverse.proxy.HelloSimpleProxy" ) );
      }

   @Override
   public void handleKnock( String location, NetOutQueue< Object > noq )
   throws IOException
      {
      // No proxy is ever allowed to connect to this server:
      noq.add( new ConPerm( false, "" ) );
      }

   @Override
   public void handleProxyObject( Object net_in_object,
                                  NetSession< Object > session )
   throws IOException
      {
      /* This server only hands out self-sufficient proxies.  There's no
       * way to establish proxy connections.
       */
      throw new IOException( "handleProxyObject() unimplemented" );
      }

   @Override
   public void handleProxyOpen( String token, 
                                NetOutQueue< Object > noq )
   throws IOException
      {
      // This server only hands out self-sufficient proxies.
      noq.add( new Err( "PROXYOPEN not implemented", 501 ) );
      }

   /**
    * @param args
    */
   public static void main( String[] args )
      {
      createServer( HelloSimpleServer.class,
                    "DVTP/0.01 HelloSimpleServer 1.0.0" );
      }
   }

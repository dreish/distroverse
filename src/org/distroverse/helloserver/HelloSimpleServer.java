package org.distroverse.helloserver;

import java.io.IOException;

import org.distroverse.core.net.NetOutQueue;
import org.distroverse.core.net.NetSession;
import org.distroverse.distroplane.lib.DvtpListener;
import org.distroverse.distroplane.lib.DvtpServer;

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
      // TODO Auto-generated method stub

      }

   @Override
   public void handleLocation( String location,
                               NetOutQueue< Object > noq )
   throws IOException
      {
      // TODO Auto-generated method stub

      }

   @Override
   public void handleProxyObject( Object net_in_object,
                                  NetSession< Object > session )
   throws IOException
      {
      // TODO Auto-generated method stub

      }

   @Override
   public void handleProxyOpen( String token, 
                                NetOutQueue< Object > noq )
                                                                         throws IOException
      {
      // TODO Auto-generated method stub

      }

   /**
    * @param args
    */
   public static void main( String[] args )
      {
      // TODO Auto-generated method stub

      }

   }

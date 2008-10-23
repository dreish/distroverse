/**
 *
 */
package org.distroverse.distroplane;

import java.io.IOException;

import org.distroverse.core.net.NetOutQueue;
import org.distroverse.core.net.NetSession;
import org.distroverse.distroplane.lib.DvtpListener;
import org.distroverse.distroplane.lib.DvtpServer;

/**
 * @author dreish
 *
 */
public class WorldServer extends DvtpServer
   {

   /**
    * @param listener
    */
   public WorldServer( DvtpListener listener )
      {
      super( listener );
      // TODO Auto-generated constructor stub
      }

   /* (non-Javadoc)
    * @see org.distroverse.distroplane.lib.DvtpServer#handleGet(java.lang.String, org.distroverse.core.net.NetOutQueue)
    */
   @Override
   public void handleGet( String url, NetOutQueue< Object > noq )
   throws IOException
      {
      // TODO Auto-generated method stub

      }

   /* (non-Javadoc)
    * @see org.distroverse.distroplane.lib.DvtpServer#handleKnock(java.lang.String, org.distroverse.core.net.NetOutQueue)
    */
   @Override
   public void handleKnock( String location, NetOutQueue< Object > noq )
   throws IOException
      {
      // TODO Auto-generated method stub

      }

   /* (non-Javadoc)
    * @see org.distroverse.distroplane.lib.DvtpServer#handleLocation(java.lang.String, org.distroverse.core.net.NetOutQueue)
    */
   @Override
   public void handleLocation( String location,
                               NetOutQueue< Object > noq )
   throws IOException
      {
      // TODO Auto-generated method stub

      }

   /* (non-Javadoc)
    * @see org.distroverse.distroplane.lib.DvtpServer#handleProxyObject(java.lang.Object, org.distroverse.core.net.NetSession)
    */
   @Override
   public void handleProxyObject( Object net_in_object,
                                  NetSession< Object > session )
   throws IOException
      {
      // TODO Auto-generated method stub

      }

   /* (non-Javadoc)
    * @see org.distroverse.distroplane.lib.DvtpServer#handleProxyOpen(java.lang.String, org.distroverse.core.net.NetOutQueue)
    */
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
      createServer( WorldServer.class,
                    "DVTP/0.01 WorldServer 0.02" );

      }

   }

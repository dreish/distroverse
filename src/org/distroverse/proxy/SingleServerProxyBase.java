package org.distroverse.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.ClosedChannelException;

import org.distroverse.core.Log;
import org.distroverse.core.net.NetSession;
import org.distroverse.distroplane.lib.DvtpServer;
import org.distroverse.dvtp.ClientSendable;
import org.distroverse.dvtp.DisplayUrl;
import org.distroverse.dvtp.DvtpExternalizable;
import org.distroverse.dvtp.SetUrl;
import org.distroverse.dvtp.Str;

/**
 * Converts setUrl objects from client into the act of connecting to a
 * server (disconnecting from the previous server if connected) and
 * sending it the PROXYOPEN command (with no argument), and passes other
 * messages to the abstract method receiveFromClient().
 * @author dreish
 */
public abstract class SingleServerProxyBase extends NetProxyBase
   {
   public SingleServerProxyBase() throws IOException
      {
      super();
      }

   public void sendToServer( DvtpExternalizable o )
   throws ClosedChannelException
      {
      mSession.getNetOutQueue().add( o );
      }

   /**
    * Continue using the same proxy, but connect to a different server.
    * (Also used for the first-time connection.)
    * @param remote_url
    * @throws URISyntaxException
    * @throws IOException
    */
   protected void setServer( String host, int port ) throws IOException
      {
      if ( mSession != null )
         mSession.close();

      Log.p( "connecting to server: " + host + ":" + port,
             Log.PROXY, -50 );
      SocketAddress remote_addr = new InetSocketAddress( host, port );
      mSession = connect( remote_addr );
      Log.p( "sending PROXYOPEN command to " + host + ":" + port,
             Log.PROXY, -50 );
      mSession.getNetOutQueue()
              .add( new Str( "PROXYOPEN" ) );
      }

   protected abstract void initWorld() throws IOException;

   protected void setUrl( String new_url )
   throws URISyntaxException, IOException
      {
      URI new_uri = new URI( new_url );
      String host = new_uri.getHost();
      int port = new_uri.getPort();
      if ( port == -1 )
         port = DvtpServer.DEFAULT_PORT;

      if ( port != mCurrentPort
           ||  ! host.contentEquals( mCurrentHost ) )
         setServer( host, port );

      putQueue( new DisplayUrl( new_url ) );
      initWorld();
      }

   public void offer( ClientSendable o ) throws IOException
      {
      if ( o instanceof SetUrl )
         {
         SetUrl su = (SetUrl) o;
         try
            {
            setUrl( su.getUrl() );
            }
         catch ( URISyntaxException e )
            {
            // TODO Auto-generated catch block
            e.printStackTrace();
            }
         }
      else
         {
         receiveFromClient( o );
         }
      }

   private NetSession< Object > mSession;
   private String mCurrentHost;
   private int    mCurrentPort;
   }
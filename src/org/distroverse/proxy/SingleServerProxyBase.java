package org.distroverse.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.ClosedChannelException;

import org.distroverse.core.net.NetSession;
import org.distroverse.distroplane.lib.DvtpServer;
import org.distroverse.dvtp.ClientSendable;
import org.distroverse.dvtp.DvtpExternalizable;
import org.distroverse.dvtp.SetUrl;

public abstract class SingleServerProxyBase extends NetProxyBase
   {
   public void sendToServer( DvtpExternalizable o )
   throws ClosedChannelException
      {
      mSession.getNetOutQueue().add( o );
      }
   
   protected void setServer( String remote_url )
   throws URISyntaxException, IOException
      {
      if ( mSession != null )
         mSession.close();

      URI remote_uri = new URI( remote_url );
      int port = remote_uri.getPort();
      if ( port == -1 )
         port = DvtpServer.DEFAULT_PORT;
      SocketAddress remote_addr
         = new InetSocketAddress( remote_uri.getHost(),
                                  port );
      mSession = connect( remote_addr );
      }
   
   public void offer( ClientSendable o ) throws IOException
      {
      if ( o instanceof SetUrl )
         {
         SetUrl su = (SetUrl) o;
         try
            {
            setServer( su.getUrl() );
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

   NetSession< Object > mSession;
   }

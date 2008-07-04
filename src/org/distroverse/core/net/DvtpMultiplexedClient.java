/**
 * 
 */
package org.distroverse.core.net;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.distroverse.distroplane.lib.DvtpMultiplexedConnection;

/**
 * @author dreish
 *
 */
public class DvtpMultiplexedClient< O extends Object,
                                    P extends ObjectParser< O >,
                                    S extends ObjectStreamer< O > >
extends DvtpMultiplexedConnection< O, P, S >
   {
   public DvtpMultiplexedClient()
      {
      super();
      }

   /* (non-Javadoc)
    * @see org.distroverse.distroplane.lib.DvtpMultiplexedConnection#acceptConnection(java.nio.channels.SelectionKey)
    */
   @Override
   protected void acceptConnection( SelectionKey key )
   throws IOException
      {
      throw new IOException( "Client cannot accept connections" );
      }

   public NetSession< O > connect( SocketAddress remote_address )
   throws IOException
      {
      SocketChannel remote = SocketChannel.open();
      remote.connect( remote_address );
      return addSocket( remote );
      }
   }

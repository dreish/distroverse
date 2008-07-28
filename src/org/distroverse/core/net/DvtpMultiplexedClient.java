/**
 *
 */
package org.distroverse.core.net;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;


/**
 * @author dreish
 *
 */
public class DvtpMultiplexedClient< O,
                                    P extends ObjectParser< O >,
                                    S extends ObjectStreamer< O > >
extends DvtpMultiplexedConnection< O, P, S >
   {
   public DvtpMultiplexedClient( Class< P > parser_class,
                                 Class< S > streamer_class )
   throws IOException
      {
      super( parser_class, streamer_class );
      mSelector = Selector.open();
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

   /**
    * Open a new connection to remote_address and return the session.
    * The InQueue watcher attached to this multiplexer will begin
    * seeing input from the remote address immediately, so it must be
    * written to recognize any greeting from the remote server without
    * crapping out just because the session's attachment is still null.
    * @param remote_address
    * @return
    * @throws IOException
    */
   public NetSession< O > connect( SocketAddress remote_address )
   throws IOException
      {
      SocketChannel remote = SocketChannel.open();
      remote.connect( remote_address );
      return addSocket( remote );
      }
   }

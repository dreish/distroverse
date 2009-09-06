/*
 * <copyleft>
 *
 * Copyright 2007-2009 Dan Reish
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or
 * combining it with Clojure (or a modified version of that program)
 * or clojure-contrib (or a modified version of that library),
 * containing parts covered by the terms of the Eclipse Public
 * License, the licensors of this Program grant you additional
 * permission to convey the resulting work. {Corresponding Source for
 * a non-source form of such a combination shall include the source
 * code for the parts of Clojure and clojure-contrib used as well as
 * that of the covered work.}
 *
 * </copyleft>
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

   @Override
   public void shutdownListener()
      {
      // do nothing; there is no listener on the client
      }
   }

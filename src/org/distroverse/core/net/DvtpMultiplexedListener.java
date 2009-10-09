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
/**
 *
 */
package org.distroverse.core.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.distroverse.core.Log;
import org.distroverse.distroplane.lib.DvtpListener;
import org.distroverse.distroplane.lib.DvtpServer;

/**
 * This class implements DvtpListener using a single thread to listen
 * for new connections and process I/O on existing connections.
 * @author dreish
 */
public class
DvtpMultiplexedListener< P extends ObjectParser< Object >,
                         S extends ObjectStreamer< Object > >
extends DvtpMultiplexedConnection< Object, P, S >
implements DvtpListener
   {
   public static final int DEFAULT_NUM_THREADS = 8;
   /**
    *
    */
   public DvtpMultiplexedListener( Class< P > parser_class,
                                   Class< S > streamer_class )
      {  super( parser_class, streamer_class );  }

   public DvtpServer getServer()
      {  return mServer;  }

   public void setServer( DvtpServer server )
      {  mServer = server;  }

   @Override
   public void shutdownListener()
      {
      try
         {
         // XXX seems like this should be the first thing that happens
         // when shutting down.
         mServerSocket.close();
         }
      catch ( IOException e )
         {
         e.printStackTrace();
         }
      }

   /* (non-Javadoc)
    * @see org.distroverse.distroplane.lib.DvtpListener#serve()
    */
   public void serve()
      {
      // Set up the sockets
      try
         {
         mServerChannel  = ServerSocketChannel.open();
         mSelector       = Selector.open();
         mServerSocket = mServerChannel.socket();
         mServerSocket.bind( new InetSocketAddress(
                                            mServer.getListenPort() ) );
         mServerChannel.configureBlocking( false );
         // SocketChannel client = server_channel.accept();
         mServerChannel.register( mSelector, SelectionKey.OP_ACCEPT );
         Log.p( "Listening for connections", Log.NET, -40 );

         run();
         }
      catch ( IOException e )
         {
         Log.p( "Unhandled exception: " + e,
                Log.NET | Log.UNHANDLED, 1 );
         Log.p( e, Log.NET | Log.UNHANDLED, 1 );
         // Returns without listening; it is assumed that the socket
         // could not be created.
         }
      }

   @Override
   protected void acceptConnection( SelectionKey key )
   throws IOException
      {
      Log.p( "acceptConnection called", Log.NET, -50 );
      ServerSocketChannel server = (ServerSocketChannel) key.channel();
      SocketChannel       client = server.accept();
      if ( client == null )  return;
      // TODO - might not always want to throw away the NetSession
      addSocket( client );
      }

   @Override
   protected void handleClosedSession( NetSession< Object > session )
      {
      mServer.handleClosedConnection( session );
      }

   private ServerSocketChannel  mServerChannel;
   private DvtpServer           mServer;
   private ServerSocket         mServerSocket;
   }

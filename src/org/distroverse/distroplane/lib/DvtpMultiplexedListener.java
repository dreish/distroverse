/*
 * Copyright (c) 2007-2008 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * General Public License (GPL) version 3 or later</a>
 */
/**
 * 
 */
package org.distroverse.distroplane.lib;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.distroverse.core.Log;
import org.distroverse.core.net.ObjectParser;
import org.distroverse.core.net.ObjectStreamer;

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
         // Bind the socket to the DvtpServer's port number
         ServerSocket ss = mServerChannel.socket();
         ss.bind( new InetSocketAddress( mServer.getListenPort() ) );
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

   private ServerSocketChannel  mServerChannel;
   private DvtpServer           mServer;
   }

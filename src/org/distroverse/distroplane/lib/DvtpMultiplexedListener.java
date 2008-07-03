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

import java.io.*;
import java.lang.reflect.Constructor;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
// import java.nio.charset.*;
import org.distroverse.core.Log;
import org.distroverse.core.net.*;

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
      {
      super();
      mParserClass   = parser_class;
      mStreamerClass = streamer_class;
//      mNumThreads = DEFAULT_NUM_THREADS;
//      mEncoder = Charset.forName( "US-ASCII" ).newEncoder();
      }

   public DvtpServer getServer()
      {  return mServer;  }

   public void setServer( DvtpServer server )
      {  mServer = server;  }

   public void setGreeting( String greeting )
      {  mGreeting = greeting;  }

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

         listen();
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
   
   /**
    * This function does not return.  When it receives a connection, it
    * creates a new listener thread to take over the job of listening
    * for new connections.  When it finishes with that connection, the
    * thread ends.
    * 
    * Exceptions are ignored.
    * 
    * FIXME Handle IOException properly
    */
   private void listen()
      {
      boolean encountered_fatal_exception = false;
      while ( ! encountered_fatal_exception )
         {
         try
            {
            mSelector.select();
            processIo();
            }
         catch ( IOException e )
            {
            Log.p( "Unhandled exception: " + e, 
                   Log.NET | Log.UNHANDLED, 1 );
            Log.p( e, Log.NET | Log.UNHANDLED, 1 );
            encountered_fatal_exception = true;
            }
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
      client.configureBlocking( false );
//      SelectionKey tmp_key = client.register( mSelector,
//                                              SelectionKey.OP_READ );

      ByteBuffer parser_buffer   = ByteBuffer.allocate( 1024 );
      ByteBuffer streamer_buffer = ByteBuffer.allocate( 1024 );
      P parser;
      S streamer;
      try
         {
         Constructor< P > parser_constructor 
            = mParserClass.getConstructor( ByteBuffer.class ); 
         Constructor< S > streamer_constructor 
            = mStreamerClass.getConstructor( ByteBuffer.class );
         parser   = parser_constructor  .newInstance( parser_buffer );
         streamer = streamer_constructor.newInstance( streamer_buffer );
         }
      catch ( Exception e )
         {
         /* This exception should never occur, because the base classes
          * ObjectParser and ObjectStreamer define concrete constructors
          * with one ByteBuffer argument.
          */
         Log.p( "Impossible exception: " + e, Log.NET, 100 );
         try
            {  key.channel().close();  }
         catch ( IOException e2 )
            {  
            Log.p( "Unhandled impossible exception: " + e,
                   Log.NET | Log.UNHANDLED, 100 );  
            }
         return;
         }
      NetInQueue< Object > niqs = new NetInQueue< Object >(
            parser,   DEFAULT_QUEUE_SIZE, mSelector, client );
      NetOutQueue< Object > noqs = new NetOutQueue< Object >(
            streamer, DEFAULT_QUEUE_SIZE, mSelector, client );
      new NetSession< Object >( niqs, noqs );
      mWatcher.addQueue( niqs );
      /* activateNetworkReader() creates a SelectionKey and attaches
       * niqs to it, so all three of the above objects survive at least
       * as long as the SelectionKey survives.
       */ 
      niqs.activateNetworkReader();
      if ( mGreeting != null )
         noqs.add( mGreeting );
      }

   //   private int                 mNumThreads;
   private ServerSocketChannel         mServerChannel;
   private DvtpServer                  mServer;
   private String                      mGreeting;
   }

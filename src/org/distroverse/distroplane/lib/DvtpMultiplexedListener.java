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
 * This class implements DvtpListener in a simple, straightforward, and
 * not particularly efficient way using multiple threads.  It always has
 * the same number of listening threads (except during the brief period
 * between accepting a connection and creating a new thread), plus a
 * thread for each active session.
 * @author dreish
 */
public class 
DvtpMultiplexedListener< P extends ObjectParser< Object >,
                         S extends ObjectStreamer< Object > > 
extends DvtpListener
   {
   public static final int DEFAULT_NUM_THREADS = 8;
   public static final int DEFAULT_QUEUE_SIZE  = 10;

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

   /* (non-Javadoc)
    * @see org.distroverse.distroplane.lib.DvtpListener#serve()
    */
   @Override
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
   
   private void processIo()
      {
      for ( SelectionKey key : mSelector.selectedKeys() )
         {
         try
            {
            if ( key.isAcceptable() )
               acceptConnection( key );
            if ( key.isReadable() )
               readConnection( key );
            if ( key.isWritable() )
               writeConnection( key );
            }
         catch ( Exception e )
            {
            Log.p( "Canceling an unknown key due to an exception",
                   Log.NET, -10 );
            key.cancel();
            Log.p( e, Log.NET, -10 );
            try 
               {  key.channel().close();  }
            catch ( IOException e2 )
               {  
               Log.p( "Unhandled exception: " + e, 
                      Log.NET | Log.UNHANDLED, 1 ); 
               }
            }
         }
      }
   
   private void acceptConnection( SelectionKey key )
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

   private void readConnection( SelectionKey key )
   throws Exception
      {
      Log.p( "readConnection called", Log.NET, -50 );
      @SuppressWarnings( "unchecked" )
      NetSession< Object > session 
         = (NetSession< Object >) key.attachment();
      session.getNetInQueue().read();
      }

   private void writeConnection( SelectionKey key )
   throws Exception
      {
      Log.p( "writeConnection called", Log.NET, -50 );
      @SuppressWarnings( "unchecked" )
      NetSession< Object > session 
         = (NetSession< Object >) key.attachment();
      session.getNetOutQueue().write();
      }

//   private int                 mNumThreads;
   private ServerSocketChannel    mServerChannel;
   private Selector               mSelector;
   private Class< P >             mParserClass;
   private Class< S >             mStreamerClass;
   }

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
import org.distroverse.core.*;

/**
 * This class implements DvtpListener in a simple, straightforward, and
 * not particularly efficient way using multiple threads.  It always has
 * the same number of listening threads (except during the brief period
 * between accepting a connection and creating a new thread), plus a
 * thread for each active session.
 * @author dreish
 */
public class DvtpMultiplexedListener< T, 
                                      P extends ObjectParser< T >,
                                      S extends ObjectStreamer< T > > 
extends DvtpListener
   {
   public static final int DEFAULT_NUM_THREADS = 8;
   public static final int DEFAULT_QUEUE_SIZE  = 10;

   /**
    * 
    */
   public DvtpMultiplexedListener()
      {
      super();
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

         listen();
         }
      catch ( IOException e )
         {
         // FIXME better exception handling/logging here
         System.err.println( "Unhandled exception: " + e );
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
            // FIXME better exception handling/logging here
            System.err.println( "Unhandled exception: " + e );
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
            }
         catch ( Exception e )
            {
            key.cancel();
            try 
               {  key.channel().close();  }
            catch ( IOException e2 )
               {  System.err.println( "Unhandled exception: " + e );  }
            }
         }
      }
   
   private void acceptConnection( SelectionKey key )
   throws IOException
      {
      System.err.println( "accept_connection called" );
      ServerSocketChannel server = (ServerSocketChannel) key.channel();
      SocketChannel       client = server.accept();
      if ( client == null )  return;
      client.configureBlocking( false );
//      SelectionKey tmp_key = client.register( mSelector,
//                                              SelectionKey.OP_READ );

      ByteBuffer parser_buffer   = ByteBuffer.allocate( 1024 );
      ByteBuffer streamer_buffer = ByteBuffer.allocate( 1024 );
      P parser = null;
      S streamer= null;
      try
         {
         @SuppressWarnings( {"unchecked", "null"} )
         Constructor< P > parser_constructor 
            = (Constructor< P >)
              parser.getClass().getConstructor( ByteBuffer.class ); 
         @SuppressWarnings( {"unchecked", "null"} )
         Constructor< S > streamer_constructor 
            = (Constructor< S >)
              streamer.getClass().getConstructor( ByteBuffer.class ); 
         parser   = parser_constructor  .newInstance( parser_buffer );
         streamer = streamer_constructor.newInstance( streamer_buffer );
         }
      catch ( Exception e )
         {
         return;
         }
      NetInQueue< T > niqs = new NetInQueue< T >(
            parser, 
            DEFAULT_QUEUE_SIZE, mSelector, client );
      NetOutQueue< T > noqs = new NetOutQueue< T >(
            streamer,
            DEFAULT_QUEUE_SIZE, mSelector, client );
      new NetSession< T >( niqs, noqs );
      niqs.activateNetworkReader();
      }

   private void readConnection( SelectionKey key )
   throws Exception
      {
      @SuppressWarnings( "unchecked" )
      NetSession< T > session = (NetSession< T >) key.attachment();
      session.getNetInQueue().read();
      // XXX Now no one calls handleCommand()
      }

//   private int                 mNumThreads;
   private ServerSocketChannel mServerChannel;
   private Selector            mSelector;
   }

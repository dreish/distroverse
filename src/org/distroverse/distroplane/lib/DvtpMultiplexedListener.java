/**
 * 
 */
package org.distroverse.distroplane.lib;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;

/**
 * This class implements DvtpListener in a simple, straightforward, and
 * not particularly efficient way using multiple threads.  It always has
 * the same number of listening threads (except during the brief period
 * between accepting a connection and creating a new thread), plus a
 * thread for each active session.
 * @author dreish
 */
public class DvtpMultiplexedListener extends DvtpListener
   {
   public static final int DEFAULT_NUM_THREADS = 8;

   /**
    * 
    */
   public DvtpMultiplexedListener()
      {
      mNumThreads = DEFAULT_NUM_THREADS;
      mEncoder = Charset.forName( "US-ASCII" ).newEncoder();
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
            process_io();
            }
         catch ( IOException e )
            {
            // FIXME better exception handling/logging here
            System.err.println( "Unhandled exception: " + e );
            encountered_fatal_exception = true;
            }
         }
      }
   
   private void process_io()
      {
      for ( SelectionKey key : mSelector.selectedKeys() )
         {
         try
            {
            if ( key.isAcceptable() )
               accept_connection( key );
            if ( key.isReadable() )
               read_connection( key );
            }
         catch ( IOException e )
            {
            key.cancel();
            try 
               {  key.channel().close();  }
            catch ( IOException e2 )
               {  System.err.println( "Unhandled exception: " + e );  }
            }
         }
      }
   
   void accept_connection( SelectionKey key )
   throws IOException
      {
      ServerSocketChannel server = (ServerSocketChannel) key.channel();
      SocketChannel       client = server.accept();
      if ( client == null )  return;
      client.configureBlocking( false );
      SelectionKey tmp_key = client.register( mSelector,
                                              SelectionKey.OP_READ );
      // XXX The design imposes an unreasonable limit on request length
      tmp_key.attach( ByteBuffer.allocate( 1024 ) );
      }

   void read_connection( SelectionKey key )
   throws IOException
      {
      SocketChannel client = (SocketChannel) key.channel();
      ByteBuffer    buffer = (ByteBuffer) key.attachment();
      client.read( buffer );
      String command = "";
      while ( buffer.hasRemaining() )
         command += buffer.get();
      buffer.clear();
      mServer.handleCommand( command, client, buffer );
      }

   int                 mNumThreads;
   CharsetEncoder      mEncoder;
   ServerSocketChannel mServerChannel;
   Selector            mSelector;
   }

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
public class DvtpThreadedListener extends DvtpListener
   {
   public static final int DEFAULT_NUM_THREADS = 8;

   /**
    * 
    */
   public DvtpThreadedListener()
      {
      mNumThreads = DEFAULT_NUM_THREADS;
      mEncoder = Charset.forName( "US-ASCII" ).newEncoder();
      }

   /* (non-Javadoc)
    * @see org.distroverse.distroplane.lib.DvtpListener#listen()
    */
   @Override
   public void serve()
      {
      for ( int i = 1; i < mNumThreads; ++i )
         createThread();
      // Each of the N-1 threads created above calls listen().  This
      // thread will now do the same:
      listen();
      }
   
   /**
    * Simple inner class to call listen() in a thread.
    * @author dreish
    */
   private class ListenerThread extends Thread
      {
      public ListenerThread()  {  /* Do nothing. */  }
      @Override
      public void run()  {  DvtpThreadedListener.this.listen();  }
      }

   private void createThread()
      {
      ListenerThread t = new ListenerThread();
      t.start();
      }

   /**
    * This function does not return.  When it receives a connection, it
    * creates a new listener thread to take over the job of listening
    * for new connections.  When it finishes with that connection, the
    * thread ends.
    * 
    * Exceptions are ignored.
    * 
    * XXX Handle IOException properly
    */
   protected void listen()
      {
      ServerSocketChannel server;
      try
         {
         ByteBuffer read_buffer = new ByteBuffer();
         server = ServerSocketChannel.open();
         ServerSocket ss = server.socket();
         ss.bind( new InetSocketAddress( 10 ) );
         server.configureBlocking( false );
         SocketChannel client = server.accept();
         client.read( read_buffer );
         }
      catch ( IOException e )
         {
         System.err.println( "Unhandled exception: " + e );
         }
      }
   
   int mNumThreads;
   CharsetEncoder mEncoder;
   }

package org.distroverse.distroplane.lib;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

import org.distroverse.core.Log;
import org.distroverse.core.net.NetInQueue;
import org.distroverse.core.net.NetInQueueWatcher;
import org.distroverse.core.net.NetOutQueue;
import org.distroverse.core.net.NetSession;
import org.distroverse.core.net.ObjectParser;
import org.distroverse.core.net.ObjectStreamer;

/**
 * The I/O core: a class that manages either outbound or incoming
 * network connections.
 * @author dreish
 *
 * @param <O>
 * @param <P>
 * @param <S>
 */
public abstract class
DvtpMultiplexedConnection< O,
                           P extends ObjectParser< O >,
                           S extends ObjectStreamer< O > >
extends Thread
   {
   public static final int DEFAULT_QUEUE_SIZE = 10;

   public DvtpMultiplexedConnection( Class< P > parser_class,
                                     Class< S > streamer_class )
      {
      super();
      mParserClass   = parser_class;
      mStreamerClass = streamer_class;
      mSelecting     = false;
      mLock          = new ReentrantReadWriteLock();
      }

   @SuppressWarnings("unused")
   private DvtpMultiplexedConnection()
      {
      // Disallowed.
      }

   /**
    * If implemented, creates a new connection for a remote socket
    * connecting to this multiplexer's server socket.
    * @param key
    * @throws IOException
    */
   protected abstract void acceptConnection( SelectionKey key )
   throws IOException;

   public void setWatcher( NetInQueueWatcher< O > watcher_thread )
      {  mWatcher = watcher_thread;  }

   public void setGreeting( O greeting )
      {  mGreeting = greeting;  }

   /**
    * This function does not return.  When it receives a connection, it
    * adds that connection to the multiplexer.
    *
    * Exceptions are ignored.
    *
    * FIXME Handle IOException properly
    */
   @Override
   public void run()
      {
      while ( true )
         {
         Lock l = mLock.writeLock();
         synchronized ( mSelecting )
            {
            assert( ! mSelecting );
            mSelecting = true;
            }
         l.lock();
         try
            {
            mSelector.select();
            }
         catch ( InterruptedIOException e )
            {
            // Normal and expected.
            }
         catch ( IOException e )
            {
            Log.p( "Unhandled exception: " + e,
                   Log.NET | Log.UNHANDLED, 1 );
            Log.p( e, Log.NET | Log.UNHANDLED, 1 );
            }
         finally
            {
            mSelecting = false;
            l.unlock();
            }
         processIo();
         }
      }

   public ReadLock pauseGetLock()
      {
      ReadLock ret = mLock.readLock();
      synchronized ( mSelecting )
         {
         if ( mSelecting )
            mSelector.wakeup();
         /* By locking inside the mSelecting critical section here, the
          * race condition whereby processIo() would return immediately
          * and the write lock would be reestablished before this thread
          * could get a read lock is avoided.
          */
         ret.lock();
         }
      return ret;
      }

   public void unpause( ReadLock l )
      {
      l.unlock();
      }

   protected void processIo()
      {
      Iterator< SelectionKey > key_iterator
         = mSelector.selectedKeys().iterator();

      while ( key_iterator.hasNext() )
         {
         SelectionKey key = key_iterator.next();
         try
            {
            if ( key.isAcceptable() )
               acceptConnection( key );
            if ( key.isReadable() )
               readConnection( key );
            if ( key.isWritable() )
               writeConnection( key );
            key_iterator.remove();
            }
         catch ( Exception e )
            {
            // FIXME This message seems unnecessarily stupid
            Log.p( "Canceling an unknown key due to an exception: " + e
                   + " - " + e.getMessage(), Log.NET, -10 );
            Log.p( e, Log.NET, -10 );
            key_iterator.remove();
            key.cancel();
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

   private void readConnection( SelectionKey key ) throws Exception
      {
      Log.p( "readConnection called", Log.NET, -50 );
      @SuppressWarnings( "unchecked" )
      NetSession< Object > session
         = (NetSession< Object >) key.attachment();
      session.getNetInQueue().read();
      }

   private void writeConnection( SelectionKey key ) throws Exception
      {
      Log.p( "writeConnection called", Log.NET, -50 );
      @SuppressWarnings( "unchecked" )
      NetSession< Object > session
         = (NetSession< Object >) key.attachment();
      session.getNetOutQueue().write();
      }

   /**
    * Creates a net session for the given SocketChannel, with input and
    * output object queues, and a parser and streamer.
    * XXX serious unencountered bug: NullPointerException if this is
    * called before setWatcher() (which might be outside the program's
    * control).  Throw exception?  Spin wait?
    * @param remote
    * @return the created NetSession< O >
    * @throws IOException
    * @throws ClosedChannelException
    */
   protected NetSession< O > addSocket( SocketChannel remote )
   throws IOException, ClosedChannelException
      {
      remote.configureBlocking( false );
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
         Log.p( e, Log.NET, 100 );
         try
            {  remote.close();  }
         catch ( IOException e2 )
            {
            Log.p( "Unhandled impossible exception: " + e,
                   Log.NET | Log.UNHANDLED, 100 );
            }
         return null;
         }
      NetInQueue< O > niqs = new NetInQueue< O >(
            parser,   DEFAULT_QUEUE_SIZE, mSelector, remote, this );
      NetOutQueue< O > noqs = new NetOutQueue< O >(
            streamer, DEFAULT_QUEUE_SIZE, mSelector, remote, this );
      NetSession< O > ret = new NetSession< O >( niqs, noqs );
      mWatcher.addQueue( niqs );
      /* activateNetworkReader() creates a SelectionKey and attaches
       * niqs to it, so all three of the above objects survive at least
       * as long as the SelectionKey survives.
       */
      niqs.activateNetworkReader();
      if ( mGreeting != null )
         noqs.add( mGreeting );

      return ret;
      }

   protected Class< P >              mParserClass;
   protected Class< S >              mStreamerClass;
   protected NetInQueueWatcher< O >  mWatcher;
   protected O                       mGreeting;
   protected Selector                mSelector;
   private   Boolean                 mSelecting;
   private   ReentrantReadWriteLock  mLock;
   }

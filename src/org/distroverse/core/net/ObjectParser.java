/*
 * Copyright (c) 2007-2008 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * General Public License (GPL) version 3 or later</a>
 */
package org.distroverse.core.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.distroverse.core.*;

/**
 * An ObjectRecognizer attempts to convert a stream of bytes into
 * objects of type T.
 * @author dreish
 *
 * @param <T>
 */
public abstract class ObjectParser< T >
   {
   public ObjectParser( ByteBuffer b )
      {
      super();
      mBuffer = b;
      mBaos = new ByteArrayOutputStream();
      }
    
   public void setQueue( NetInQueue< T > queue )
      {  mQueue = queue;  }
   
   /**
    * Takes input (from the network socket), adds it to the variable-
    * size buffer mBaos, and if there is at least one complete object
    * in that buffer ready to be reassembled, adds that object or
    * objects to the queue, removes it or them from the buffer, and
    * notify()s any wait()ing threads.
    * @param input - a ByteBuffer, all of which is to be copied
    */
   synchronized public void readBytes( ByteBuffer input )
   throws Exception
      {
      if ( ! input.hasArray() )
         throw new IllegalArgumentException( "input with no array" );
      /* FIXME for some reason this is the only way I'm able to
       * recognize a closed connection.
       */
      if ( input.limit() == 0 )
         {
         Log.p( "readBytes(): buffer was empty", Log.NET, 1 );
         throw new IOException( "readable stream empty" );
         }
      mBaos.write( input.array(), input.arrayOffset(),
                   input.limit() );
      input.clear();
      parseObjects( mBaos, mQueue );
      }

   synchronized public void read( SocketChannel client )
   throws Exception
      {
      client.read( mBuffer );
      mBuffer.flip();
      readBytes( mBuffer );
      mBuffer.clear();
      }
   
   /**
    * Called by readBytes() every time bytes are added to
    * ObjectParser.mBaos, this method must parse any and all complete
    * and fully parseable objects in the given BAOS, removing those and
    * any remaining bytes into the subclass' own BAOS, and adding any
    * objects to 'queue'.
    * FIXME this method always clears baos; doesn't need to be a BAOS. 
    * @param baos - byte stream input
    * @param queue - object output
    * @throws Exception
    */
   abstract protected void
   parseObjects( ByteArrayOutputStream baos,
                 NetInQueue< T > queue )
   throws Exception;

   private NetInQueue< T >       mQueue;
   private ByteArrayOutputStream mBaos;
   private ByteBuffer            mBuffer;
   }

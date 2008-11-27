/*
 * <copyleft>
 *
 * Copyright 2007-2008 Dan Reish
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
 * containing parts covered by the terms of the Common Public License,
 * the licensors of this Program grant you additional permission to
 * convey the resulting work. {Corresponding Source for a non-source
 * form of such a combination shall include the source code for the
 * parts of Clojure and clojure-contrib used as well as that of the
 * covered work.}
 *
 * </copyleft>
 */
package org.distroverse.core.net;

import java.io.*;
//import java.net.*;
import java.nio.*;
//import java.nio.channels.*;
//import java.nio.charset.*;
import java.nio.channels.SocketChannel;

/**
 * An ObjectStreamer works on a NetOutQueue of objects of class T, and
 * converts that queue into a stream of bytes.
 * @author dreish
 *
 * @param <T>
 */
public abstract class ObjectStreamer< T >
   {
   public ObjectStreamer( ByteBuffer b )
      {
      super();
      mBuffer = b;
      mBaos = new ByteArrayOutputStream();
      }

   public void setQueue( NetOutQueue< T > queue )
      {
      mQueue = queue;
      mPosition = 0;
      }
   
   /**
    * If there is data remaining to be written from the queue, this
    * method writes at least 1 and at most bytes_requested bytes to
    * output, returning the number of bytes written.  Otherwise it
    * clears output, stops the Selector (via the queue), and returns 0.
    * @param output
    * @param bytes_requested
    * @return
    */
   synchronized public int 
   writeBytes( ByteBuffer output, int bytes_requested )
   throws Exception
      {
      if ( bytes_requested == 0 )
         return 0;

      if ( empty() )
         {
         mBaos.reset();
         mPosition = 0;
         streamNextObject( mBaos, mQueue );
         }

      int bytes_to_write = mBaos.size() - mPosition;
      if ( bytes_to_write > bytes_requested )
         bytes_to_write = bytes_requested;
      if ( bytes_to_write > 0 )
         output.put( mBaos.toByteArray(), mPosition, bytes_to_write );
      else
         mQueue.stopNetworkWriter();
      mPosition += bytes_to_write;
      
      return bytes_to_write;
      }
   
   synchronized public void write( SocketChannel client )
   throws Exception
      {
      mBuffer.clear();
      writeBytes( mBuffer, mBuffer.capacity() );
      mBuffer.flip();
      client.write( mBuffer );
      }
   
   /**
    * Subclasses must implement this method: take the head object in the
    * queue, write it to baos, and remove it from the queue.  It must do
    * nothing when the queue is empty.  The method must be threadsafe,
    * by synchronizing all access to queue, and by being itself
    * synchronized, which is guaranteed to make modification of baos
    * threadsafe.
    * FIXME Deadlock opportunity?
    * @param baos
    * @param queue
    */
   abstract protected void 
   streamNextObject( ByteArrayOutputStream baos,
                     NetOutQueue< T > queue )
   throws Exception;
   
   protected void setByteBuffer( ByteBuffer b )
      {  mBuffer = b;  }

   public ByteBuffer getByteBuffer()
      {  return mBuffer;  }
   
   private boolean empty()
      {
      return ( mBaos.size() == 0 || mBaos.size() == mPosition );
      }
   
   private NetOutQueue< T >      mQueue;
   private int                   mPosition;
   private ByteArrayOutputStream mBaos;
   private ByteBuffer            mBuffer;
   }

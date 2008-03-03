package org.distroverse.core;

import java.io.*;
//import java.net.*;
import java.nio.*;
//import java.nio.channels.*;
//import java.nio.charset.*;

/**
 * An ObjectStreamer works on a queue of objects of class T, and
 * converts that queue into a stream of bytes.
 * @author dreish
 *
 * @param <T>
 */
public abstract class ObjectStreamer< T >
   {
   public void setQueue( NetOutQueue< T > queue )
      {
      mQueue = queue;
      mPosition = 0;
      mBaos = new ByteArrayOutputStream();
      }
   
   /**
    * If there is data remaining to be written from the queue, this
    * method writes at least 1 and at most bytes_requested bytes to
    * output, returning the number of bytes written.  Otherwise it
    * clears output.
    * and returns 0.
    * @param output
    * @param bytes_requested
    * @return
    */
   public int getBytes( ByteBuffer output, int bytes_requested )
      {
      if ( empty() )
         {
         mBaos.reset();
         mPosition = 0;
         getNextObject( mBaos, mQueue );
         }

      byte[] baos_contents = mBaos.toByteArray();
      int bytes_to_write = 0;
      bytes_to_write = mBaos.size() - mPosition;
      if ( bytes_to_write > bytes_requested )
         bytes_to_write = bytes_requested;
      output.put( baos_contents, mPosition, bytes_to_write );
      return bytes_to_write;
      }
   
   abstract protected void 
   getNextObject( ByteArrayOutputStream baos, NetOutQueue< T > queue );
   
   private boolean empty()
      {
      return ( mBaos.size() == 0 || mBaos.size() == mPosition );
      }
   
   private NetOutQueue< T > mQueue;
   private int mPosition;
   private ByteArrayOutputStream mBaos; 
   }

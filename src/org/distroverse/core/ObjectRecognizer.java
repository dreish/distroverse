package org.distroverse.core;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

/**
 * An ObjectRecognizer attempts to convert a stream of bytes into
 * objects of type T.
 * @author dreish
 *
 * @param <T>
 */
public abstract class ObjectRecognizer< T >
   {
   public void setQueue( NetInQueue< T > queue )
      {
      mQueue = queue;
      mPosition = 0;
      }

   private NetInQueue< T >       mQueue;
   private int                   mPosition;
   private ByteArrayOutputStream mBaos;
   private ByteBuffer            mBuffer;
   }

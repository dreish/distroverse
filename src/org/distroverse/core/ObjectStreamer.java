package org.distroverse.core;

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
      }
   
   private NetOutQueue< T > mQueue;
   
   }

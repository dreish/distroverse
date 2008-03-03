package org.distroverse.core;

import java.util.*;

/**
 * A NetOutQueue is a queue of objects waiting to be sent through a
 * network connection.
 * @author dreish
 */
public class NetOutQueue<T>
   {
   public NetOutQueue()
      {
      mContents = new LinkedList<T>();
      }
   
   public void add( T o )
      {
      
      }
   
   public boolean offer( T o )
      {
      
      }
   
   public int size()
      {
      return mContents.size();
      }
   
   /* Plan:
    * - This object should have a reference to the network connection,
    * and should turn on and off the waiting-for-readability status
    * - Add a method to get as much as the next N bytes (or just
    * something that takes a ByteBuffer and fills/flips it)
    */

   LinkedList<T> mContents;
   }

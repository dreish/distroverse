package org.distroverse.core;

import java.util.*;

/**
 * A NetOutQueue is a queue of objects waiting to be sent through a
 * network connection.
 * @author dreish
 */
public class NetOutQueue
   {
   public NetOutQueue()
      {
      mContents = new LinkedList< Object >();
      }
   
   public void add( Object o )
      {
      
      }
   
   public boolean offer( Object o )
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

   LinkedList< Object > mContents;
   }

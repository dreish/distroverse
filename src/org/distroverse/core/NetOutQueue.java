package org.distroverse.core;

import java.util.*;
import org.distroverse.core.*;

/**
 * A NetOutQueue is a queue of objects waiting to be sent through a
 * network connection. A NetOutQueue tells a network socket, when it has
 * data to send, to monitor that network connection for writability
 * status.
 * 
 * @author dreish
 */
public class NetOutQueue< T >
   {
   public NetOutQueue( ObjectStreamer< T > os )
      {
      mContents       = new LinkedList< T >();
      mObjectStreamer = os;
      os.setQueue( this );
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

   private LinkedList< T >     mContents;
   private ObjectStreamer< T > mObjectStreamer;
   }

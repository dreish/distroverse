package org.distroverse.core.net;

import java.util.*;
import java.nio.*;
import java.nio.channels.*;

/**
 * A NetOutQueue is a queue of objects waiting to be sent through a
 * network connection. A NetOutQueue tells a network socket, when it has
 * data to send, to monitor that network connection for writability
 * status.  A NetOutQueue is constructed with an ObjectStreamer subclass
 * that determines how add()ed objects are encoded.  It is expected that
 * multiple threads will need to add and remove from the queue, so all
 * methods are synchronized.
 * 
 * @author dreish
 */
public class NetOutQueue< T >
   {
   public NetOutQueue( ObjectStreamer< T > os,
                       int max_length,
                       Selector s,
                       SocketChannel client )
      {
      mContents       = new LinkedList< T >();
      mMaxLength      = max_length;
      mObjectStreamer = os;
      os.setQueue( this );
      mWriterKey      = null;
      mSelector       = s;
      mClient         = client;
      }
   
   public void add( T o )
   throws ClosedChannelException
      {
      if ( ! offer( o ) )
         throw new BufferOverflowException();
      }
   
   synchronized public boolean offer( T o )
   throws ClosedChannelException
      {
      if ( mContents.size() >= mMaxLength )
         return false;
      mContents.add( o );
      if ( mContents.size() == 1 )
         activateNetworkWriter();
      return true;
      }
   
   synchronized public T remove()
      {
      return mContents.remove();
      }
   
   // FIXME duplicate with NetInQueue
   synchronized public int size()
      {
      return mContents.size();
      }
   
   synchronized public void stopNetworkWriter()
      {
      if ( mWriterKey != null )
         mWriterKey.cancel();
      mWriterKey = null;
      }
   
   synchronized private void activateNetworkWriter()
   throws ClosedChannelException
      {
      if ( mWriterKey == null )
         {
         mWriterKey = mClient.register( mSelector,
                                        SelectionKey.OP_WRITE );
         mWriterKey.attach( mNetSession );
         }
      }

   // FIXME duplicate with NetInQueue
   public void setSession( NetSession< T > ns )
      {  mNetSession = ns;  }
   public ObjectStreamer< T > getStreamer()
      {  return mObjectStreamer;  }

   /* Plan:
    * - This object should have a reference to the network connection,
    * and should turn on and off the waiting-for-readability status
    * - Add a method to get as much as the next N bytes (or just
    * something that takes a ByteBuffer and fills/flips it)
    */

   private LinkedList< T >     mContents;
   private int                 mMaxLength;
   private ObjectStreamer< T > mObjectStreamer;
   private SelectionKey        mWriterKey;
   private Selector            mSelector;
   private SocketChannel       mClient;
   private NetSession< T >     mNetSession;
   }

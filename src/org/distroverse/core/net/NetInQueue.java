package org.distroverse.core.net;

import java.util.*;
import java.nio.*;
import java.nio.channels.*;

import org.distroverse.core.*;

// TODO Factor out the copy/paste code from NetOutQueue.
public class NetInQueue< T >
   {
   public NetInQueue( ObjectParser< T > op,
                      int max_length,
                      Selector s,
                      SocketChannel client )
      {
      mContents         = new LinkedList< T >();
      mQueueWatchers    = new LinkedList< NetInQueueWatcher< T > >();
      mMaxLength        = max_length;
      mObjectParser = op;
      op.setQueue( this );
      mReaderKey        = null;
      mSelector         = s;
      mClient           = client;
      }

   /**
    * add() always adds an object to the queue, even if it is larger
    * than the maximum length chosen when the queue was constructed.
    * Maximum length is enforced by stopping the network reader when
    * bytes are added while the queue is full.
    * @param o
    */
   synchronized public void add( T o )
      {
      mContents.add( o );
      if ( mContents.size() == 1 )
         activateQueueWatcher();
      }

   synchronized public T remove()
   throws ClosedChannelException
      {
      if ( mReaderKey == null  
           &&  mContents.size() < mMaxLength )
         activateNetworkReader();
      return mContents.remove();
      }
   
   synchronized public int size()
      {
      return mContents.size();
      }
   
   synchronized public void readBytes( ByteBuffer b )
   throws Exception
      {
      mObjectParser.readBytes( b );
      }
   
   synchronized public void read() throws Exception
      {
      mObjectParser.read( mClient );
      }

   synchronized public void stopNetworkReader()
      {
      Log.p( "stopNetworkReader() called", Log.NET, -50 );
      if ( mReaderKey != null )
         mReaderKey.interestOps( 0 );
//         mReaderKey.cancel();
//      mReaderKey = null;
      }
   
   synchronized public void activateNetworkReader()
   throws ClosedChannelException
      {
      if ( mReaderKey == null )
         {
         mReaderKey = mClient.register( mSelector,
                                        SelectionKey.OP_READ );
         mReaderKey.attach( mNetSession );
         }
      else
         mReaderKey.interestOps( SelectionKey.OP_READ );
      }

   public void resetNetworkReader()
   throws ClosedChannelException
      {
      stopNetworkReader();
      activateNetworkReader();
      }
   
   synchronized public void addQueueWatcher( NetInQueueWatcher< T > t )
      {  mQueueWatchers.add( t );  }
   synchronized public boolean 
   removeQueueWatcher( NetInQueueWatcher< T > t )
      {  return mQueueWatchers.remove( t );  }
   public void setSession( NetSession< T > ns )
      {  mNetSession = ns;  }
   public NetSession< T > getSession()
      {  return mNetSession;  }
   public SocketChannel getClient()
      {  return mClient;  }
   
   synchronized public void activateQueueWatcher()
      {
      // TODO actually allow more than one queue reader thread - a pool?
      if ( mQueueWatchers.size() > 0 )
         mQueueWatchers.getFirst().interrupt();
      }

   private LinkedList< T >      mContents;
   private LinkedList< NetInQueueWatcher< T > > 
      mQueueWatchers;
   private int                  mMaxLength;
   private ObjectParser< T >    mObjectParser;
   private SelectionKey         mReaderKey;
   private Selector             mSelector;
   private SocketChannel        mClient;
   private NetSession< T >      mNetSession;
   }

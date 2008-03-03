package org.distroverse.core;

import java.util.*;
import java.nio.*;
import java.nio.channels.*;

// TODO Factor out the copy/paste code from NetOutQueue.
public class NetInQueue< T >
   {
   public NetInQueue( ObjectParser< T > op,
                      int max_length,
                      Selector s,
                      SocketChannel client )
      {
      mContents         = new LinkedList< T >();
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
      }

   synchronized public T remove()
   throws ClosedChannelException
      {
      if ( mReaderKey == null  
           &&  mContents.size() <= mMaxLength )
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

   synchronized public void stopNetworkReader()
      {
      if ( mReaderKey != null )
         mReaderKey.cancel();
      mReaderKey = null;
      }
   
   synchronized public void activateNetworkReader()
   throws ClosedChannelException
      {
      if ( mReaderKey == null )
         {
         mReaderKey = mClient.register( mSelector,
                                        SelectionKey.OP_READ );
         mReaderKey.attach( mObjectParser );
         }
      }
   
   synchronized public void addQueueReader( Thread t )
      {  mQueueReaders.add( t );  }
   synchronized public boolean removeQueueReader( Thread t )
      {  return mQueueReaders.remove( t );  }
   
   synchronized public void activateQueueReader()
      {
      // TODO actually allow more than one queue reader thread - a pool?
      if ( mQueueReaders.size() > 0 )
         mQueueReaders.getFirst().interrupt();
      }

   private LinkedList< T >      mContents;
   private LinkedList< Thread > 
      mQueueReaders;
   private int                  mMaxLength;
   private ObjectParser< T >    mObjectParser;
   private SelectionKey         mReaderKey;
   private Selector             mSelector;
   private SocketChannel        mClient;
   }

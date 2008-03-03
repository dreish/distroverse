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
   
   synchronized public T remove()
      {
      return mContents.remove();
      }
   
   synchronized public int size()
      {
      return mContents.size();
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

   private LinkedList< T >   mContents;
   private int               mMaxLength;
   private ObjectParser< T > mObjectParser;
   private SelectionKey      mReaderKey;
   private Selector          mSelector;
   private SocketChannel     mClient;
   }

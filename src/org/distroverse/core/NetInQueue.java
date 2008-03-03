package org.distroverse.core;

import java.util.*;
import java.nio.*;
import java.nio.channels.*;

// TODO Factor out the copy/paste code from NetOutQueue.
public class NetInQueue< T >
   {
   public NetInQueue( ObjectRecognizer< T > or,
                      int max_length,
                      Selector s,
                      SocketChannel client )
      {
      mContents         = new LinkedList< T >();
      mMaxLength        = max_length;
      mObjectRecognizer = or;
      or.setQueue( this );
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
         mReaderKey.attach( mObjectRecognizer );
         }
      }

   private LinkedList< T >       mContents;
   private int                   mMaxLength;
   private ObjectRecognizer< T > mObjectRecognizer;
   private SelectionKey          mReaderKey;
   private Selector              mSelector;
   private SocketChannel         mClient;
   }

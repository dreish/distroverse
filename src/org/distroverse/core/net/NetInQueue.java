/*
 * <copyleft>
 *
 * Copyright 2007-2008 Dan Reish
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or
 * combining it with Clojure (or a modified version of that program)
 * or clojure-contrib (or a modified version of that library),
 * containing parts covered by the terms of the Common Public License,
 * the licensors of this Program grant you additional permission to
 * convey the resulting work. {Corresponding Source for a non-source
 * form of such a combination shall include the source code for the
 * parts of Clojure and clojure-contrib used as well as that of the
 * covered work.}
 *
 * </copyleft>
 */
package org.distroverse.core.net;

import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

import org.distroverse.core.Log;

// TODO Factor out the copy/paste code from NetOutQueue.
public class NetInQueue< T >
   {
   public NetInQueue( ObjectParser< T > op,
                      int max_length,
                      Selector s,
                      SocketChannel remote,
                      DvtpMultiplexedConnection< T, ?, ? > multiplexer )
      {
      mContents         = new LinkedList< T >();
      mQueueWatchers    = new LinkedList< NetInQueueWatcher< T > >();
      mMaxLength        = max_length;
      mObjectParser = op;
      op.setQueue( this );
      mReaderKey        = null;
      mSelector         = s;
      mRemote           = remote;
      mMultiplexer      = multiplexer;
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
      mObjectParser.read( mRemote );
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
         synchronized ( mRemote )
            {
            // FIXME Rewrite using a mMultiplexer.withPausedSelector()
            ReadLock l = mMultiplexer.pauseGetLock();
            mReaderKey = mRemote.register( mSelector,
                                           SelectionKey.OP_READ );
            mMultiplexer.unpause( l );
            mReaderKey.attach( mNetSession );
            }
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
   public SocketChannel getRemote()
      {  return mRemote;  }

   synchronized public void activateQueueWatcher()
      {
      // TODO actually allow more than one queue reader thread - a pool?
      if ( mQueueWatchers.size() > 0 )
         mQueueWatchers.getFirst().interrupt();
      }

   private LinkedList< T >                        mContents;
   private LinkedList< NetInQueueWatcher< T > >   mQueueWatchers;
   private int                                    mMaxLength;
   private ObjectParser< T >                      mObjectParser;
   private SelectionKey                           mReaderKey;
   private Selector                               mSelector;
   private SocketChannel                          mRemote;
   private NetSession< T >                        mNetSession;
   private DvtpMultiplexedConnection< T, ?, ? >   mMultiplexer;
   }

/*
 * <copyleft>
 *
 * Copyright 2007-2009 Dan Reish
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
 * containing parts covered by the terms of the Eclipse Public
 * License, the licensors of this Program grant you additional
 * permission to convey the resulting work. {Corresponding Source for
 * a non-source form of such a combination shall include the source
 * code for the parts of Clojure and clojure-contrib used as well as
 * that of the covered work.}
 *
 * </copyleft>
 */
package org.distroverse.core.net;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.net.InetSocketAddress;
import java.nio.*;
import java.nio.channels.*;

import org.distroverse.core.*;

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
                       SocketChannel client,
                       DvtpMultiplexedConnection< T, ?, ? > mplexer )
      {
      mContents       = new LinkedList< T >();
      mMaxLength      = max_length;
      mObjectStreamer = os;
      os.setQueue( this );
      mWriterKey      = null;
      mSelector       = s;
      mRemote         = client;
      mMultiplexer    = mplexer;
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
      Log.p( "offer on queue " + this + ": " + o, Log.NET, -10 );
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

   // TODO duplicate with NetInQueue; factor out
   synchronized public int size()
      {
      return mContents.size();
      }

   synchronized public void stopNetworkWriter()
      {
      Log.p( "stopNetworkWriter() called", Log.NET, -150 );
      if ( mWriterKey != null )
         mWriterKey.interestOps( 0 );
//         mWriterKey.cancel();
//      mWriterKey = null;
      // FIXME No idea why this is necessary; yuck:
      try
         {  mNetSession.getNetInQueue().resetNetworkReader();  }
      catch ( ClosedChannelException e )
         {
         Log.p( "In stopNetworkWriter(): " + e, Log.NET, 20 );
         Log.p( e, Log.NET, 5 );
         }
      }

   /**
    * This only gets called when objects are added to the queue.
    * @throws ClosedChannelException
    */
   synchronized private void activateNetworkWriter()
   throws ClosedChannelException
      {
      if ( mWriterKey == null )
         {
         // FIXME Rewrite using a mMultiplexer.withPausedSelector()
         ReadLock l = mMultiplexer.pauseGetLock();
         mWriterKey = mRemote.register( mSelector,
                                        SelectionKey.OP_WRITE );
         mMultiplexer.unpause( l );
         mWriterKey.attach( mNetSession );
         }
      else
         mWriterKey.interestOps( SelectionKey.OP_WRITE );
      }

   public void write() throws Exception
      {
      mObjectStreamer.write( mRemote );
      }

   public void setSession( NetSession< T > ns )
      {  mNetSession = ns;  }
   public NetSession< T > getSession()
      {  return mNetSession;  }
   public ObjectStreamer< T > getStreamer()
      {  return mObjectStreamer;  }

   public InetSocketAddress getPeerAddress()
      {
      return (InetSocketAddress) 
             mRemote.socket().getRemoteSocketAddress();
      }

   /**
    * Objects waiting to be sent
    */
   private LinkedList< T >     mContents;
   /**
    * Length limit before further adds fail
    */
   private int                 mMaxLength;
   /**
    * An object that can convert objects of type T into bytes
    */
   private ObjectStreamer< T > mObjectStreamer;
   /**
    * The key that registers this NetOutQueue's interest in writing
    * bytes to mRemote
    */
   private SelectionKey        mWriterKey;
   /**
    * The Selector through which input and output are multiplexed
    */
   private Selector            mSelector;
   /**
    * The connection to the remote (receiving) socket
    */
   private SocketChannel       mRemote;
   /**
    * The session associated with this connection.
    */
   private NetSession< T >     mNetSession;
   /**
    * The I/O multiplexer handling this session.
    */
   private DvtpMultiplexedConnection< T, ?, ? > mMultiplexer;
   }

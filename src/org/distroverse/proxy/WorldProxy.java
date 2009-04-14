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
package org.distroverse.proxy;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.distroverse.core.Log;
import org.distroverse.core.NodeTreeUtils;
import org.distroverse.core.net.NetSession;
import org.distroverse.dvtp.ClientSendable;
import org.distroverse.dvtp.DList;
import org.distroverse.dvtp.DNode;
import org.distroverse.dvtp.DNodeRef;
import org.distroverse.dvtp.DvtpExternalizable;
import org.distroverse.dvtp.FunCall;
import org.distroverse.dvtp.FunRet;
import org.distroverse.dvtp.Str;
import org.distroverse.dvtp.ULong;

import com.jme.math.Vector3f;

/**
 *
 * @author dreish
 */
public class WorldProxy extends SingleServerProxyBase
   {
   /**
    * @throws IOException
    */
   public WorldProxy() throws IOException
      {
      super();
      mAvatar = null;
      mDetail = new AtomicReference< Float >();
      mRequestedNodes = new AtomicInteger();
      }

   /* (non-Javadoc)
    * @see org.distroverse.proxy.SingleServerProxyBase#initWorld()
    */
   @Override
   protected void initWorld() throws IOException
      {
      // TODO Auto-generated method stub

      }

   /* (non-Javadoc)
    * @see org.distroverse.proxy.NetProxyBase#receiveFromClient(org.distroverse.dvtp.ClientSendable)
    */
   @Override
   protected void receiveFromClient( ClientSendable o )
      {
      switch ( o.getClassNumber() )
         {
         default:
            Log.p( "Unrecognized message from client",
                   Log.UNHANDLED, -10 );
         }
      }

   /* (non-Javadoc)
    * @see org.distroverse.proxy.NetProxyBase#receiveFromServer(org.distroverse.core.net.NetSession, org.distroverse.dvtp.DvtpExternalizable)
    */
   @Override
   public void receiveFromServer( NetSession< Object > s,
                                  DvtpExternalizable o )
   throws ClosedChannelException
      {
      switch ( o.getClassNumber() )
         {
         case 30:
            receiveDNodeFromServer( s, (DNode) o );
            break;
         case 128:
            receiveDListFromServer( s, (DList) o );
            break;
         case 129:
            receiveFunCallFromServer( s, (FunCall) o );
            break;
         default:
            Log.p( "Unrecognized message from server",
                   Log.NET | Log.UNHANDLED | Log.SERVER, 10 );
         }
      }

   private static Str STR_RN = new Str( "rn" );
   private void receiveDListFromServer( NetSession< Object > s,
                                        DList o )
   throws ClosedChannelException
      {
      if ( o.getContents( 0 ).equals( STR_RN ) )
         {
         // DNode sent in response to a request
         mRequestedNodes.decrementAndGet();
         receiveFromServer( s, o.getContents( 1 ) );
         }
      }

   private void receiveDNodeFromServer( NetSession< Object > s,
                                        DNode dn )
      {
      addCache( dn );
      displayNode( dn );

      if ( withinView( dn ) )
         maybeGetChildren( s, dn );
      }

   private boolean withinView( DNode dn )
      {
      // TODO Auto-generated method stub

      return false;
      }

   private void maybeGetChildren( NetSession< Object > s, DNode dn )
      {
      if ( visibleTo( mAvatar, dn, mDetail.get() ) )
         for ( int i = 0; i < dn.getNumChildren(); ++i )
            {
            DNodeRef ch = dn.getChild( i );
            DNode cached = getCachedNode( ch );

            if ( cached != null )
               {
               BigDecimal server_last_changed
                  = ch.getLastChanged().toBigDecimal();
               BigDecimal cache_last_changed
                  = cached.getThisRef().getLastChanged().toBigDecimal();

               if ( server_last_changed.compareTo( cache_last_changed )
                     > 0 )
                  requestNode( s, ch );
               }
            else
               requestNode( s, ch );
            }
      }

   private void displayNode( DNode dn )
      {
      // TODO Auto-generated method stub
      long client_id_for_node
         = serverIdToClient( dn.getThisRef().getId(),
                             dn.getThisRef().getRemoteHost() );
      }

   public long serverIdToClient( long id, String remoteHost )
      {
      // XXX If remoteHost == mMainHost:
      return id;
      // XXX else, map it to something
      }

   /**
    * Request a node if its parent node is visible to mAvatar.
    * @param s
    * @param child
    */
   private void requestNode( NetSession< Object > s, DNodeRef child )
      {
      // TODO send a get-node query to the server

      mRequestedNodes.incrementAndGet();
      }

   /**
    * Returns true if the given node dn is visible to the given avatar
    * at the given detail level, _or_ if it is unable to determine this
    * for any reason, such as a missing avatar or connecting node.
    * @param avatar
    * @param dn
    * @param detail
    * @return
    */
   private boolean visibleTo( DNodeRef avatar_ref, DNode dn,
                              float detail )
      {
      DNode avatar = getCachedNode( avatar_ref );
      if ( avatar == null )
         return true;

      Vector3f vec_to = NodeTreeUtils.vectorTo( avatar, dn );
      float target_size = dn.getRadius();
      float target_range = dn.getMoveSeq().getRange();
      float min_distance = vec_to.length() - target_range;
      return min_distance
             < WorldProxy.visibleDistance( detail, target_size );
      }

   private void addCache( DNode dn )
      {
      // TODO -- caching
      }

   private DNode getCachedNode( DNodeRef ch )
      {
      // TODO -- caching
      return null;
      }

   private void receiveFunCallFromServer( NetSession< Object > s,
                                          FunCall fc )
   throws ClosedChannelException
      {
      ULong fc_id = (ULong) fc.getContents( 0 );

      FunRet fr = callFun( fc, fc_id );
      if ( fc_id.toLong() != 0 )
         s.getNetOutQueue().add( fr );
      }

   private FunRet callFun( FunCall fc, ULong fc_id )
      {
      Str fc_name = (Str) fc.getContents( 1 );

      // FIXME Use a hash for dispatch
      if ( fc_name.equals( "set-avatar" ) )
         {
         long avatar_id = ((ULong) fc.getContents( 2 )).toLong();
         mAvatar = new DNodeRef( null, avatar_id, null, null );

         return new FunRet( fc_id );
         }

      return null;
      }

   /**
    * Returns the maximum distance at which an object of size
    * target_size can be seen at the given detail level.
    * @param detail
    * @param target_size
    * @return
    */
   public static float visibleDistance( float detail,
                                        float target_size )
      {
      // TODO Auto-generated method stub
      return 0;
      }

   private DNodeRef mAvatar;
   private final AtomicInteger mRequestedNodes;
   private final AtomicReference< Float > mDetail;
   }

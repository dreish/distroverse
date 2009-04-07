/**
 *
 */
package org.distroverse.proxy;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.atomic.AtomicInteger;

import org.distroverse.core.Log;
import org.distroverse.core.Util;
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
         getChildren( s, dn );
      }

   private boolean withinView( DNode dn )
      {
      // TODO Auto-generated method stub

      return false;
      }

   private void getChildren( NetSession< Object > s, DNode dn )
      {
      // TODO get node transform to avatar; pass to requestNode

      for ( int i = 0; i < dn.getNumChildren(); ++i )
         {
         DNodeRef ch = dn.getChild( i );
         DNode cached = getCache( ch );

         if ( cached != null )
            {
            BigDecimal server_last_changed
               = ch.getLastChanged().toBigDecimal();
            BigDecimal cache_last_changed
               = cached.getThisRef().getLastChanged().toBigDecimal();

            if ( server_last_changed.compareTo( cache_last_changed )
                 > 0 )
               {
               requestNode( s, ch );
               }
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

   private void requestNode( NetSession< Object > s, DNodeRef ch )
      {
      // TODO trim based on transform-to-avatar and detail level
      // TODO send a get-node query to the server

      mRequestedNodes.incrementAndGet();
      }

   private void addCache( DNode dn )
      {
      // TODO -- caching
      }

   private DNode getCache( DNodeRef ch )
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

   private DNodeRef mAvatar;
   private AtomicInteger mRequestedNodes;
   }

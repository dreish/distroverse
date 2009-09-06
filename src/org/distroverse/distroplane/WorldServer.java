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
package org.distroverse.distroplane;

import java.io.IOException;

import org.distroverse.core.Log;
import org.distroverse.core.net.NetOutQueue;
import org.distroverse.core.net.NetSession;
import org.distroverse.distroplane.lib.DvtpListener;
import org.distroverse.distroplane.lib.DvtpServer;

import clojure.lang.RT;
import clojure.lang.Var;

/**
 * @author dreish
 *
 */
public class WorldServer extends DvtpServer
   {
   /**
    * @param listener
    */
   public WorldServer( DvtpListener listener )
      {
      super( listener );
      }

   /* (non-Javadoc)
    * @see org.distroverse.distroplane.lib.DvtpServer#handleGet(java.lang.String, org.distroverse.core.net.NetOutQueue)
    */
   @Override
   public void handleGet( String url, NetOutQueue< Object > noq )
      {
      try
         {
         mHandleGet.invoke( noq, url );
         }
      catch ( Exception e )
         {
         Log.p( e, Log.SERVER | Log.UNHANDLED, 100 );
         Log.p( "(handle-get!) must not throw exceptions",
                Log.SERVER | Log.UNHANDLED, 100 );
         e.printStackTrace();
         }
      }

   /* (non-Javadoc)
    * @see org.distroverse.distroplane.lib.DvtpServer#handleLocation(java.lang.String, org.distroverse.core.net.NetOutQueue)
    */
   @Override
   public void handleLocation( String location,
                               NetOutQueue< Object > noq )
      {
      try
         {
         mHandleLocation.invoke( noq, location );
         }
      catch ( Exception e )
         {
         Log.p( e, Log.SERVER | Log.UNHANDLED, 100 );
         Log.p( "(handle-location!) must not throw exceptions",
                Log.SERVER | Log.UNHANDLED, 100 );
         e.printStackTrace();
         }
      }

   /* (non-Javadoc)
    * @see org.distroverse.distroplane.lib.DvtpServer#handleEnvoyObject(java.lang.Object, org.distroverse.core.net.NetSession)
    */
   @Override
   public void handleEnvoyObject( Object net_in_object,
                                  NetSession< Object > session )
      {
      try
         {
         mHandleObjectBang.invoke( session,
                                   session.getAttachment(),
                                   net_in_object );
         }
      catch ( Exception e )
         {
         Log.p( e, Log.SERVER | Log.UNHANDLED, 100 );
         Log.p( "(handle-object!) must not throw exceptions",
                Log.SERVER | Log.UNHANDLED, 100 );
         e.printStackTrace();
         try
            {
            session.close();
            }
         catch ( IOException e2 )
            {
            Log.p( e2, Log.SERVER | Log.UNHANDLED, 100 );
            Log.p( "(handle-object!) exception: got another exception"
                   + "while closing the channel!",
                   Log.SERVER | Log.UNHANDLED, 100 );
            }
         }
      }

   /* (non-Javadoc)
    * @see org.distroverse.distroplane.lib.DvtpServer#handleEnvoyOpen(java.lang.String, org.distroverse.core.net.NetOutQueue)
    */
   @Override
   public void handleEnvoyOpen( String token,
                                NetOutQueue< Object > noq )
      {
      NetSession< Object > ns = noq.getSession();
//      WorldSession ws = ns.setAttachment( WorldSession.class,
//                                          new WorldSession( ns ) );
      try
         {
         // Down the rabbit hole.
         mInitConnectionBang.invoke( ns, token );
         }
      catch ( Exception e )
         {
         e.printStackTrace();
         try
            {
            Log.p( e, Log.SERVER | Log.UNHANDLED, 100 );
            Log.p( "(init-connection!) must not throw exceptions",
                   Log.SERVER | Log.UNHANDLED, 100 );
            ns.close();
            }
         catch ( IOException e2 )
            {
            Log.p( e2, Log.SERVER | Log.UNHANDLED, 100 );
            Log.p( "(init-connection!) exception: got another exception"
                   + "while closing the channel!",
                   Log.SERVER | Log.UNHANDLED, 100 );
            e2.printStackTrace();
            }
         }
      }

   /**
    * @param args - ignored
    */
   public static void main( String[] args )
      {
      Var startup_bang;
      try
         {
         RT.loadResourceScript( "world-server.clj" );
         final String ws_ns = "world-server";
         mInitConnectionBang = RT.var( ws_ns, "init-connection!" );
         mHandleObjectBang   = RT.var( ws_ns, "handle-object!" );
         mHandleGet          = RT.var( ws_ns, "handle-get" );
         mHandleLocation     = RT.var( ws_ns, "handle-location" );
         startup_bang        = RT.var( ws_ns, "startup!" );
         }
      catch ( Exception e )
         {
         e.printStackTrace();
         return;
         }
      startup_bang.run();
      mServer = createServer( WorldServer.class,
                                   "DVTP/0.01 WorldServer 0.02" );
      mServer.serve();
      }
   
   public static boolean shutdownInstance()
      {
      return mServer.shutdown();
      }
   
   private static Var mInitConnectionBang;
   private static Var mHandleObjectBang;
   private static Var mHandleGet;
   private static Var mHandleLocation;
   private static DvtpServer mServer;
   }

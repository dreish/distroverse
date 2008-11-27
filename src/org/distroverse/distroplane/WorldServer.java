/* <copyleft>
 *
 * Copyright 2008 Dan Reish
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
 * combining it with clojure-contrib (or a modified version of that
 * library), containing parts covered by the terms of the Common
 * Public License, the licensors of this Program grant you additional
 * permission to convey the resulting work. {Corresponding Source for
 * a non-source form of such a combination shall include the source
 * code for the parts of clojure-contrib used as well as that of the
 * covered work.}
 *
 * </copyleft>
 */
package org.distroverse.distroplane;

import java.io.IOException;

import org.distroverse.core.net.NetOutQueue;
import org.distroverse.core.net.NetSession;
import org.distroverse.distroplane.lib.DvtpListener;
import org.distroverse.distroplane.lib.DvtpServer;

import clojure.lang.RT;

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
   throws IOException
      {
      // TODO Auto-generated method stub

      }

   /* (non-Javadoc)
    * @see org.distroverse.distroplane.lib.DvtpServer#handleLocation(java.lang.String, org.distroverse.core.net.NetOutQueue)
    */
   @Override
   public void handleLocation( String location,
                               NetOutQueue< Object > noq )
   throws IOException
      {
      // TODO Auto-generated method stub

      }

   /* (non-Javadoc)
    * @see org.distroverse.distroplane.lib.DvtpServer#handleProxyObject(java.lang.Object, org.distroverse.core.net.NetSession)
    */
   @Override
   public void handleProxyObject( Object net_in_object,
                                  NetSession< Object > session )
   throws IOException
      {
      // TODO Auto-generated method stub

      }

   /* (non-Javadoc)
    * @see org.distroverse.distroplane.lib.DvtpServer#handleProxyOpen(java.lang.String, org.distroverse.core.net.NetOutQueue)
    */
   @Override
   public void handleProxyOpen( String token,
                                NetOutQueue< Object > noq )
      {
      NetSession< Object > ns = noq.getSession();
      WorldSession ws =
         noq.getSession().setAttachment( WorldSession.class,
                                         new WorldSession( ns ) );
      try
         {
         ws.start();
         }
      catch ( Exception e )
         {
         e.printStackTrace();
         ns.close();
         }
      }

   /**
    * @param args - ignored
    */
   public static void main( String[] args )
      {
      try
         {  RT.loadResourceScript( "world-server.clj" );  }
      catch ( Exception e )
         {
         e.printStackTrace();
         return;
         }
      createServer( WorldServer.class,
                    "DVTP/0.01 WorldServer 0.02" );
      }
   }

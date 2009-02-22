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
package org.distroverse.helloserver;

import java.io.IOException;

import org.distroverse.core.net.NetOutQueue;
import org.distroverse.core.net.NetSession;
import org.distroverse.distroplane.lib.DvtpListener;
import org.distroverse.distroplane.lib.DvtpServer;
import org.distroverse.distroplane.lib.SUtil;
import org.distroverse.dvtp.Err;
import org.distroverse.dvtp.ProxySpec;

public class HelloSimpleServer extends DvtpServer
   {
   public HelloSimpleServer( DvtpListener l )
      {
      super( l );
      }

   @Override
   public void handleGet( String url, NetOutQueue< Object > noq )
   throws IOException
      {
      if ( url.equals( "drtp://localhost/HelloSimpleProxy.jar" ) )
         {
         SUtil.sendFile( "HelloSimpleProxy.jar", url, noq );
         }
      else
         {
         noq.add( new Err( "Resource not found: " + url, 404 ) );
         }
      }

   @Override
   public void handleLocation( String location,
                               NetOutQueue< Object > noq )
   throws IOException
      {
      noq.add( new ProxySpec( "drtp://localhost/HelloSimpleProxy.jar",
                              ".*",
                          "org.distroverse.proxy.HelloSimpleProxy" ) );
      }

   @Override
   public void handleProxyObject( Object net_in_object,
                                  NetSession< Object > session )
   throws IOException
      {
      /* This server only hands out self-sufficient proxies.  There's no
       * way to establish proxy connections.
       */
      throw new IOException( "handleProxyObject() unimplemented" );
      }

   @Override
   public void handleProxyOpen( String token, 
                                NetOutQueue< Object > noq )
   throws IOException
      {
      // This server only hands out self-sufficient proxies.
      noq.add( new Err( "PROXYOPEN not implemented", 501 ) );
      }

   /**
    * @param args
    */
   public static void main( String[] args )
      {
      createServer( HelloSimpleServer.class,
                    "DVTP/0.01 HelloSimpleServer 1.0.0" );
      }
   }

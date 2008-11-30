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
package org.distroverse.helloserver;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;

import org.distroverse.core.net.NetOutQueue;
import org.distroverse.core.net.NetSession;
import org.distroverse.distroplane.lib.DvtpListener;
import org.distroverse.distroplane.lib.DvtpServer;
import org.distroverse.distroplane.lib.PrimFactory;
import org.distroverse.distroplane.lib.SUtil;
import org.distroverse.distroplane.lib.PrimFactory.PrimShape;
import org.distroverse.dvtp.AddObject;
import org.distroverse.dvtp.Err;
import org.distroverse.dvtp.MoveSeq;
import org.distroverse.dvtp.ProxySpec;
import org.distroverse.dvtp.Shape;
import org.distroverse.dvtp.ULong;
import org.distroverse.dvtp.WarpSeq;
import org.distroverse.viewer.VUtil;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

public class HelloContentServer extends DvtpServer
   {
   public HelloContentServer( DvtpListener listener )
      {
      super( listener );
      }

   @Override
   public void handleGet( String url, NetOutQueue< Object > noq )
   throws IOException
      {
      if ( url.matches( "drtp://.*/PassThroughProxy.jar" ) )
         {
         SUtil.sendFile( "PassThroughProxy.jar", url, noq );
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
      noq.add( new ProxySpec( "drtp://localhost/PassThroughProxy.jar",
                              ".*",
                          "org.distroverse.proxy.PassThroughProxy" ) );
      }

   @Override
   public void handleProxyObject( Object net_in_object,
                                  NetSession< Object > session )
   throws IOException
      {
      // This server never enters proxy mode.
      throw new IOException( "handleProxyObject() unimplemented" );
      }

   @Override
   public void handleProxyOpen( String token,
                                NetOutQueue< Object > noq )
   throws IOException
      {
      PrimFactory pf = new PrimFactory();
      pf.setDims( 10.0, 1.0, 1.0 );
      addObject( noq,
                 pf.setPrimShape( PrimShape.PYRAMID ).generate(), 2L,
                 +00.0f, 10.0f, 40.0f );
      addObject( noq,
                 pf.setPrimShape( PrimShape.SPHERE  ).generate(), 1L,
                 -30.0f, 10.0f, 40.0f );
      addObject( noq,
                 pf.setPrimShape( PrimShape.CUBOID  ).generate(), 3L,
                 +30.0f, 10.0f, 40.0f );
      noq.getSession().close();
      }

   protected void addObject( NetOutQueue< Object > noq, Shape s,
                             ULong id, ULong pid, MoveSeq m )
   throws ClosedChannelException
      {
      noq.add( new AddObject( true, s, id, pid, m, new WarpSeq() ) );
      }

   // XXX This is mostly duplicated with ProxyBase.
   protected void addObject( NetOutQueue< Object > noq, Shape s,
                             long id, float x, float y, float z )
   throws ClosedChannelException
      {
      // XXX - Probably bad form to call something in VUtil from here.
      addObject( noq, s, new ULong( id ), new ULong( 0 ),
                 VUtil.simpleMove( new Vector3f( x, y, z ),
                                   new Quaternion() ) );
      }

   /**
    * @param args
    */
   public static void main( String[] args )
      {
      createServer( HelloContentServer.class,
                    "DVTP/0.01 HelloContentServer 1.0.0" );
      }
   }

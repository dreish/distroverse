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

import org.distroverse.core.Log;
import org.distroverse.core.net.*;
import org.distroverse.distroplane.lib.*;
import java.io.*;

/**
 * An ultra-simple server to demonstrate the basic concepts of DVTP.
 * 
 * @author dreish
 */
public final class ElizaServer extends DvtpServer
   {
   public ElizaServer( DvtpListener l )
      {
      super( l );
      }
   
   /* (non-Javadoc)
    * @see org.distroverse.distroplane.lib.DvtpServer#handleGet(java.lang.String)
    */
   @Override
   public void handleGet( String url, NetOutQueue< Object > noq )
   throws IOException
      {
      Log.p( "got: get " + url, Log.SERVER, -50 );
      noq.add( "What do you expect to find at " + url + "?" );
      }

   /* (non-Javadoc)
    * @see org.distroverse.distroplane.lib.DvtpServer#handleLocation(java.lang.String)
    */
   @Override
   public void handleLocation( String location, 
                               NetOutQueue< Object > noq )
   throws IOException
      {
      Log.p( "got: location " + location, Log.SERVER, -50 );
      noq.add( "So, you want to go to " + location + "?" );
      }

   /* (non-Javadoc)
    * @see org.distroverse.distroplane.lib.DvtpServer#handleEnvoyOpen(java.lang.String)
    */
   @Override
   public void handleEnvoyOpen( String token,
                                NetOutQueue< Object > noq )
   throws IOException
      {
      Log.p( "got: envoyopen " + token, Log.SERVER, -50 );
      noq.add( "Tell me more about " + token + "." );
//      // This is how we would normally handle the ENVOYOPEN command:
//      noq.getSession().setAttachment( EnvoySession.class,
//                                      new EnvoySession() );
//      noq.getSession().setEnvoyMode();
//      noq.add( new True() );
      }

   /**
    * @param args
    */
   public static void main( String[] args )
      {
      createServer( ElizaServer.class,
                    "DVTP/0.01 ElizaServer 1.0.0" );
      }

   @Override
   public void handleEnvoyObject( Object net_in_object,
                                  NetSession< Object > session )
   throws IOException
      {
      throw new IOException( "How did I get here?" );
//      // Normally we would do this:
//      EnvoySession ps
//         = session.getAttachmentOrNull( EnvoySession.class );
//      // And using some combination of the session data in ps and the
//      // global data in this class, react to the object.
      }
   }

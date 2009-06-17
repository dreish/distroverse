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
/*
 *
 */
package org.distroverse.distroplane.lib;

import java.io.IOException;
import java.lang.reflect.Constructor;

import org.distroverse.core.*;
import org.distroverse.core.net.*;
import org.distroverse.dvtp.Err;

/**
 * An abstract base class handling the annoying details of a DVTP
 * server.  Subclass this and implement handleLocation(), handleGet(),
 * and handleEnvoyOpen() to define your server.
 *
 * Instantiate it with an instance of a subclass of DvtpListener that
 * will define how your server handles multiple connections (e.g.,
 * multiple threads, multiplexing, or a combination), or use
 * createServer() to set up a DvtpMultiplexedListener.  (See the
 * org.distroverse.helloserver.* classes for simple examples.)
 *
 * @author dreish
 */
public abstract class DvtpServer
   {
   public final static int DEFAULT_PORT = 1808;

   /**
    *
    */
   public DvtpServer( DvtpListener listener )
      {
      mListenPort = DEFAULT_PORT;
      mListener   = listener;
      mListener.setServer( this );
      }

   /**
    * Performs the routine task of setting up and running a DvtpServer
    * instance, with a standard set of supporting objects.  Does not
    * return unless unsuccessful in constructing a server.
    * @param <S> - Type of server class, inferred from that argument
    * @param server_class - A subclass of DvtpServer
    * @param greeting - String to send to clients when they connect
    */
   public static <S extends DvtpServer> void
   createServer( Class<S> server_class, String greeting )
      {
      DvtpListener l
         = new DvtpMultiplexedListener< DvtpFlexiParser,
                                        DvtpFlexiStreamer >
               ( DvtpFlexiParser.class, DvtpFlexiStreamer.class );
      DvtpServer server;
      try
         {
         Constructor< S > server_constructor
            = server_class.getConstructor( DvtpListener.class );
         server = server_constructor.newInstance( l );
         }
      catch ( Exception e )
         {
         Log.p( "Could not construct server of class " + server_class
                + ": " + e, Log.NET, 100 );
         Log.p( e, Log.NET, 100 );
         return;
         }
      NetInQueueWatcher< Object > watcher_thread =
         new DvtpServerInQueueObjectWatcher( server );
      watcher_thread.start();
      l.setWatcher( watcher_thread );
      l.setGreeting( greeting );
      l.serve();  // Does not return.
      }

   /**
    * Call the listen() method in the DvtpListener implementation.
    */
   public void listen()
      {
      mListener.serve();
      }

   /**
    * Decodes the command and calls the appropriate handle*() function.
    * @param command
    */
   public void handleCommand( String command,
                              NetOutQueue< Object > noq )
   throws IOException
      {
      Log.p( "Received command: " + command, Log.SERVER, -20 );
      // TODO Add lambdas and/or method references to Java and rewrite
      if      ( Util.stringStartsIgnoreCase( command, "get " ) )
         handleGet( command.substring( "get ".length() ), noq );
      else if ( Util.stringStartsIgnoreCase( command, "location " ) )
         handleLocation( command.substring( "location ".length() ),
                         noq );
      else if ( Util.stringStartsIgnoreCase( command, "envoyopen " ) )
         handleEnvoyOpen( command.substring( "envoyopen ".length() ),
                          noq );
      else if ( command.equalsIgnoreCase( "envoyopen" ) )
         handleEnvoyOpen( null, noq );
      else
         handleUnrecognizedCommand( command, noq );
      }

   /**
    * Handles the LOCATION command, which tells a client the URL of the
    * server that should be used for that location URL.  It must return
    * the URL of a Java .jar file (various compressed jar extensions are
    * recognized).
    * @param location - an URL
    */
   public abstract void handleLocation( String location,
                                        NetOutQueue< Object > noq )
   throws IOException;

   /**
    * Get an arbitrary resource by URL.  This mimics the GET method of
    * an HTTP server to some extent, but a DVTP server does not ever
    * implement a POST method, and a DVTP client should not accept
    * http://... URLs as user input.
    * @param url - an URL
    */
   public abstract void handleGet( String url,
                                   NetOutQueue< Object > noq )
   throws IOException;

   /**
    * Handles the ENVOYOPEN command, which is the handshake that begins
    * a session between envoy and server.  The response, and every
    * aspect of the protocol after that point, are completely
    * @param token
    */
   public abstract void handleEnvoyOpen( String token,
                                         NetOutQueue< Object > noq )
   throws IOException;

   /**
    * Handles any arbitrary object from an envoy.  This is the only
    * method called on a DvtpServer from a session that has entered
    * envoy mode by calling setEnvoyMode() on the NetSession object.
    * @param net_in_object
    * @param session
    */
   public abstract void
   handleEnvoyObject( Object net_in_object,
                      NetSession< Object > session )
   throws IOException;

   /**
    * Handles any unrecognized command by printing an error message.
    * @param token
    */
   public void handleUnrecognizedCommand( String token,
                                          NetOutQueue< Object > noq )
   throws IOException
      {
      noq.add( new Err( "Unrecognized command: " + token, 501 ) );
      }

   /**
    * @return the listen port
    */
   public int getListenPort()  {  return mListenPort;  }

   private int          mListenPort;
   private DvtpListener mListener;
   }

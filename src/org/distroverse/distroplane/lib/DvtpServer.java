/**
 * An abstract base class handling the annoying details of a DVTP
 * server.  Subclass this and implement handleLocation(), handleGet(),
 * and handleProxyOpen() to define your server.
 * 
 * Instantiate it with an instance of a subclass of DvtpListener that
 * will define how your server handles multiple connections (e.g.,
 * multiple threads, multiplexing, or a combination).
 */
package org.distroverse.distroplane.lib;

import java.nio.*;

/**
 * @author dreish
 *
 */
public abstract class DvtpServer
   {
   public final static int DEFAULT_PORT = 808;

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
   public void handleCommand( String command, ByteBuffer output_buffer )
      {
      // This could be done more elegantly in a better language, such
      // as one with lambdas or method references.
      if      ( stringBeginsIgnoreCase( command, "get" ) )
         handleGet( command.substring( "get".length() + 1 ),
                    output_buffer );
      else if ( stringBeginsIgnoreCase( command, "location" ) )
         handleLocation( command.substring( "location".length() + 1 ),
                         output_buffer );
      else if ( stringBeginsIgnoreCase( command, "proxyopen" ) )
         handleProxyOpen( command.substring( "proxyopen".length() + 1 ),
                          output_buffer );
      else
         handleUnrecognizedCommand( command, output_buffer );
      }

   private boolean stringBeginsIgnoreCase( String full, String prefix )
      {
      return ( full.substring( 0, prefix.length() )
                   .compareToIgnoreCase( prefix )
               == 0 );
      }
   
   /**
    * Handles the LOCATION command, which tells a client the URL of the
    * server that should be used for that location URL.  It must return
    * the URL of a Java .jar file (various compressed jar extensions are
    * recognized) or a Scheme source file with an URL ending in ".szp".
    * @param location - an URL
    */
   public abstract void handleLocation( String location,
                                        ByteBuffer output_buffer );
   /**
    * Get an arbitrary resource by URL.  This mimics the GET method of
    * an HTTP server to some extent, but a DVTP server does not ever
    * implement a POST method, and a DVTP client should not accept
    * http://... URLs as user input.
    * @param url - an URL
    */
   public abstract void handleGet( String url,
                                   ByteBuffer output_buffer );
   /**
    * Handles the PROXYOPEN command, which is the handshake that begins
    * a session between proxy and server.  The response, and every
    * aspect of the protocol after that point, are completely 
    * @param token
    */
   public abstract void handleProxyOpen( String token,
                                         ByteBuffer output_buffer );
   /**
    * Handles any unrecognized command by printing an error message.
    * @param token
    */
   public void handleUnrecognizedCommand( String token,
                                          ByteBuffer output_buffer )
      {
      
      }


   /**
    * @return the listen port
    */
   public int getListenPort()  {  return mListenPort;  }

   int          mListenPort;
   DvtpListener mListener;
   }

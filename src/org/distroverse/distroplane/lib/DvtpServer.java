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
    * Handles the LOCATION command, which tells a client the URL of the
    * server that should be used for that location URL.  It must return
    * the URL of a Java .jar file (various compressed jar extensions are
    * recognized) or a Scheme source file with an URL ending in ".szp".
    * @param location - an URL
    */
   public abstract void handleLocation( String location );
   /**
    * Get an arbitrary resource by URL.  This mimics the GET method of
    * an HTTP server to some extent, but a DVTP server does not ever
    * implement a POST method, and a DVTP client should not accept
    * http://... URLs as user input.
    * @param url - an URL
    */
   public abstract void handleGet( String url );
   /**
    * Handles the PROXYOPEN command, which is the handshake that begins
    * a session between proxy and server.  The response, and every
    * aspect of the protocol after that point, are completely 
    * @param token
    */
   public abstract void handleProxyOpen( String token );

   int          mListenPort;
   DvtpListener mListener;
   }

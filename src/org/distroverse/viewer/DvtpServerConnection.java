/*
 * Copyright (c) 2007-2008 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * General Public License (GPL) version 3 or later</a>
 */
package org.distroverse.viewer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URI;

import org.distroverse.core.Util;
import org.distroverse.core.net.DvtpFlexiStreamer;
import org.distroverse.distroplane.lib.DvtpServer;
import org.distroverse.dvtp.Blob;
import org.distroverse.dvtp.CompactUlong;
import org.distroverse.dvtp.DvtpObject;
import org.distroverse.dvtp.ProxySpec;
import org.distroverse.dvtp.Str;


/**
 * A connection to a remote DVTP server, with methods for simple,
 * synchronous I/O.
 * TODO: Add timeouts to the synchronous reads.
 * @author dreish
 */
public class DvtpServerConnection
   {
   private static final double MIN_SUPPORTED_VERSION = 0.01;

   /**
    * Construct a new connection to a DVTP server, getting the hostname
    * from a URL.
    * @param url
    * @throws IOException 
    */
   public DvtpServerConnection( URI u )
   throws IOException
      {
      String hostname = u.getHost();
      int port = u.getPort();
      if ( port < 0 )
         port = DvtpServer.DEFAULT_PORT;
      
      init( hostname, port );
      }
   
   public DvtpServerConnection( String hostname, int port )
   throws IOException
      {
      init( hostname, port );
      }
   
   public DvtpServerConnection( String hostname )
   throws IOException
      {
      init( hostname, DvtpServer.DEFAULT_PORT );
      }
   
   private void init( String hostname, int port )
   throws IOException
      {
      mHostname = hostname;
      mPort = port;
      
      mSock = new Socket( mHostname, mPort );
      try
         {
         checkGreeting();
         }
      catch ( IOException e )
         {
         this.close();
         throw e;
         }
      }
   

   /**
    * Read the greeting from a newly-connected-to DVTP server and make
    * sure the protocol and version are compatible with this class.
    * @throws IOException
    */
   private void checkGreeting()
   throws IOException
      {
      Object greeting_ob;
      try
         {
         greeting_ob = getObject();
         }
      catch ( IOException e )
         {
         throw e;
         }
      catch ( ClassNotFoundException e )
         {
         throw new ProtocolException( e.getLocalizedMessage() );
         }
      if ( greeting_ob instanceof Str )
         greeting_ob = ((Str) greeting_ob).toString();
      String protocol_version = null;
      if ( greeting_ob instanceof String )
         {
         String greeting = (String) greeting_ob;
         protocol_version = (greeting.split( " ", 2 ))[ 0 ];
         }
      if ( protocol_version == null )
         throw new ProtocolException( "Server sent something other"
                           + " than a DVTP string for its greeting" );
      if ( ! Util.stringStartsIgnoreCase( protocol_version, "DVTP/" ) )
         throw new ProtocolException( "Server sent a greeting"
                       + " indicating some other protocol than DVTP: "
                       + protocol_version );
      double version;
      try
         {
         version = Double.valueOf( protocol_version.substring( 5 ) );
         }
      catch ( NumberFormatException e )
         {
         throw new ProtocolException( e.getLocalizedMessage() );
         }
      if ( version < DvtpServerConnection.MIN_SUPPORTED_VERSION )
         throw new ProtocolException( "Server speaks a version of"
                       + "DVTP so old that this client does not"
                       + "maintain backward compatability that far" );
      }
   
   public void close() throws IOException
      {
      if ( mSock != null )
         mSock.close();
      mSock = null;
      }

   public Blob get( URI u )
   throws IOException
      {
      try
         {
         return receiveBlob( query( "get", u ) );
         }
      catch ( ClassNotFoundException e )    // From query()
         {
         throw new ProtocolException( "Server did not return a Blob in"
                                      + " response to a GET query" );
         }
      }
   
   public Blob get( String resource_name )
   throws IOException
      {  
      try
         {
         return receiveBlob( query( "get " + resource_name ) );
         }
      catch ( ClassNotFoundException e )    // From query()
         {
         throw new ProtocolException( "Server did not return a Blob in"
                                      + " response to a GET query" );
         }
      }

   public ProxySpec location( URI u )
   throws IOException, ProtocolException
      { 
      try
         {
         return receiveLocation( query( "location", u ) );
         }
      catch ( ClassNotFoundException e )    // From query()
         {
         throw new ProtocolException( "Server did not return a"
                       + " ProxySpec in response to a LOCATION query" );
         }  
      }
   
   public ProxySpec location( String resource_name ) 
   throws IOException, ProtocolException
      {  
      try
         {
         return receiveLocation( query( "location " + resource_name ) );
         }
      catch ( ClassNotFoundException e )    // From query()
         {
         throw new ProtocolException( "Server did not return a"
                       + " ProxySpec in response to a LOCATION query" );
         }  
      }

   /**
    * Sends a query given a query type name (such as "GET" or
    * "LOCATION"), and a URI, which must match the server this object is
    * connected to.
    * @param type
    * @param u
    * @return
    * @throws IOException
    * @throws ClassNotFoundException
    */
   public Object query( String type, URI u )
   throws IOException, ClassNotFoundException
      {
      if ( u.getHost() != mHostname )
         throw new RuntimeException( "Attempted to get URI with host "
                      + u.getHost() + " from connection to host " 
                      + mHostname );
      if ( u.getPort() == mPort
           ||  (u.getPort() == -1  
                &&  mPort == DvtpServer.DEFAULT_PORT) )
         return query( type + " " + u );
      throw new RuntimeException( "Attempted to get URI with port "
                     + u.getPort() + " from connection to same host"
                     + " at port " + mPort );
      }
   
   /**
    * Sends a query string and returns an object in response, which will 
    * either be a String or a DvtpExternalizable object.
    * @param q
    * @return
    * @throws IOException
    * @throws ClassNotFoundException
    */
   public Object query( String q ) 
   throws IOException, ClassNotFoundException
      {
      safeSend( q );
      return getObject();
      }

   /**
    * Sends a string to this object's socket, wrapping it in a Str if
    * necessary.  Compatible with DvtpFlexi*.
    * @param q - String to send
    * @throws IOException
    */
   private void safeSend( String q ) throws IOException
      {
      if ( DvtpFlexiStreamer.safelyStreamableString( q ) )
         mSock.getOutputStream()
              .write( (q + "\r\n").getBytes( "UTF-8" ) );
      else
         {
         DvtpObject.writeObject( mSock.getOutputStream(),
                                 new Str( q ) );
         }
      }

   private ProxySpec
   receiveLocation( Object response ) throws ProtocolException
      {
      try
         {
         // TODO possibly throw an exception if the regexp doesn't
         // match 'url', but only if it solves a real problem.
         return (ProxySpec) response;
         }
      catch ( ClassCastException e )
         {
         throw new ProtocolException( "Server did not return a"
                       + " ProxySpec in response to a LOCATION query" );
         }
      }

   private Blob receiveBlob( Object response ) throws ProtocolException
      {
      try
         {
         return (Blob) response;
         }
      catch ( ClassCastException e )
         {
         throw new ProtocolException( "Server did not return a blob in"
                                      + " response to a GET query" );
         }
      }

   public Object getObject()
   throws IOException, ClassNotFoundException
      {
      InputStream sis = mSock.getInputStream();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      int first_byte = sis.read();
      if ( first_byte == 0 )
         {
         /* Throw away the length; we're a synchronous thread, so we
          * don't mind blocking for input from a slow server.
          */ 
         CompactUlong.externalAsLong( sis );
         return DvtpObject.parseObject( sis );
         }

      baos.write( first_byte );
      return getString( baos, sis );
      }

   /**
    * Reads a CRLF-terminated string from the given sis, using the baos
    * (and whatever it already contains) as a buffer.
    * @param baos - A ByteArrayOutputStream to use as a buffer
    * @param is - An InputStream from which to read a string
    * @return
    * @throws IOException
    */
   private String getString( ByteArrayOutputStream baos,
                             InputStream is )
   throws IOException
      {
      while ( ! endsWithNewline( baos ) )
         baos.write( is.read() );
      
      byte[] ba = baos.toByteArray();
      return new String( ba, 0, ba.length - 2, "UTF-8" );
      }

   private boolean endsWithNewline( ByteArrayOutputStream baos )
      {
      if ( baos.size() < 2 )
         return false;
      byte[] ba = baos.toByteArray();
      return  ba[ ba.length - 2 ] == '\r'
              && ba[ ba.length - 1 ] == '\n';
      }

   private String mHostname;
   private int    mPort;
   private Socket mSock;
   }

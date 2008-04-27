package org.distroverse.viewer;

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URI;

import org.distroverse.core.net.DvtpFlexiStreamer;
import org.distroverse.distroplane.lib.DvtpServer;
import org.distroverse.dvtp.DvtpObject;
import org.distroverse.dvtp.Str;

public class DvtpServerConnection
   {
   /**
    * Construct a new connection to a DVTP server, getting the hostname
    * from a URL.
    * @param url
    * @throws IOException 
    */
   public DvtpServerConnection( URI u ) throws IOException
      {
      mHostname = u.getHost();
      mPort = u.getPort();
      if ( mPort < 0 )
         mPort = DvtpServer.DEFAULT_PORT;
      
      mSock = new Socket( mHostname, mPort );
      }
   
   public void close() throws IOException
      {
      if ( mSock != null )
         mSock.close();
      mSock = null;
      }

   public Object get( URI u ) throws IOException
      {  return query( "get", u );  }
   
   public Object get( String resource_name ) throws IOException
      {  return query( "get " + resource_name );  }

   public Object location( URI u ) throws IOException
      {  return query( "location", u );  }
   
   public Object location( String resource_name ) throws IOException
      {  return query( "location " + resource_name );  }

   public Object query( String type, URI u ) throws IOException
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
   
   public Object query( String q ) throws IOException
      {
      safeSend( q );
      // TODO this
      return null;
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
         mSock.getOutputStream().write( q.getBytes( "UTF-8" ) );
      else
         {
         ObjectOutput oo 
            = new ObjectOutputStream( mSock.getOutputStream() );
         DvtpObject.writeObject( oo, new Str( q ) );
         }
      }

   private String mHostname;
   private int    mPort;
   private Socket mSock;
   }

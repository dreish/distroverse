package org.distroverse.viewer;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;

import org.distroverse.distroplane.lib.DvtpServer;

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

   public Object get( URI u )
      {  return query( "get", u );  }
   
   public Object get( String resource_name )
      {  return query( "get " + resource_name );  }

   public Object location( URI u )
      {  return query( "location", u );  }
   
   public Object location( String resource_name )
      {  return query( "location " + resource_name );  }

   public Object query( String type, URI u )
      {
      if ( u.getHost() != mHostname )
         throw new RuntimeException( "Attempted to get URI with host "
                      + u.getHost() + " from connection to host " 
                      + mHostname );
      if ( u.getPort() == mPort
           ||  u.getPort() == -1  &&  mPort == DvtpServer.DEFAULT_PORT )
         return query( type + " " + u );
      throw new RuntimeException( "Attempted to get URI with port "
                     + u.getPort() + " from connection to same host"
                     + " at port " + mPort );
      }
   
   public Object query( String q )
      {
      // TODO this
      return null;
      }

   private String mHostname;
   private int    mPort;
   private Socket mSock;
   }

package org.distroverse.helloserver;

import org.distroverse.distroplane.lib.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

/**
 * An ultra-simple server to demonstrate the basic concepts of DVTP.
 * 
 * @author dreish
 */
public final class HelloServer extends DvtpServer
   {
   public HelloServer( DvtpListener l )
      {
      super( l );
      }
   
   /* (non-Javadoc)
    * @see org.distroverse.distroplane.lib.DvtpServer#handleGet(java.lang.String)
    */
   @Override
   public void handleGet( String url, SocketChannel client,
                          ByteBuffer buffer )
      {
      // TODO Auto-generated method stub
      
      }

   /* (non-Javadoc)
    * @see org.distroverse.distroplane.lib.DvtpServer#handleLocation(java.lang.String)
    */
   @Override
   public void handleLocation( String location, SocketChannel client,
                               ByteBuffer buffer )
   throws IOException
      {
      String response = "I always respond thusly";
      buffer.clear();
      buffer.put( response.getBytes( "UTF-8" ) );
      client.write( buffer );
      }

   /* (non-Javadoc)
    * @see org.distroverse.distroplane.lib.DvtpServer#handleProxyOpen(java.lang.String)
    */
   @Override
   public void handleProxyOpen( String token, SocketChannel client,
                                ByteBuffer buffer )
      {
      // TODO Auto-generated method stub
      
      }

   /**
    * @param args
    */
   public static void main( String[] args )
      {
      // TODO Auto-generated method stub
      DvtpListener l = new DvtpMultiplexedListener();
      new HelloServer( l );
      l.serve();
      }
   }

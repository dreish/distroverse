package org.distroverse.helloserver;

import org.distroverse.core.net.*;
import org.distroverse.distroplane.lib.*;
import java.io.*;

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
   public void handleGet( String url, NetOutQueue< Object > noq )
   throws IOException
      {
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
      noq.add( "So, you want to go to " + location + "?" );
      }

   /* (non-Javadoc)
    * @see org.distroverse.distroplane.lib.DvtpServer#handleProxyOpen(java.lang.String)
    */
   @Override
   public void handleProxyOpen( String token,
                                NetOutQueue< Object > noq )
   throws IOException
      {
      noq.add( "Tell me more about " + token + "." );
      }

   /**
    * @param args
    */
   public static void main( String[] args )
      {
      DvtpListener l 
      = new DvtpMultiplexedListener< DvtpFlexiParser, 
                                     DvtpFlexiStreamer >
               ( DvtpFlexiParser.class, DvtpFlexiStreamer.class );
      HelloServer hs = new HelloServer( l );
      NetInQueueWatcher< Object > watcher_thread =
         new DvtpInQueueObjectWatcher( hs );
      watcher_thread.start();
      l.setWatcher( watcher_thread );
      l.serve();  // Does not return.
      }
   }

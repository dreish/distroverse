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
      noq.add( "What do you expect to find at " + url + "?\r\n" );
      }

   /* (non-Javadoc)
    * @see org.distroverse.distroplane.lib.DvtpServer#handleLocation(java.lang.String)
    */
   @Override
   public void handleLocation( String location, 
                               NetOutQueue< Object > noq )
   throws IOException
      {
      noq.add( "So, you want to go to " + location + "?\r\n" );
      }

   /* (non-Javadoc)
    * @see org.distroverse.distroplane.lib.DvtpServer#handleProxyOpen(java.lang.String)
    */
   @Override
   public void handleProxyOpen( String token,
                                NetOutQueue< Object > noq )
   throws IOException
      {
      noq.add( "Tell me more about " + token + ".\r\n" );
      }

   /**
    * @param args
    */
   public static void main( String[] args )
      {
      // TODO Auto-generated method stub
      DvtpListener l 
      = new DvtpMultiplexedListener< DvtpFlexiParser, 
                                     DvtpFlexiStreamer >
               ( DvtpFlexiParser.class, DvtpFlexiStreamer.class );
      HelloServer hs = new HelloServer( l );
      l.serve();
      NetInQueueWatcher< Object > watcher_thread =
         new DvtpInQueueObjectWatcher( hs );
      watcher_thread.run();
      }
   }

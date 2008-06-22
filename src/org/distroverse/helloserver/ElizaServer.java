/*
 * Copyright (c) 2007-2008 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * General Public License (GPL) version 3 or later</a>
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
    * @see org.distroverse.distroplane.lib.DvtpServer#handleKnock(java.lang.String, org.distroverse.core.net.NetOutQueue)
    */
   @Override
   public void handleKnock( String location, NetOutQueue< Object > noq )
   throws IOException
      {
      Log.p( "got: knock " + location, Log.SERVER, -50 );
      noq.add( "Who's there?" );
      }

   /* (non-Javadoc)
    * @see org.distroverse.distroplane.lib.DvtpServer#handleProxyOpen(java.lang.String)
    */
   @Override
   public void handleProxyOpen( String token,
                                NetOutQueue< Object > noq )
   throws IOException
      {
      Log.p( "got: proxyopen " + token, Log.SERVER, -50 );
      noq.add( "Tell me more about " + token + "." );
//      // This is how we would normally handle the PROXYOPEN command:
//      noq.getSession().setAttachment( ProxySession.class,
//                                      new ProxySession() );
//      noq.getSession().setProxyMode();
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
   public void handleProxyObject( Object net_in_object,
                                  NetSession< Object > session )
   throws IOException
      {
      throw new IOException( "How did I get here?" );
//      // Normally we would do this:
//      ProxySession ps
//         = session.getAttachmentOrNull( ProxySession.class );
//      // And using some combination of the session data in ps and the
//      // global data in this class, react to the object.
      }
   }

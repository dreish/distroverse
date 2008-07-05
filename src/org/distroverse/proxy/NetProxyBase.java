/*
 * Copyright (c) 2007-2008 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * General Public License (GPL) version 3 or later</a>
 */
package org.distroverse.proxy;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;

import org.distroverse.core.net.DvtpFlexiParser;
import org.distroverse.core.net.DvtpFlexiStreamer;
import org.distroverse.core.net.DvtpMultiplexedClient;
import org.distroverse.core.net.DvtpProxyInQueueObjectWatcher;
import org.distroverse.core.net.NetInQueueWatcher;
import org.distroverse.core.net.NetSession;
import org.distroverse.dvtp.ClientSendable;
import org.distroverse.dvtp.DvtpExternalizable;

/**
 * Provides a useful base upon which to build proxy classes that
 * communicate with a server using pure-object DVTP (i.e., no simple
 * CRLF-terminated strings).
 * @author dreish
 */
public abstract class NetProxyBase extends ProxyBase
   {
   public NetProxyBase()
      {
      mMultiplexer
         = new DvtpMultiplexedClient< Object, DvtpFlexiParser,
                                      DvtpFlexiStreamer >();
      mWatcher = new DvtpProxyInQueueObjectWatcher( this );
      mMultiplexer.setWatcher( mWatcher );
      mWatcher.start();
      }
   
   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpProxy#run()
    */
   public void run()
      {
      // XXX Auto-generated method stub
      // There might be nothing to do here.
      }

   /**
    * Create a new connection to a server and initialize the connection
    * with sendGreeting().
    * @param remote_address
    * @return
    * @throws IOException
    */
   protected NetSession< Object >
   connect( SocketAddress remote_address )
   throws IOException
      {
      NetSession< Object > ret = mMultiplexer.connect( remote_address );
      sendGreeting( ret );
      return ret;
      }

   /**
    * Defines how 
    * @param new_server
    * @throws ClosedChannelException
    */
   protected void sendGreeting( NetSession< Object > new_server )
   throws ClosedChannelException
      {
      // FIXME: rather than doing it this way, respond to the server's
      // greeting.
      new_server.getNetOutQueue().add( "PROXYOPEN" );
      }

   /**
    * This method is called by the NetInQueueWatcher every time an
    * object is sent from a server.
    * @param o
    */
   public abstract void receiveFromServer( NetSession< Object > s,
                                           DvtpExternalizable o );
   
   /**
    * This method is called by offer() for any object that is not a
    * SetUrl.
    * @param o
    * @throws ClosedChannelException
    */
   abstract protected void receiveFromClient( ClientSendable o )
   throws ClosedChannelException;

   private NetInQueueWatcher< Object > mWatcher;
   private DvtpMultiplexedClient< Object, DvtpFlexiParser,
                                  DvtpFlexiStreamer > mMultiplexer;
   }

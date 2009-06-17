/*
 * <copyleft>
 *
 * Copyright 2007-2009 Dan Reish
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or
 * combining it with Clojure (or a modified version of that program)
 * or clojure-contrib (or a modified version of that library),
 * containing parts covered by the terms of the Eclipse Public
 * License, the licensors of this Program grant you additional
 * permission to convey the resulting work. {Corresponding Source for
 * a non-source form of such a combination shall include the source
 * code for the parts of Clojure and clojure-contrib used as well as
 * that of the covered work.}
 *
 * </copyleft>
 */
package org.distroverse.envoy;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;

import org.distroverse.core.net.DvtpFlexiParser;
import org.distroverse.core.net.DvtpFlexiStreamer;
import org.distroverse.core.net.DvtpMultiplexedClient;
import org.distroverse.core.net.DvtpEnvoyInQueueObjectWatcher;
import org.distroverse.core.net.NetInQueueWatcher;
import org.distroverse.core.net.NetSession;
import org.distroverse.dvtp.ClientSendable;
import org.distroverse.dvtp.DvtpExternalizable;

/**
 * Provides a useful base upon which to build envoy classes that
 * communicate with a server using pure-object DVTP (i.e., no simple
 * CRLF-terminated strings).
 * @author dreish
 */
public abstract class NetEnvoyBase extends EnvoyBase
   {
   public NetEnvoyBase() throws IOException
      {
      mMultiplexer
         = new DvtpMultiplexedClient< Object, DvtpFlexiParser,
                                      DvtpFlexiStreamer >
                  ( DvtpFlexiParser.class, DvtpFlexiStreamer.class );
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpEnvoy#run()
    */
   public void run()
      {
      mWatcher = new DvtpEnvoyInQueueObjectWatcher( this );
      mMultiplexer.setWatcher( mWatcher );
      mWatcher.start();
      mMultiplexer.start();
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
      new_server.getNetOutQueue().add( "ENVOYOPEN" );
      }

   /**
    * This method is called by the NetInQueueWatcher every time an
    * object is sent from a server.
    * @param o
    * @throws ClosedChannelException
    */
   public abstract void receiveFromServer( NetSession< Object > s,
                                           DvtpExternalizable o )
   throws ClosedChannelException;

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

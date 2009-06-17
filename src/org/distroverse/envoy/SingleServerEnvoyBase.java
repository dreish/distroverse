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
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.ClosedChannelException;

import org.distroverse.core.Log;
import org.distroverse.core.net.NetSession;
import org.distroverse.distroplane.lib.DvtpServer;
import org.distroverse.dvtp.ClientSendable;
import org.distroverse.dvtp.DisplayUrl;
import org.distroverse.dvtp.DvtpExternalizable;
import org.distroverse.dvtp.SetUrl;
import org.distroverse.dvtp.Str;

/**
 * Converts setUrl objects from client into the act of connecting to a
 * server (disconnecting from the previous server if connected) and
 * sending it the ENVOYOPEN command (with no argument), and passes other
 * messages to the abstract method receiveFromClient().
 * @author dreish
 */
public abstract class SingleServerEnvoyBase extends NetEnvoyBase
   {
   public SingleServerEnvoyBase() throws IOException
      {
      super();
      }

   public void sendToServer( DvtpExternalizable o )
   throws ClosedChannelException
      {
      mRemoteSession.getNetOutQueue().add( o );
      }

   /**
    * Continue using the same envoy, but connect to a different server.
    * (Also used for the first-time connection.)
    * @param remote_url
    * @throws URISyntaxException
    * @throws IOException
    */
   protected void setServer( String host, int port ) throws IOException
      {
      if ( mRemoteSession != null )
         mRemoteSession.close();

      Log.p( "connecting to server: " + host + ":" + port,
             Log.ENVOY, -50 );
      SocketAddress remote_addr = new InetSocketAddress( host, port );
      mRemoteSession = connect( remote_addr );
      Log.p( "sending ENVOYOPEN command to " + host + ":" + port,
             Log.ENVOY, -50 );
      mRemoteSession.getNetOutQueue()
              .add( new Str( "ENVOYOPEN" ) );
      }

   /**
    * Called at the end of setUrl().
    * @throws IOException
    */
   protected abstract void initWorld() throws IOException;

   protected void setUrl( String new_url )
   throws URISyntaxException, IOException
      {
      URI new_uri = new URI( new_url );
      String host = new_uri.getHost();
      int port = new_uri.getPort();
      if ( port == -1 )
         port = DvtpServer.DEFAULT_PORT;

      if ( port != mCurrentPort
           ||  ! host.contentEquals( mCurrentHost ) )
         setServer( host, port );

      putQueue( new DisplayUrl( new_url ) );
      initWorld();
      }

   public void offer( ClientSendable o ) throws IOException
      {
      if ( o instanceof SetUrl )
         {
         SetUrl su = (SetUrl) o;
         try
            {
            setUrl( su.getUrl() );
            }
         catch ( URISyntaxException e )
            {
            // TODO Auto-generated catch block
            e.printStackTrace();
            }
         }
      else
         {
         receiveFromClient( o );
         }
      }

   private NetSession< Object > mRemoteSession;
   private String mCurrentHost;
   private int    mCurrentPort;
   }

/*
 * Copyright (c) 2007-2008 Dan Reish.
 *
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * General Public License (GPL) version 3 or later</a>
 */
package org.distroverse.proxy;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;

import org.distroverse.core.Log;
import org.distroverse.core.Util;
import org.distroverse.core.net.NetSession;
import org.distroverse.dvtp.ClientSendable;
import org.distroverse.dvtp.DvtpExternalizable;
import org.distroverse.dvtp.FunCall;
import org.distroverse.dvtp.ProxySendable;
import org.distroverse.dvtp.Str;

public class PassThroughProxy extends SingleServerProxyBase
   {
   public PassThroughProxy() throws IOException
      {
      super();
      Log.p( "instantiated PassThroughProxy", Log.PROXY, -50 );
      }

   @Override
   public void receiveFromServer( NetSession< Object > s,
                                  DvtpExternalizable o )
      {
      Log.p( "from server: " + Util.prettyPrintList( o ),
             Log.PROXY, -100 );
      if ( o instanceof ProxySendable )
         putQueue( (ProxySendable) o );
      else
         Log.p( "(which was not ProxySendable, so ignored)",
                Log.PROXY, -20 );
      }

   @Override
   protected void receiveFromClient( ClientSendable o )
   throws ClosedChannelException
      {
      Log.p( "from client: " + Util.prettyPrintList( o ),
             Log.PROXY, -100 );
      Util.prettyPrintList( o );
      sendToServer( o );
      }

   @Override
   protected void initWorld() throws ClosedChannelException
      {
      sendToServer( new FunCall( new Str( "init" ) ) );
      }
   }

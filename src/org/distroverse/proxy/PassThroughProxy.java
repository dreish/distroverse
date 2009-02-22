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

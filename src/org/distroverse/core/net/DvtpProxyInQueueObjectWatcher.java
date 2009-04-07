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
package org.distroverse.core.net;

import java.nio.channels.ClosedChannelException;

import org.distroverse.dvtp.DvtpExternalizable;
import org.distroverse.dvtp.Str;
import org.distroverse.proxy.NetProxyBase;

/**
 * Defines an object-handling method for NetInQueueWatcher appropriate
 * for a NetProxyBase.
 * @author dreish
 */
public class DvtpProxyInQueueObjectWatcher
extends NetInQueueWatcher< Object >
   {
   /**
    * Create a thread object that, when started, will watch any queues
    * that are added to it and dispatch each object o in those queues
    * with p.receiveFromServer( o ).
    * @param p
    */
   public DvtpProxyInQueueObjectWatcher( NetProxyBase p )
      {
      super();
      mProxy = p;
      }

   /* (non-Javadoc)
    * @see org.distroverse.core.net.NetInQueueWatcher#handleNetInObject(java.lang.Object, org.distroverse.core.net.NetInQueue)
    */
   @Override
   protected void
   handleNetInObject( Object net_in_object,
                      NetInQueue< Object > queue )
   throws ClosedChannelException
      {
      if ( net_in_object instanceof String )
         mProxy.receiveFromServer( queue.getSession(),
                                   new Str( (String) net_in_object ) );
      else if ( net_in_object instanceof DvtpExternalizable )
         mProxy.receiveFromServer( queue.getSession(),
                                   (DvtpExternalizable) net_in_object );
      else
         throw new IllegalArgumentException( "net_in_object not a valid"
                                             + "DVTP type" );
      }

   private NetProxyBase mProxy;
   }

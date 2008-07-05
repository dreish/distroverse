/*
 * Copyright (c) 2007-2008 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * General Public License (GPL) version 3 or later</a>
 */
package org.distroverse.core.net;

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

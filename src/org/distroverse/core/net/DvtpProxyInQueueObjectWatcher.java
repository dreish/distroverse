/*
 * Copyright (c) 2007-2008 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * General Public License (GPL) version 3 or later</a>
 */
package org.distroverse.core.net;

import org.distroverse.dvtp.DvtpExternalizable;
import org.distroverse.proxy.NetProxyBase;

/**
 * Defines an object-handling method for NetInQueueWatcher appropriate
 * for a NetProxyBase.
 * @author dreish
 */
public class DvtpProxyInQueueObjectWatcher
extends NetInQueueWatcher< DvtpExternalizable >
   {
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
   handleNetInObject( DvtpExternalizable net_in_object,
                      NetInQueue< DvtpExternalizable > queue )
      {
      mProxy.receiveFromServer( net_in_object );
      }

   private NetProxyBase mProxy;
   }

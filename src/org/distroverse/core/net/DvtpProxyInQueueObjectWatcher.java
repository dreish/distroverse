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

   @Override
   protected void 
   handleNetInObject( DvtpExternalizable net_in_object,
                      NetInQueue< DvtpExternalizable > queue )
      {
      mProxy.receiveFromServer( net_in_object );
      }

   private NetProxyBase mProxy;
   }

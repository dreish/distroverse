package org.distroverse.core.net;

import org.distroverse.proxy.ProxyBase;

/**
 * Defines an object-handling method for NetInQueueWatcher appropriate
 * for a ProxyBase.
 * @author dreish
 */
public class DvtpProxyInQueueObjectWatcher
extends NetInQueueWatcher< Object >
   {
   public DvtpProxyInQueueObjectWatcher( ProxyBase p )
      {
      super();
      mProxy = p;
      }

   @Override
   protected void handleNetInObject( Object net_in_object,
                                     NetInQueue< Object > queue )
      {
      mProxy.receiveFromServer( net_in_object );
      }

   private ProxyBase mProxy;
   }

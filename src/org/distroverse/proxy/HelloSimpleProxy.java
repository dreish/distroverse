package org.distroverse.proxy;

import java.util.concurrent.BlockingQueue;

import org.distroverse.dvtp.ClientSendable;
import org.distroverse.dvtp.DvtpProxy;
import org.distroverse.dvtp.ProxySendable;

/**
 * A simple demo proxy that does not connect to any server.
 * @author dreish
 */
public class HelloSimpleProxy implements DvtpProxy
   {
   public void offer( ClientSendable o )
      {
      // Client input is ignored.
      }

   public void run()
      {
      
      }

   public void setQueue( BlockingQueue< ProxySendable > queue )
      {
      mClientQueue = queue;
      }

   private BlockingQueue< ProxySendable > mClientQueue;
   }

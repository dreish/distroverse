package org.distroverse.core.net;

import org.distroverse.distroplane.lib.*;

public class DvtpInQueueObjectWatcher 
extends NetInQueueWatcher< Object >
   {
   public DvtpInQueueObjectWatcher( DvtpServer s )
      {
      super();
      mServer = s;
      }

   @Override
   protected void handleNetInObject( Object net_in_object,
                                     NetInQueue< Object > queue )
      {
      // TODO Auto-generated method stub
      if ( net_in_object instanceof String )
         {
         mServer.handleCommand( (String) net_in_object,
                                client, buffer );
         }
      }

   private DvtpServer mServer;
   }

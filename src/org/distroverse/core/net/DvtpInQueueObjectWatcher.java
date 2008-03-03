package org.distroverse.core.net;

import java.io.IOException;

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
   throws IOException
      {
      // TODO Auto-generated method stub
      if ( net_in_object instanceof String )
         {
         NetOutQueue< Object > noq
            = queue.getSession().getNetOutQueue();
         mServer.handleCommand( (String) net_in_object, noq );
         }
      }

   private DvtpServer mServer;
   }

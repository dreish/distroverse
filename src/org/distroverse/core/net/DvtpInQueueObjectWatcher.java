package org.distroverse.core.net;

import java.io.IOException;
import java.net.ProtocolException;

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
      NetSession< Object > session = queue.getSession();
      
      if ( session.inProxyMode() )
         {
         mServer.handleProxyObject( net_in_object, session );
         }
      else if ( net_in_object instanceof String )
         {
         NetOutQueue< Object > noq = session.getNetOutQueue();
         mServer.handleCommand( (String) net_in_object, noq );
         }
      else
         {
         throw new ProtocolException( "Received a non-string object"
                      + " of type "
                      + net_in_object.getClass().getCanonicalName()
                      + " while in conversation mode" );
         }
      }

   private DvtpServer mServer;
   }

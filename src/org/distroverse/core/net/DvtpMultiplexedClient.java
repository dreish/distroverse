/**
 * 
 */
package org.distroverse.core.net;

import java.io.IOException;
import java.nio.channels.SelectionKey;

import org.distroverse.distroplane.lib.DvtpMultiplexedConnection;

/**
 * @author dreish
 *
 */
public class DvtpMultiplexedClient< O extends Object,
                                    P extends ObjectParser< O >,
                                    S extends ObjectStreamer< O > >
extends DvtpMultiplexedConnection< O, P, S >
   {

   /**
    * 
    */
   public DvtpMultiplexedClient()
      {
      // TODO Auto-generated constructor stub
      }

   /* (non-Javadoc)
    * @see org.distroverse.distroplane.lib.DvtpMultiplexedConnection#acceptConnection(java.nio.channels.SelectionKey)
    */
   @Override
   protected void acceptConnection( SelectionKey key )
   throws IOException
      {
      throw new IOException( "Client cannot accept connections" );
      }

   }

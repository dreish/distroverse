/**
 *
 */
package org.distroverse.proxy;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;

import org.distroverse.core.Log;
import org.distroverse.core.net.NetSession;
import org.distroverse.dvtp.ClientSendable;
import org.distroverse.dvtp.DvtpExternalizable;
import org.distroverse.dvtp.FunCall;
import org.distroverse.dvtp.Str;
import org.distroverse.dvtp.ULong;

/**
 *
 * @author dreish
 */
public class WorldProxy extends SingleServerProxyBase
   {

   /**
    * @throws IOException
    */
   public WorldProxy() throws IOException
      {
      super();
      }

   /* (non-Javadoc)
    * @see org.distroverse.proxy.SingleServerProxyBase#initWorld()
    */
   @Override
   protected void initWorld() throws IOException
      {
      // TODO Auto-generated method stub

      }

   /* (non-Javadoc)
    * @see org.distroverse.proxy.NetProxyBase#receiveFromClient(org.distroverse.dvtp.ClientSendable)
    */
   @Override
   protected void receiveFromClient( ClientSendable o )
   throws ClosedChannelException
      {
      switch ( o.getClassNumber() )
         {
         default:
            Log.p( "Unrecognized message from client",
                   Log.UNHANDLED, -10 );
         }
      }

   /* (non-Javadoc)
    * @see org.distroverse.proxy.NetProxyBase#receiveFromServer(org.distroverse.core.net.NetSession, org.distroverse.dvtp.DvtpExternalizable)
    */
   @Override
   public void receiveFromServer( NetSession< Object > s,
                                  DvtpExternalizable o )
      {
      switch ( o.getClassNumber() )
         {
         case 129:
            receiveFunCallFromServer( s, (FunCall) o );
            break;
         default:
            Log.p( "Unrecognized message from server",
                   Log.NET | Log.UNHANDLED | Log.SERVER, 10 );
         }
      }

   private void receiveFunCallFromServer( NetSession< Object > s,
                                          FunCall fc )
      {
      Str   fc_name = (Str)   fc.getContents( 0 );
      ULong fc_id   = (ULong) fc.getContents( 1 );
      // XXX finish this
      }

   }

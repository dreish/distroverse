/*
 * Copyright (c) 2007-2008 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * General Public License (GPL) version 3 or later</a>
 */
package org.distroverse.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;

import org.distroverse.core.net.DvtpExtParser;
import org.distroverse.core.net.DvtpFlexiParser;
import org.distroverse.core.net.DvtpProxyInQueueObjectWatcher;
import org.distroverse.core.net.NetInQueue;
import org.distroverse.core.net.NetInQueueWatcher;
import org.distroverse.core.net.NetOutQueue;
import org.distroverse.dvtp.ClientSendable;
import org.distroverse.dvtp.DvtpExternalizable;
import org.distroverse.dvtp.SetUrl;
import org.distroverse.viewer.DvtpServerConnection;

/**
 * Provides a useful base upon which to build proxy classes that
 * communicate with a server using pure-object DVTP (i.e., no simple
 * CRLF-terminated strings).
 * @author dreish
 */
public abstract class NetProxyBase extends ProxyBase
   {
   private static final int DEFAULT_QUEUE_SIZE = 10;

   public NetProxyBase()
      {
      mFromServerQueue 
         = new NetInQueue< DvtpExternalizable >
               (new DvtpExtParser( ByteBuffer.allocate( 1024 ) ),
                DEFAULT_QUEUE_SIZE, null, null);
      mWatcher = new DvtpProxyInQueueObjectWatcher( this );
      mWatcher.addQueue( mFromServerQueue );
      mWatcher.start();
      
      mConnection = null;
      }
   
   private class ServerWatcher extends Thread
      {
      public ServerWatcher( DvtpServerConnection c,
                            NetProxyBase         q )
         {
         mWatchedConnection = c;
         mNetProxy          = q;
         }
      @Override
      public void run()
         {
         try
            {
            while ( true )
               {
               Object o = mWatchedConnection.getObject();
               mNetProxy.receiveFromServer( (DvtpExternalizable) o );
               }
            }
         catch ( IOException e )
            {
            // FIXME this probably just means the connection was closed
            e.printStackTrace();
            }
         catch ( ClassNotFoundException e )
            {
            // TODO Auto-generated catch block
            e.printStackTrace();
            }
         }
      
      private DvtpServerConnection mWatchedConnection;
      private NetProxyBase mNetProxy;
      }
   
   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpProxy#offer(org.distroverse.dvtp.ClientSendable)
    */
   public void offer( ClientSendable o )
   throws IOException
      {
      if ( o instanceof SetUrl )
         {
         SetUrl su = (SetUrl) o;
         if ( mConnection != null )
            mConnection.close();
         mConnection = new DvtpServerConnection( su.getUrl() );
         new ServerWatcher( mConnection, this ).start();
         }
      else
         {
         receiveFromClient( o );
         }
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpProxy#run()
    */
   public void run()
      {
      // XXX Auto-generated method stub
      // There might be nothing to do here.
      }
   
   public void sendToServer( DvtpExternalizable o )
   throws ClosedChannelException
      {
      mToServerQueue.add( o );
      }
   
   /**
    * This method is called by the NetInQueueWatcher every time an
    * object is sent from the server.
    * @param o
    */
   public abstract void receiveFromServer( DvtpExternalizable o );
   
   /**
    * This method is called by offer() for any object that is not a
    * SetUrl.
    * @param o
    * @throws ClosedChannelException
    */
   abstract protected void receiveFromClient( ClientSendable o )
   throws ClosedChannelException;

   private NetOutQueue< DvtpExternalizable > mToServerQueue;
   private NetInQueue< DvtpExternalizable > mFromServerQueue;
   private NetInQueueWatcher< DvtpExternalizable > mWatcher;
   private DvtpServerConnection mConnection;
   }

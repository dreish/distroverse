package org.distroverse.distroplane.lib;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

import org.distroverse.core.Log;
import org.distroverse.core.net.NetInQueueWatcher;
import org.distroverse.core.net.NetSession;
import org.distroverse.core.net.ObjectParser;
import org.distroverse.core.net.ObjectStreamer;

public abstract class
DvtpMultiplexedConnection< O extends Object,
                           P extends ObjectParser< O >, 
                           S extends ObjectStreamer< O > >
   {
   public static final int DEFAULT_QUEUE_SIZE = 10;

   protected abstract void acceptConnection( SelectionKey key )
   throws IOException;

   protected Selector mSelector;

   public void setWatcher( NetInQueueWatcher< O > watcher_thread )
      {  mWatcher = watcher_thread;  }

   protected void processIo()
      {
      Iterator< SelectionKey > key_iterator 
         = mSelector.selectedKeys().iterator();
      
      while ( key_iterator.hasNext() )
         {
         SelectionKey key = key_iterator.next();
         try
            {
            if ( key.isAcceptable() )
               acceptConnection( key );
            if ( key.isReadable() )
               readConnection( key );
            if ( key.isWritable() )
               writeConnection( key );
            key_iterator.remove();
            }
         catch ( Exception e )
            {
            Log.p( "Canceling an unknown key due to an exception",
                   Log.NET, -10 );
            Log.p( e, Log.NET, -10 );
            key_iterator.remove();
            key.cancel();
            try 
               {  key.channel().close();  }
            catch ( IOException e2 )
               {  
               Log.p( "Unhandled exception: " + e, 
                      Log.NET | Log.UNHANDLED, 1 ); 
               }
            }
         }
      }

   private void readConnection( SelectionKey key ) throws Exception
      {
      Log.p( "readConnection called", Log.NET, -50 );
      @SuppressWarnings( "unchecked" )
      NetSession< Object > session 
         = (NetSession< Object >) key.attachment();
      session.getNetInQueue().read();
      }

   private void writeConnection( SelectionKey key ) throws Exception
      {
      Log.p( "writeConnection called", Log.NET, -50 );
      @SuppressWarnings( "unchecked" )
      NetSession< Object > session 
         = (NetSession< Object >) key.attachment();
      session.getNetOutQueue().write();
      }

   protected Class< P > mParserClass;
   protected Class< S > mStreamerClass;
   protected NetInQueueWatcher< O > mWatcher;

   public DvtpMultiplexedConnection()
      {
      super();
      }

   }
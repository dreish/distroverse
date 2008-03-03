/**
 * An abstract base class handling the annoying details of a DVTP
 * server.
 */
package org.distroverse.distroplane.lib;

/**
 * @author dreish
 *
 */
public abstract class DvtpServer
   {
   public final static int DEFAULT_MAX_LISTENERS = 8;

   /**
    * 
    */
   public DvtpServer()
      {
      mListenPort = 0;
      mReadyListeners = 0;
      mMaxListeners = DEFAULT_MAX_LISTENERS;
      }
   
   public void listen()
      {
      // TODO implement listen()
      
      }
   
   public void session()
      {
      
      }
   
   public abstract void handleLocation( String location );
   public abstract void handleGet( String url );
   public abstract void handleProxyOpen( String token );

   int mListenPort;
   int mReadyListeners;
   int mMaxListeners;
   }

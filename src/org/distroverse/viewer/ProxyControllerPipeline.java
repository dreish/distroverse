package org.distroverse.viewer;

import com.jmex.game.StandardGame;

public class ProxyControllerPipeline extends ControllerPipeline
   {
   /**
    * Note that the 'proxy' passed to this constructor, which may be
    * null, is the one already loaded and running, if any.  If that
    * proxy can handle the given URL, it will be used.
    * @param url - the dvtp:// URL this pipeline should be connected to
    * @param game - the jME StandardGame
    * @param window - the ViewerWindow on which to display
    * @param proxy - the already-running proxy, if any
    */
   public ProxyControllerPipeline( String url, StandardGame game,
                                   ViewerWindow window,
                                   ProxyClientConnection proxy )
      {
      // TODO Auto-generated constructor stub
      }

   @Override
   public void close()
      {
      // TODO Auto-generated method stub

      }

   private ProxyClientConnection mProxy;
   }

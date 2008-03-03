package org.distroverse.viewer;

import com.jmex.game.StandardGame;

/**
 * @author dreish
 *
 */
public class ViewerWindow
   {
   public ViewerWindow()
      {
      mGame  = new StandardGame( "Distroverse Viewer" );
      mProxy = new ProxyClientConnection();
      mGame.start();
      }
   
   void setUrl( String url )
      {
      if ( mPipeline != null )
         mPipeline.close();
      mProxy.setUrl( url );
      mPipeline = ControllerPipeline.getNew( url, mProxy, mGame, this );
      }
   
   // TODO UI elements will be drawn in ViewerWindow itself
   
   StandardGame          mGame;
   ProxyClientConnection mProxy;
   ControllerPipeline    mPipeline;
   }

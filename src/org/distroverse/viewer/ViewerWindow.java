package org.distroverse.viewer;

import com.jme.scene.Node;
import com.jmex.game.StandardGame;
import com.jmex.game.state.DebugGameState;
import com.jmex.game.state.GameStateManager;

/**
 * @author dreish
 */
public class ViewerWindow
   {
   public ViewerWindow()
      {
      mGame  = new StandardGame( "Distroverse Viewer" );
      mProxy = new ProxyClientConnection();
      mGame.start();
      final DebugGameState debug = new DebugGameState();
      GameStateManager.getInstance().attachChild( debug );
      debug.setActive( true );
      Node root_node = debug.getRootNode();
      mGui   = new ViewerGui( root_node );
      }
   
   void setUrl( String url )
      {
      if ( mPipeline != null )
         mPipeline.close();
      mProxy.setUrl( url );
      mGui.getLocationBar().setText( url );
      mPipeline = ControllerPipeline.getNew( url, mProxy, mGame, this );
      }
   
   // TODO UI elements will be drawn in ViewerWindow itself
   
   StandardGame          mGame;
   ProxyClientConnection mProxy;
   ControllerPipeline    mPipeline;
   ViewerGui             mGui;
   }

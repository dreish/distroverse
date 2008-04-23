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
      mWorld = new WorldGraph( root_node );
      }
   
   public void setUrl( String url )
      {
      if ( mPipeline == null  ||  ! mPipeline.handlesUrl( url ) )
         newPipelineUrl( url );
      else
         mPipeline.setUrl( url );
      
      mGui.getLocationBar().setText( url );
      }
   
   private void newPipelineUrl( String url )
      {
      if ( mPipeline != null )
         mPipeline.close();
      mProxy.setUrl( url );
      mPipeline = ControllerPipeline.getNew( url, mProxy, mGame, this );
      }
   
   public WorldGraph getWorld()  {  return mWorld;  }
   
   private StandardGame          mGame;
   private ProxyClientConnection mProxy;
   private ControllerPipeline    mPipeline;
   private ViewerGui             mGui;
   private WorldGraph            mWorld;
   }

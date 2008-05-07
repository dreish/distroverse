package org.distroverse.viewer;

import java.io.IOException;
import java.net.URISyntaxException;

import org.distroverse.core.Log;

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
      mGame.start();
      final DebugGameState debug = new DebugGameState();
      GameStateManager.getInstance().attachChild( debug );
      debug.setActive( true );
      Node root_node = debug.getRootNode();
      mGui   = new ViewerGui( root_node );
      mWorld = new WorldGraph( root_node );
      }
   
   /**
    * Sets the URL viewed by this window.  Can be called in response to
    * a user action, or a SetUrl object from the proxy.
    * @param url - the DVTP URL to go to
    * @throws URISyntaxException - goes without saying
    * @throws IOException - general I/O problems, typically network
    * @throws ClassNotFoundException - if the proxy cannot be loaded
    */
   public void setUrl( String url ) 
      {
      try
         {
         if ( mPipeline == null  ||  ! mPipeline.handlesUrl( url ) )
            newPipelineUrl( url );
         else
            {
            mPipeline.setUrl( url );
            mGui.getLocationBar().setText( url );
            }
         }
      catch ( URISyntaxException e )
         {
         Log.p( "Bad URI syntax: " + url, Log.CLIENT, 0 );
         Log.p( e, Log.CLIENT, 0 );
         }
      catch ( IOException e )
         {
         Log.p( "I/O error from: " + url, Log.NET, 10 );
         Log.p( e, Log.NET, 10 );
         }
      catch ( ClassNotFoundException e )
         {
         Log.p( "Unloadable proxy from: " + url, Log.DVTP, 5 );
         Log.p( e, Log.DVTP, 5 );
         }
      }
   
   private void newPipelineUrl( String url ) 
   throws URISyntaxException, IOException, ClassNotFoundException
      {
      if ( mPipeline != null )
         mPipeline.close();
      if ( mProxy != null )
         mProxy.close();
      mProxy = new ProxyClientConnection( url,
                        ProxyControllerPipeline.getProxyUrl( url ) );
      mPipeline = ControllerPipeline.getNew( url, mProxy, mGame, this );
      }
   
   public WorldGraph getWorld()  {  return mWorld;  }
   
   private StandardGame          mGame;
   private ProxyClientConnection mProxy;
   private ControllerPipeline    mPipeline;
   private ViewerGui             mGui;
   private WorldGraph            mWorld;
   }

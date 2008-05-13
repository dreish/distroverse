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
    * Requests that this viewer go to a given URL by either getting a
    * new proxy, or sending a SetUrl object to the existing proxy.
    * @param url - the DVTP URL to go to
    * @throws URISyntaxException - goes without saying
    * @throws IOException - general I/O problems, typically network
    * @throws ClassNotFoundException - if the proxy cannot be loaded
    */
   public void requestUrl( String url ) 
      {
      try
         {
         if ( mPipeline == null  ||  ! mPipeline.handlesUrl( url ) )
            newPipelineUrl( url );
         else
            mPipeline.requestUrl( url );
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
      catch ( Exception e )
         {
         Log.p( "Unloadable proxy from: " + url, Log.DVTP, 5 );
         Log.p( e, Log.DVTP, 5 );
         }
      }
   
   public void setDisplayedUrl( String url )
      {
      mGui.getLocationBar().setText( url );
      }
   
   private void newPipelineUrl( String url ) 
   throws Exception
      {
      if ( mPipeline != null )
         mPipeline.close();
      if ( mProxy != null )
         mProxy.close();
      mProxy = new ProxyClientConnection( url,
                        ProxyControllerPipeline.getProxyUrl( url ),
                        this );
      mPipeline = ControllerPipeline.getNew( url, mProxy, mGame, this );
      }
   
   public WorldGraph getWorld()  {  return mWorld;  }
   
   private StandardGame          mGame;
   private ProxyClientConnection mProxy;
   private ControllerPipeline    mPipeline;
   private ViewerGui             mGui;
   private WorldGraph            mWorld;
   }

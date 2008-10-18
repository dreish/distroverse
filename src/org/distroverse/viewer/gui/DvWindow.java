/**
 *
 */
package org.distroverse.viewer.gui;

import org.fenggui.Display;
import org.fenggui.render.lwjgl.LWJGLBinding;

import com.jme.app.BaseGame;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.state.LightState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.util.Timer;
import com.jme.util.lwjgl.LWJGLTimer;

/**
 * Provides some useful general methods for adding nodes, getting
 * protected access to the FengGUI display, and update()ing.
 * @author dreish
 */
public class DvWindow extends BaseGame
   {
   /**
    *
    */
   public DvWindow()
      {
      mRootNode = new Node( "mRootNode" );
      publicRoot = mRootNode;
      }

   public Node getRootNode()
      {  return mRootNode;  }

   public int getWidth()
      {  return mDisp.getWidth();  }
   public int getHeight()
      {  return mDisp.getWidth();  }

   public Vector3f getCameraLoc()
      {  return mCam.getLocation();  }
   public Vector3f getCameraLeft()
      {  return mCam.getLeft();  }
   public Vector3f getCameraUp()
      {  return mCam.getUp();  }
   public Vector3f getCameraDir()
      {  return mCam.getDirection();  }


   /**
    * Move the camera to the specified location and orientation
    * @param loc - the location of the camera
    * @param left - the left axis of the camera
    * @param up - the up axis of the camera
    * @param dir - the direction the camera is facing
    */
   public void setCamera( Vector3f loc, Vector3f left,
                          Vector3f up, Vector3f dir )
      {
      mCam.setFrame( loc, left, up, dir );
      mCam.update();
      }

   /* (non-Javadoc)
    * @see com.jme.app.BaseGame#cleanup()
    */
   @Override
   protected void cleanup()
      {
      // XXX This seems non-reentrant.

      // Clean up the mouse
      MouseInput.get().removeListeners();
      MouseInput.destroyIfInitalized();
      // Clean up the keyboard
      KeyInput.destroyIfInitalized();
      }

   /* (non-Javadoc)
    * @see com.jme.app.BaseGame#initGame()
    */
   @Override
   protected void initGame()
      {
      mDisp     = new org.fenggui.Display( new LWJGLBinding() );
      mInput    = new FengJMEInputHandler( mDisp );

      enableZBuffering( mRootNode );

//      // ---- LIGHTS
//      /** Set up a basic, default light. */
//      PointLight light = new PointLight();
//      light.setDiffuse( new ColorRGBA( 0.75f, 0.75f, 0.75f, 0.75f ) );
//      light.setAmbient( new ColorRGBA( 0.5f, 0.5f, 0.5f, 1.0f ) );
//      light.setLocation( new Vector3f( 100, 100, 100 ) );
//      light.setEnabled( true );
//
//      /** Attach the light to a lightState and the lightState to rootNode. */
//      LightState lightState = display.getRenderer().createLightState();
//      lightState.setEnabled( true );
//      lightState.attach( light );
//      mRootNode.setRenderState( lightState );
//      
//      mRootNode.updateRenderState();

      }

   private void enableZBuffering( Node node )
      {
      ZBufferState buf = display.getRenderer().createZBufferState();
      buf.setEnabled( true );
      buf.setFunction( ZBufferState.CF_LEQUAL );
      node.setRenderState( buf );
      mRootNode.updateRenderState();
      }

   /* (non-Javadoc)
    * @see com.jme.app.BaseGame#initSystem()
    */
   @Override
   protected void initSystem()
      {
      // 'properties' is a protected field of AbstractGame, sadly.
      display =
         DisplaySystem.getDisplaySystem( properties.getRenderer() );
      display.createWindow( properties.getWidth(),
                            properties.getHeight(),
                            properties.getDepth(),
                            properties.getFreq(),
                            properties.getFullscreen() );

      createCamera();

      MouseInput.get().setCursorVisible( true );

      // XXX This is only for testing, obviously; should be Cmd-Q
      KeyBindingManager.getKeyBindingManager()
                       .set( "quit", KeyInput.KEY_ESCAPE );

      mTimer = new LWJGLTimer();
      }

   private void createCamera()
      {
      mCam = display.getRenderer()
                    .createCamera( display.getWidth(),
                                   display.getHeight() );
      mCam.setFrustumPerspective( 45.0f, (float) display.getWidth()
                                         / (float) display.getHeight(),
                                  1, 1000 );

      Vector3f loc  = new Vector3f(  0.0f, 0.0f, 10.0f );
      Vector3f left = new Vector3f( -1.0f, 0.0f,  0.0f );
      Vector3f up   = new Vector3f(  0.0f, 1.0f,  0.0f );
      Vector3f dir  = new Vector3f(  0.0f, 0.0f, -1.0f );
      mCam.setFrame( loc, left, up, dir );
      mCam.update();
      display.getRenderer().setCamera( mCam );
      }

   /* (non-Javadoc)
    * @see com.jme.app.BaseGame#reinit()
    */
   @Override
   protected void reinit()
      {
      // TODO Auto-generated method stub

      }

   /* (non-Javadoc)
    * @see com.jme.app.BaseGame#render(float)
    */
   @Override
   protected void render( float interpolation )
      {
      Renderer r = display.getRenderer();
      r.clearBuffers();
      r.draw( mRootNode );
      mDisp.display();
      }

   /* (non-Javadoc)
    * @see com.jme.app.BaseGame#update(float)
    */
   @Override
   protected void update( float interpolation )
      {
      mTimer.update();
      float tpf = mTimer.getTimePerFrame();
      mInput.update( tpf );
      if ( ! mInput.wasKeyHandled() )
         {
         if ( KeyBindingManager.getKeyBindingManager()
                               .isValidCommand( "quit" ) )
            finish();
         }
      mRootNode.updateGeometricState( tpf, true );
      }

   private Timer               mTimer;
   private FengJMEInputHandler mInput;
   private Node                mRootNode;
   private Display             mDisp;
   private Camera              mCam;

   public static Node publicRoot;
   }

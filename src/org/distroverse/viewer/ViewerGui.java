package org.distroverse.viewer;

import org.distroverse.viewer.gui.TextDisplayBar;

import com.jme.renderer.Renderer;
import com.jme.scene.*;

public class ViewerGui
   {
   public ViewerGui( Node root )
      {
      mGuiRoot = new Node( "GUI-root" );
      mGuiRoot.setRenderQueueMode( Renderer.QUEUE_ORTHO );
      root.attachChild( mGuiRoot );
      mGuiRoot.setCullMode( SceneElement.CULL_NEVER );
      // FIXME for some reason, the GUI still disappears
      mLocationBar = new TextDisplayBar( mGuiRoot, 0, 420,
                                         "initial-text" );
      }

   public TextDisplayBar getLocationBar()
      {  return mLocationBar;   }

   Node            mGuiRoot;
   TextDisplayBar  mLocationBar;
   }

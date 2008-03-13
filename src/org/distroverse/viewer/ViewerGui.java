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
      mLocationBar = new TextDisplayBar( mGuiRoot, 0, 50,
                                         "initial-text" );
      }

   public TextDisplayBar getLocationBar()
      {  return mLocationBar;   }

   Node            mGuiRoot;
   TextDisplayBar  mLocationBar;
   }

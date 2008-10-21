/*
 * Copyright (c) 2007-2008 Dan Reish.
 *
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * General Public License (GPL) version 3 or later</a>
 */
package org.distroverse.viewer;

import org.distroverse.viewer.gui.DvWindow;
import org.distroverse.viewer.gui.TextDisplayBar;

import com.jme.scene.Node;

public class ViewerGui
   {
   public ViewerGui( DvWindow w )
      {
      mWindowRoot = new Node( "DvWindow-root" );
      w.getRootNode().attachChild( mWindowRoot );

//      mGuiRoot = new Node( "GUI-root" );
//      mGuiRoot.setRenderQueueMode( Renderer.QUEUE_ORTHO );
//      mGuiRoot.setCullMode( SceneElement.CULL_NEVER );
//      mWindowRoot.attachChild( mGuiRoot );

      mWorldRoot = new Node( "World-root" );
      mWindowRoot.attachChild( mWorldRoot );

      mLocationBar = new TextDisplayBar( w, 0, 0,
                                         "initial-text" );
      }

   public Node getWorldRootNode()
      {
      return mWorldRoot;
      }

   public TextDisplayBar getLocationBar()
      {  return mLocationBar;   }

//   private Node           mGuiRoot;
   private Node           mWindowRoot;
   private Node           mWorldRoot;
   private TextDisplayBar mLocationBar;
   }

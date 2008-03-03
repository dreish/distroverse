package org.distroverse.viewer;

import org.distroverse.viewer.gui.TextDisplayBar;

import com.jme.scene.*;
import com.jmex.game.state.DebugGameState;

public class ViewerGui
   {
   public ViewerGui()
      {
      final DebugGameState debug = new DebugGameState();
      Node root_node = debug.getRootNode();
//      mGuiRoot = new Node();
//      root_node.attachChild( mGuiRoot );
      mGuiRoot = root_node;
      mLocationBar = new TextDisplayBar( mGuiRoot, "initial-text" );
      }

   public TextDisplayBar getLocationBar()
      {  return mLocationBar;   }

   Node            mGuiRoot;
   TextDisplayBar  mLocationBar;
   }

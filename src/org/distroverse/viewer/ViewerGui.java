package org.distroverse.viewer;

import org.distroverse.viewer.gui.TextDisplayBar;

import com.jme.scene.*;

public class ViewerGui
   {
   public ViewerGui( Node root )
      {
      mGuiRoot = new Node();
      root.attachChild( mGuiRoot );
      mLocationBar = new TextDisplayBar( mGuiRoot, "initial-text" );
      }

   public TextDisplayBar getLocationBar()
      {  return mLocationBar;   }

   Node            mGuiRoot;
   TextDisplayBar  mLocationBar;
   }

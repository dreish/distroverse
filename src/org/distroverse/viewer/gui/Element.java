package org.distroverse.viewer.gui;

import com.jme.math.Vector3f;
import com.jme.scene.Node;

public abstract class Element
   {
   public Element( Node parent )
      {
      mParent = parent;
      }
   
   protected Node     mParent;
   protected Vector3f mPos;
   }

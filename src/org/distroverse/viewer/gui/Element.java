/*
 * Copyright (c) 2007-2008 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * General Public License (GPL) version 3 or later</a>
 */
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

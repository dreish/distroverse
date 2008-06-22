/*
 * Copyright (c) 2007-2008 Dan Reish.
 * 
 * For license details, see the file COPYING-L in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/lgpl.html">GNU
 * Lesser General Public License (LGPL) version 3 or later</a>
 */
package org.distroverse.dvop;

import javax.vecmath.*;
import org.distroverse.dvtp.*;

/**
 * This class is part of the DVOP protocol.
 * 
 * @author dreish
 */
public abstract class PhysicalObject
   {
   public void setPosition( Point3d position )
      {
      mPosition = position;
      }

   /**
    * 
    */
   public PhysicalObject()
      {
      // TODO Auto-generated constructor stub
      }

   private Point3d  mPosition;
   private Quat4d   mOrientation;
   private Vector3d mVelocity;
   private Quat4d   mRotation;
   private Shape    mShape;
   }

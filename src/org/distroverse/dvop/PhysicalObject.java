/**
 * Copyright (c) 2007 Dan Reish
 * 
 * 
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

/**
 * 
 */
package org.distroverse.core;

import javax.vecmath.*;

/**
 * @author dreish
 *
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

package org.distroverse.distroplane.lib;

import java.util.List;

import com.jme.math.Vector3f;

/**
 * I couldn't find a valid term for the class of shapes consisting of
 * parallel polygons connected by trapezoidal sides, the degenerate case
 * of which is a pyramid (with triangular sides), so I'm calling them
 * "frustoids".
 * @author dreish
 */
public abstract class FrustoidFactory
extends ShapeFactory
implements DimFactory
   {
   protected FrustoidFactory()
      {
      super();
      }

   protected List< Vector3f > genBase( Vector3f center )
      {
      return new DynPolygon( mSides,
                             center,
                             new Vector3f( 0, mRadius, 0 ),
                             new Vector3f( mRadius * mHAspect,
                                           0, 0 ) );
      }

   public FrustoidFactory 
   setDims( double radius, double v_aspect, double h_aspect )
      {
      mRadius = (float) radius;
      mHeight = (float) (2 * radius * v_aspect);
      mHAspect = (float) h_aspect;
      return this;
      }

   public FrustoidFactory setSides( int s )
      {
      mSides = s;
      return this;
      }
   
   protected float getHeight()
      {  return mHeight;  }

   private int mSides;
   private float mRadius;
   private float mHeight;
   private float mHAspect;
   }
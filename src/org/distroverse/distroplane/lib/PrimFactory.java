package org.distroverse.distroplane.lib;

import org.distroverse.dvtp.Shape;

public class PrimFactory extends ShapeFactory implements DimFactory
   {
   public enum PrimShape 
      {
      SPHERE, PYRAMID, CUBOID;

      public DimFactory newFactory()
         {
         switch ( this )
            {
            case SPHERE:
               return new BallFactory();
            case PYRAMID:
               return new PyramidFactory();
            case CUBOID:
               return new CuboidFactory();
            }
         }
      }

   public PrimFactory()
      {
      mRadius = mVAspect = mHAspect = 1.0;
      mShape = null;
      mFactory = null;
      }

   public PrimFactory setPrimShape( PrimShape s )
      {
      if ( s != mShape )
         {
         mShape = s;
         mFactory = s.newFactory();
         }
      return this;
      }

   @Override
   public Shape generate()
      {
      mFactory.setDims( mRadius, mVAspect, mHAspect );
      return (Shape) mFactory.generate();
      }

   public PrimFactory setDims( double radius, double v_aspect,
                               double h_aspect )
      {
      mRadius = radius;
      mVAspect = v_aspect;
      mHAspect = h_aspect;
      return this;
      }

   private double mRadius;
   private double mVAspect;
   private double mHAspect;
   private PrimShape mShape;
   private DimFactory mFactory;
   }

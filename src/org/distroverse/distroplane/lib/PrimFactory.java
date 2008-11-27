/*
 * <copyleft>
 *
 * Copyright 2007-2008 Dan Reish
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or
 * combining it with Clojure (or a modified version of that program)
 * or clojure-contrib (or a modified version of that library),
 * containing parts covered by the terms of the Common Public License,
 * the licensors of this Program grant you additional permission to
 * convey the resulting work. {Corresponding Source for a non-source
 * form of such a combination shall include the source code for the
 * parts of Clojure and clojure-contrib used as well as that of the
 * covered work.}
 *
 * </copyleft>
 */
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
               return new PrismFactory();
            }
         // Only Java thinks this is necessary:
         return null;
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

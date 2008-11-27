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
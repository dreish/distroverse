/*
 * <copyleft>
 *
 * Copyright 2007-2009 Dan Reish
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
 * containing parts covered by the terms of the Eclipse Public
 * License, the licensors of this Program grant you additional
 * permission to convey the resulting work. {Corresponding Source for
 * a non-source form of such a combination shall include the source
 * code for the parts of Clojure and clojure-contrib used as well as
 * that of the covered work.}
 *
 * </copyleft>
 */
package org.distroverse.distroplane.lib;

import java.util.AbstractList;
import java.util.List;

import com.jme.math.Vector3f;

/**
 * A dynamically-generated polygonal list of vectors.
 * @author dreish
 */
public class DynPolygon
extends AbstractList< Vector3f >
implements List< Vector3f >
   {
   @SuppressWarnings("unused")
   private DynPolygon()
      {
      // Not allowed
      }

   public DynPolygon( int sides, 
                       Vector3f center, Vector3f up, Vector3f right )
      {
      mSides = sides;
      mCenter = center;
      mUp = up;
      mRight = right;
      }

   @Override
   public Vector3f get( int index )
      {
      Vector3f ret = mCenter.clone();
      float theta = (float) (2.0 * Math.PI * index / mSides);
      return ret.addLocal( mUp.mult( (float) Math.cos( theta ) ) )
                .addLocal( mRight.mult( (float) Math.sin( theta ) ) );
      }

   @Override
   public int size()
      {
      return mSides;
      }

   private int mSides;
   private Vector3f mCenter;
   private Vector3f mUp;
   private Vector3f mRight;
   }

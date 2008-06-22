/*
 * Copyright (c) 2007-2008 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * General Public License (GPL) version 3 or later</a>
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

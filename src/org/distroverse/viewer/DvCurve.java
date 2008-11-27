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
/**
 * 
 */
package org.distroverse.viewer;

import org.distroverse.dvtp.MoveSeq;

import com.jme.curve.Curve;
import com.jme.intersection.CollisionResults;
import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;

/**
 * DvCurve is the jME implementation of the movement represented by a
 * MoveSeq message, which combines a polynomial and a sum of sines to
 * define movement in three dimensions.
 * @author dreish
 */
public class DvCurve extends Curve
   {
   /**
    * 
    */
   private static final long serialVersionUID = 9141698020038034251L;

   /**
    * @param n - name
    */
   public DvCurve( String n )
      {
      super( n );
      }

   /**
    * @param n - name
    */
   public DvCurve( String n, MoveSeq ms )
      {
      super( n );
      setMoves( ms );
      }

   /**
    * @param n - name
    * @param cp - control points (unused)
    */
   @SuppressWarnings("unused")
   private DvCurve( String n, Vector3f[] cp )
      {
      super( n, cp );
      // Unreachable.
      }
   
   public void setMoves( MoveSeq ms )
      {
      mMoves = ms;
      }

   /* (non-Javadoc)
    * @see com.jme.curve.Curve#getOrientation(float, float)
    */
   @Override
   public Matrix3f getOrientation( float time, float precision )
      {
      // FIXME this is a joke implementation for testing
      return getOrientation( time, precision, Vector3f.UNIT_Y );
      }

   /* (non-Javadoc)
    * @see com.jme.curve.Curve#getOrientation(float, float, com.jme.math.Vector3f)
    */
   @Override
   public Matrix3f getOrientation( float time, float precision,
                                   Vector3f up )
      {
      Quaternion o = new Quaternion();
      o.slerp( new Quaternion( 1, 0, 0, 0 ),
               new Quaternion( 0.7071f, 0, 0.7071f, 0 ),
               time );
      o.mult( (new Quaternion()).fromAngleAxis( 0, up ) );
      return o.toRotationMatrix();
      }

   /* (non-Javadoc)
    * @see com.jme.curve.Curve#getPoint(float)
    */
   @Override
   public Vector3f getPoint( float time )
      {
      return getPoint( time, new Vector3f() );
      }

   /* (non-Javadoc)
    * @see com.jme.curve.Curve#getPoint(float, com.jme.math.Vector3f)
    */
   @Override
   public Vector3f getPoint( float time, Vector3f store )
      {
      // FIXME this is a joke implementation for testing
      store.x = (float) Math.sin( time ) * 2;
      store.y = (float) Math.sin( time * 1.251398413891 ) * 2;
      store.z = (float) Math.sin( time * 1.449734986243 ) * 2;
      return store;
      }

   /* (non-Javadoc)
    * @see com.jme.scene.Spatial#findCollisions(com.jme.scene.Spatial, com.jme.intersection.CollisionResults)
    */
   @Override
   public void findCollisions( Spatial scene, CollisionResults results )
      {
      throw new UnsupportedOperationException( "DvCurve does not"
                                   + " support collision detection" );
      }

   /* (non-Javadoc)
    * @see com.jme.scene.Spatial#hasCollision(com.jme.scene.Spatial, boolean)
    */
   @Override
   public boolean hasCollision( Spatial scene, boolean checkTriangles )
      {
      throw new UnsupportedOperationException( "DvCurve does not"
                                   + " support collision detection" );
      }

   private MoveSeq mMoves;
   }

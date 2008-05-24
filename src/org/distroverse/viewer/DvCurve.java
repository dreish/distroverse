/**
 * 
 */
package org.distroverse.viewer;

import org.distroverse.dvtp.MoveSeq;

import com.jme.curve.Curve;
import com.jme.intersection.CollisionResults;
import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;

/**
 * DvCurve combines a polynomial and a sum of sines to define movement
 * in three dimensions.
 * @author dreish
 */
public class DvCurve extends Curve
   {
   /**
    * 
    */
   private static final long serialVersionUID = 9141698020038034251L;

   /**
    * @param name
    */
   public DvCurve( String name )
      {
      super( name );
      // TODO Auto-generated constructor stub
      }

   /**
    * @param name
    * @param controlPoints
    */
   public DvCurve( String name, Vector3f[] controlPoints )
      {
      super( name, controlPoints );
      // TODO Auto-generated constructor stub
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
      // TODO Auto-generated method stub
      return null;
      }

   /* (non-Javadoc)
    * @see com.jme.curve.Curve#getOrientation(float, float, com.jme.math.Vector3f)
    */
   @Override
   public Matrix3f getOrientation( float time, float precision,
                                   Vector3f up )
      {
      // TODO Auto-generated method stub
      return null;
      }

   /* (non-Javadoc)
    * @see com.jme.curve.Curve#getPoint(float)
    */
   @Override
   public Vector3f getPoint( float time )
      {
      // TODO Auto-generated method stub
      return null;
      }

   /* (non-Javadoc)
    * @see com.jme.curve.Curve#getPoint(float, com.jme.math.Vector3f)
    */
   @Override
   public Vector3f getPoint( float time, Vector3f store )
      {
      // TODO Auto-generated method stub
      return null;
      }

   /* (non-Javadoc)
    * @see com.jme.scene.Spatial#findCollisions(com.jme.scene.Spatial, com.jme.intersection.CollisionResults)
    */
   @Override
   public void findCollisions( Spatial scene, CollisionResults results )
      {
      // TODO Auto-generated method stub

      }

   /* (non-Javadoc)
    * @see com.jme.scene.Spatial#hasCollision(com.jme.scene.Spatial, boolean)
    */
   @Override
   public boolean hasCollision( Spatial scene, boolean checkTriangles )
      {
      // TODO Auto-generated method stub
      return false;
      }

   private MoveSeq mMoves;
   }

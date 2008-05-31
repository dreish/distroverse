/**
 * 
 */
package org.distroverse.distroplane.lib;

import org.distroverse.core.*;
import org.distroverse.dvtp.*;

import com.jme.math.Vector3f;

/**
 * A BallFactory is a Factory that generates roughly spherical Shapes.
 * <p>
 * Fun facts: for setNumRows(3), the generate()d shape is a regular
 * octahedron. For setNumRows(4), the generate()d shape is not exactly a
 * regular icosahedron, but it could pass for one. (The equatorial band
 * of triangles is stretched too wide toward the poles.) More party
 * trivia: I can't help typing "*-hedrom" instead of "*-hedron".
 * 
 * @author dreish
 * 
 */
public class BallFactory extends ShapeFactory implements DimFactory
   {
   public static final int DEFAULT_NUM_ROWS = 16;
   
   /**
    * Default BallFactory creates reasonably smooth spheres.
    */
   public BallFactory()
      {
      mEquatorialRadius = 1.0;
      mAspectRatio      = 1.0;
      mNumRows          = DEFAULT_NUM_ROWS;
      }
   
   public BallFactory setEquatorialRadius( double r )
      { mEquatorialRadius = r;  return this; }
   public BallFactory setAspectRatio( double a )
      { mAspectRatio = a;  return this; }
   public BallFactory setNumRows( long r )
      { mNumRows = r;  return this; }

   /* (non-Javadoc)
    * @see org.distroverse.core.ShapeFactory#Generate()
    */
   @Override
   public Shape generate()
      {
      Vector3f vertices[][] = generateVertices();
      return generateSurface( vertices );
      }
   
   private Vector3f[][] generateVertices()
      {
      // TODO Handle slices of a ball here, and in AddVertexRow.
      mRenderedRows = Util.safeInt( mNumRows );
      Vector3f vertices[][] = new Vector3f[ mRenderedRows ][];
      for ( int i = 0; i < mRenderedRows; ++i )
         addVertexRow( vertices, i, mRenderedRows );
      return vertices;
      }
   
   private void addVertexRow( Vector3f vertices[][], int row,
                              int total_rows )
      {
      // 'latitude' in radians south of north pole.
      // TODO Make rows evenly spaced in terms of dist., not lat.
      double latitude = Math.PI * row / (total_rows - 1);
      float y = (float) (Math.cos( latitude ) * mEquatorialRadius
                         * mAspectRatio);
      double circle_radius = Math.sin( latitude )
                             * mEquatorialRadius;
      int n_points = Util.safeInt( 
               Math.round((total_rows * 2 - 2) 
                          * Math.sin( latitude )));
      double circle_divisions = n_points;
      // Make sure the last point is the same as the first one.  This
      // turns out to be the right thing to do even for the poles:
      ++n_points;
      vertices[ row ] = new Vector3f[ n_points ];
      double offset = (row % 2 == 1) ? 0.5 : 0.0;
      for ( int i = 0; i < n_points; ++i )
         {
         double longitude;
         if ( n_points == 1 )
            longitude = 0;
         else
            longitude = 2.0 * Math.PI * (i + offset) 
                        / circle_divisions;
         float x = (float) (Math.sin( longitude ) * circle_radius);
         float z = (float) (Math.cos( longitude ) * circle_radius);
         vertices[ row ][ i ] = new Vector3f( x, y, z );
         }
      }

   
   public BallFactory setDims( double radius, double v_aspect,
                               double h_aspect )
      {
      mEquatorialRadius = radius;
      mAspectRatio = v_aspect;
      return this;
      }

   private double mEquatorialRadius;
   private double mAspectRatio;
   private long   mNumRows;
   private int    mRenderedRows;
   }

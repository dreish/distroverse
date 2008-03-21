/**
 * 
 */
package org.distroverse.distroplane.lib;

import org.distroverse.core.*;
import org.distroverse.dvtp.*;
//import javax.media.j3d.*;
import javax.vecmath.*;

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
public class BallFactory extends ShapeFactory
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
   
   public void setEquatorialRadius( double r )
      { mEquatorialRadius = r; }
   public void setAspectRatio( double a )
      { mAspectRatio = a; }
   public void setNumRows( long r )
      { mNumRows = r; }

   /* (non-Javadoc)
    * @see org.distroverse.core.ShapeFactory#Generate()
    */
   @Override
   public Shape generate()
      {
      Point3d vertices[][] = generateVertices();
      return generateSurface( vertices );
      }
   
   private Point3d[][] generateVertices()
      {
      // TODO Handle slices of a ball here, and in AddVertexRow.
      mRenderedRows = Util.safeInt( mNumRows );
      Point3d vertices[][] = new Point3d[ mRenderedRows ][];
      for ( int i = 0; i < mRenderedRows; ++i )
         addVertexRow( vertices, i, mRenderedRows );
      return vertices;
      }
   
   private void addVertexRow( Point3d vertices[][], int row,
                              int total_rows )
      {
      // 'latitude' in radians south of north pole.
      // TODO Make rows evenly spaced in terms of dist., not lat.
      double latitude = Math.PI * row / (total_rows - 1);
      double y = Math.cos( latitude ) * mEquatorialRadius
                 * mAspectRatio;
      double circle_radius = Math.sin( latitude )
                             * mEquatorialRadius;
      int n_points = Util.safeInt( 
               Math.round((total_rows * 2 - 2) 
                          * Math.sin( latitude )));
      double circle_divisions = n_points;
      // Make sure the last point is the same as the first one.  This
      // turns out to be the right thing to do even for the poles:
      ++n_points;
      vertices[ row ] = new Point3d[ n_points ];
      double offset = (row % 2 == 1) ? 0.5 : 0.0;
      for ( int i = 0; i < n_points; ++i )
         {
         double longitude;
         if ( n_points == 1 )
            longitude = 0;
         else
            longitude = 2.0 * Math.PI * (i + offset) 
                        / circle_divisions;
         double x = Math.sin( longitude ) * circle_radius;
         double z = Math.cos( longitude ) * circle_radius;
         vertices[ row ][ i ] = new Point3d( x, y, z );
         }
      }

   private double mEquatorialRadius;
   private double mAspectRatio;
   private long   mNumRows;
   private int    mRenderedRows;
   }

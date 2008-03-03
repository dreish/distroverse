package org.distroverse.distroplane;

import org.distroverse.dvtp.*;
import org.distroverse.core.*;
import javax.vecmath.*;

// import org.distroverse.core.*;

public abstract class ShapeFactory extends Factory
   {
   @Override
   abstract public Shape generate();
   
   /**
    * Generates a Shape connecting the two-dimensional array of
    * Point3ds.
    * @param vertices - A two-dimensional array of points to
    * be knitted together into a surface
    * @param is_closed - Does each row represent a closed curve?
    * (N.B.: to close the ends, make the first and last row
    * the same)
    * @return An org.distoverse.core.Shape connecting the given
    * points with triangles that are as close to equilateral as
    * possible
    */
   protected static Shape generateSurface( Point3d[][] vertices,
                                           boolean     is_closed )
      {
      // TODO Implement GenerateSurface()
      int     n_points_needed   = tsaPointsNeeded( vertices,
                                                   is_closed );
      Point3d triangle_strips[] = new Point3d[ n_points_needed ];
      
      return null;
      }
   
   private static int tsaPointsNeeded( Point3d[][] vertices,
                                       boolean     is_closed )
      {
      int ret = 0;
      for ( int i = 0; i < vertices.length - 1; ++i )
         {
         int row_a = vertices[ i   ].length;
         int row_b = vertices[ i+1 ].length;
         if ( row_a > 1 && is_closed )
            ++row_a;
         if ( row_b > 1 && is_closed )
            ++row_b;
         ret += Util.max( row_a, row_b ) * 2;
         if ( row_a == 1  ||  row_b == 1 )
            --ret;
         }
      
      return ret;
      }

   /**
    * Connects two rows of points with a strip of triangles, keeping
    * them, to the extent reasonably possible, between isosceles and
    * right.  This function is intended to feed the data structure
    * used by TriangleStripArray.
    * @param target - An existing vertex array to which to append
    * @param row_a - One row of points
    * @param row_b - The other row of points
    * @return The number of vertices added for this strip.
    */
   private static int connectWithTriangles( Point3d[] target,
                                            Point3d[] row_a,
                                            Point3d[] row_b )
      {
      int points_added = 0;
      int index_a      = 0;
      int index_b      = 0;
      
      return points_added;
      }
   }

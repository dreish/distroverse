package org.distroverse.distroplane;

import org.distroverse.dvtp.*;
import org.distroverse.core.*;
import javax.vecmath.*;
import java.util.*;

// import org.distroverse.core.*;

public abstract class ShapeFactory implements Factory
   {
   abstract public Shape generate();
   
   /**
    * Generates a Shape connecting the two-dimensional array of
    * Point3ds.  Useful in a variety of subclasses.
    * @param vertices - A two-dimensional array of points to
    * be knitted together into a surface
    * @return An org.distoverse.core.Shape connecting the given
    * points with triangles that are as close to equilateral as
    * possible
    */
   protected static Shape generateSurface( Point3d[][] vertices )
      {
      List<Point3d> triangle_strips = new ArrayList<Point3d>();
      for ( int i = 0; i < vertices.length - 1; ++i )
         {
         if (    vertices[ i   ].length > 1 
              || vertices[ i+1 ].length > 1 )
            connectWithTriangles( triangle_strips,
                                  vertices[ i   ],
                                  vertices[ i+1 ],
                                  is_closed );
         }
      // TODO Implement generateSurface()
      return null;
      }

   /**
    * Connects two rows of points with a strip of triangles, keeping
    * them, to the extent reasonably possible, between isosceles and
    * right.  This function is intended to feed the data structure
    * used by Shape, which is based on the data structure used by
    * Java3d's TriangleStripArray.
    * @param target - An existing vertex array to which to append
    * @param row_0 - One row of points
    * @param row_1 - The other row of points
    * @return The number of vertices added for this strip
    */
   private static void connectWithTriangles( List<Point3d> target,
                                             Point3d[] row_0,
                                             Point3d[] row_1 )
      {
      int index[]      = { 0, 0 };
      Point3d[][]row   = { row_0, row_1 };
      int current_row  = longerDiagonal( row[0], row[1], 
                                         index[0], index[1], 1.0 );
      
      while ( index[0] < row[0].length
              ||  index[1] < row[1].length )
         {
         
         }
      }
   
   /**
    * Returns the index (0 or 1) of the row with the longer diagonal
    * from the currently indexed point to the next point on the opposite
    * row.  A bias value greater than 1.0 will return 0 even if row 1's
    * point is longer by as much as that factor, and a bias below 1.0
    * will have the opposite effect.  Somewhat counterintuitively, this
    * function returns the index of the row with multiple points if one
    * row no longer has any points.  If both rows are at their last
    * point, an exception is thrown.
    * @param row_0 - First row of points
    * @param row_1 - Second row of points
    * @param index_0 - Current index into the first row of points
    * @param index_1 - Current index into the second row of points
    * @param bias - Factor by which to favor row_0
    * @return 0 or 1, depending on which row's diagonal is longer
    */
   private static int longerDiagonal( Point3d[] row_0, Point3d[] row_1,
                                      int index_0, int index_1,
                                      double bias )
      {
      int row = 0;
      boolean row_0_at_end = false;
      boolean row_1_at_end = false;
      
      if ( index_0 == row_0.length - 1 )
         row_0_at_end = true;
      if ( index_1 == row_1.length - 1 )
         row_1_at_end = true;
      if ( row_0_at_end && row_1_at_end )
         throw new IllegalArgumentException( "ShapeFactory."
             + "longerDiagonal called with no more segments" );
      
      if ( row_0_at_end )
         row = 1;
      else if ( row_1_at_end )
         row = 0;
      else 
         {
         double dist_from_0
            = row_0[ index_0 ].distance( row_1[ index_1 + 1 ] );
         double dist_from_1
            = row_1[ index_1 ].distance( row_0[ index_0 + 1 ] );
         if ( dist_from_0 * bias > dist_from_1 )
            row = 0;
         else
            row = 1;
         }

      return row;
      }
   }

/**
 * 
 */
package org.distroverse.distroplane;

import org.distroverse.core.*;
//import javax.media.j3d.*;
import javax.vecmath.*;

/**
 * A BallFactory is a Factory that generates roughly spherical Shapes.
 *
 * @author dreish
 *
 */
public class BallFactory extends ShapeFactory
   {
   /**
    * 
    */
   public BallFactory()
      {
      mEquatorialRadius = 1.0;
      mAspectRatio      = 1.0;
      mNumRows          = 16;
      }

   /* (non-Javadoc)
    * @see org.distroverse.core.ShapeFactory#Generate()
    */
   @Override
   public Shape Generate()
      {
      Point3d vertices[][] = GenerateVertices();
      return GenerateSurface( vertices );
      }
   
   private Point3d[][] GenerateVertices()
      {
      // TODO Handle slices of a ball here.
      mRenderedRows = Util.SafeInt( mNumRows );
      Point3d vertices[][] = new Point3d[ mRenderedRows ][];
      for ( int i = 0; i < mRenderedRows; ++i )
         AddVertexRow( vertices, i, mRenderedRows );
      return vertices;
      }
   
   private void AddVertexRow( Point3d vertices[][], int row,
                              int total_rows )
      {
      
      }

   private double mEquatorialRadius;
   private double mAspectRatio;
   private long   mNumRows;
   private int    mRenderedRows;
   }

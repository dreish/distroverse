/*
 * Copyright (c) 2007 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * Lesser General Public License (GPL) version 3 or later</a>
 */
package org.distroverse.dvtp;

import java.io.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;
import javax.vecmath.*;
import com.jme.scene.TriMesh;

/**
 * This class is part of the DVTP protocol.  It defines how shapes
 * are transmitted from the proxy to the client.  Shape includes
 * texture and image map.
 * 
 * @author dreish
 *
 */
public class Shape implements Serializable
   {
   /**
    * No default constructor; there is no default shape.
    */
   @SuppressWarnings("unused")
   private Shape()
      {
      // Disallowed.
      }
   
   /**
    * Constructor from a List of points and an array of vertex counts,
    * as for a TriangleStripArray.
    * @param points - One-dimensional list of points
    * @param vertex_counts - Number of points in each triangle strip
    */
   public Shape( List<Point3d> points, int[] vertex_counts )
      {
//      Object[] points_objarr = points.toArray();
      Point3d[] points_pointarr 
         = points.toArray( new Point3d[ points.size() ] );
//      Point3d[] points_pointarr = new Point3d[ points_objarr.length ];
//      for ( int i = 0; i < points_objarr.length; ++i )
//         points_pointarr[ i ] = (Point3d) points_objarr[ i ];
      mPoints = new PointArray( points_pointarr );
      mVertexCounts = vertex_counts;
      }
   
//   /**
//    * Get an actual TriangleStripArray object with this Shape.
//    * @return the shape as a TriangleStripArray
//    */
//   public TriangleStripArray asTriangleStripArray()
//      {
//      return new TriangleStripArray( mPoints.p.length,
//                                     GeometryArray.COORDINATES 
//                                     | GeometryArray.BY_REFERENCE,
//                                     mVertexCounts );
//      }
   
   public TriMesh asTriMesh()
      {
      // XXX This is completely broken:
      IntBuffer   vc = IntBuffer.wrap( mVertexCounts );
      FloatBuffer p  = mPoints.asFloatBuffer();
      return new TriMesh( "DvtpShape",
                          p, null, null, null, vc );
      }

   // TODO Add texture fields and methods.
   private PointArray mPoints;
   private int[]      mVertexCounts;
   static final long  serialVersionUID = 1;
   }

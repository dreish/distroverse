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
      Point3d[] points_pointarr 
         = points.toArray( new Point3d[ points.size() ] );
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
      FloatBuffer p  = mPoints.asFloatBuffer();
      IntBuffer   vi = vertexIndices();
      
      return new TriMesh( "DvtpShape",
                          p, null, null, null, vi );
      }
   
   private IntBuffer vertexIndices()
      {
      IntBuffer ret = IntBuffer.allocate( numVertexIndices() );
      
      int pos = 0;
      for ( int vertex_count : mVertexCounts )
         {
         for ( int i = 0; i < vertex_count - 2; ++i )
            {
            int tuple_begin = (i + pos) * 3;
            addThreeTuples( ret, tuple_begin );
            }
         pos += vertex_count;
         }
      
      return ret;
      }
   
   private void addThreeTuples( IntBuffer ib, int n )
      {
      for ( int i = 0; i < 9; ++i )
         ib.put( n + i );
      }
   
   private int numVertexIndices()
      {
      int ret = 0;
      for ( int vertex_count : mVertexCounts )
         ret += (vertex_count - 2) * 9;
      return ret;
      }

   // TODO Add texture fields and methods.
   private PointArray mPoints;
   private int[]      mVertexCounts;
   static final long  serialVersionUID = 1;
   }

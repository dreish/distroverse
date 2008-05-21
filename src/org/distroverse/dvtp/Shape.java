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
import javax.vecmath.Point3d;

import org.distroverse.core.Util;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TriMesh;
import com.jme.util.geom.BufferUtils;

/**
 * This class is part of the DVTP protocol.  It defines how shapes
 * are transmitted from the proxy to the client.  Shape includes
 * texture and image map.
 * 
 * @author dreish
 *
 */
public class Shape implements DvtpExternalizable
   {
   public Shape()
      {
      super();
      }
   
   /**
    * Constructor from a List of points and an array of vertex counts,
    * as for a TriangleStripArray.
    * @param points - One-dimensional list of points
    * @param vertex_counts - Number of points in each triangle strip
    */
   @Deprecated
   public Shape( List<Point3d> points, int[] vertex_counts,
                 @SuppressWarnings("unused") int dummy )
      {
      Point3d[] points_arr 
         = points.toArray( new Point3d[ points.size() ] );
      mPoints = new PointArray( points_arr );
      mVertexCounts = vertex_counts;
      }

   /**
    * Constructor from a List of vectors and an array of vertex counts,
    * as for a TriangleStripArray.
    * @param points - One-dimensional list of points
    * @param vertex_counts - Number of points in each triangle strip
    */
   public Shape( List<Vector3f> vectors, int[] vertex_counts )
      {
      Vector3f[] vectors_arr 
         = vectors.toArray( new Vector3f[ vectors.size() ] );
      mPoints = new PointArray( vectors_arr );
      mVertexCounts = vertex_counts;
      }

   public int getClassNumber()
      {  return 10;  }

   public TriMesh asTriMesh()
      {
      FloatBuffer p  = mPoints.asFloatBuffer();
      IntBuffer   vi = vertexIndices();

      // XXX Obviously need to take colors as a parameter, and provide
      // a way to set all vertices to the same color
      ColorRGBA[] colors = new ColorRGBA[ numVertexIndices() ];
      for ( int i = 0; i < colors.length; ++i )
         colors[ i ] = new ColorRGBA( (float) Math.random(),
                                      (float) Math.random(),
                                      (float) Math.random(),
                                      1 );

      TriMesh tm = new TriMesh( "DvtpShape",
                                p, null, 
                                BufferUtils.createFloatBuffer( colors ),
                                p, vi );
      tm.setModelBound( new BoundingBox() );
      tm.updateModelBound();
            
      return tm;
      }
   
   private IntBuffer vertexIndices()
      {
      IntBuffer ret = IntBuffer.allocate( numVertexIndices() );
      
      int pos = 0;
      for ( int vertex_count : mVertexCounts )
         {
         for ( int i = 0; i < vertex_count - 2; ++i )
            {
            int tuple_begin = i + pos;
            addTriangleIndices( ret, tuple_begin );
            }
         pos += vertex_count;
         }
      
      return ret;
      }
   
   private void addTriangleIndices( IntBuffer ib, int n )
      {
      for ( int i = 0; i < 3; ++i )
         ib.put( n + i );
      }
   
   private int numVertexIndices()
      {
      int ret = 0;
      for ( int vertex_count : mVertexCounts )
         ret += (vertex_count - 2) * 3;
      return ret;
      }

   public void readExternal( ObjectInput in )
   throws IOException, ClassNotFoundException
      {
      (mPoints = new PointArray()).readExternal( in );
      int num_vcs = Util.safeInt( CompactUlong.externalAsLong( in ) );
      CompactUlong[] vcs = DvtpObject.readArray( in, num_vcs, 
                                                 CompactUlong.class );
      for ( int i = 0; i < vcs.length; ++i )
         mVertexCounts[ i ] = Util.safeInt( vcs[ i ].toLong() );
      }

   public void writeExternal( ObjectOutput out ) throws IOException
      {
      mPoints.writeExternal( out );
      CompactUlong.longAsExternal( out, mVertexCounts.length );
      for ( int vc : mVertexCounts )
         CompactUlong.longAsExternal( out, vc );
      }

   // TODO Add texture fields and methods.
   private PointArray mPoints;
   private int[]      mVertexCounts;
   }

/*
 * Copyright (c) 2007-2008 Dan Reish.
 *
 * For license details, see the file COPYING-L in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/lgpl.html">GNU
 * Lesser General Public License (LGPL) version 3 or later</a>
 */
package org.distroverse.dvtp;

import java.io.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import org.distroverse.core.Util;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TriMesh;
import com.jme.util.geom.BufferUtils;

//immutable

/**
 * This class is part of the DVTP protocol.  It defines how shapes
 * are transmitted from the proxy to the client.  Shape includes
 * texture and image map.
 *
 * @author dreish
 *
 */
public final class Shape implements DvtpExternalizable
   {
   public Shape( InputStream in )
   throws IOException, ClassNotFoundException
      {
      super();
      readExternal( in );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings( "unused" )
   private Shape()
      {
      super();
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
      mVertexCounts = vertex_counts.clone();
      }

   public int getClassNumber()
      {  return 10;  }

   @Override
   public boolean equals( Object o )
      {
      if ( o instanceof Shape )
         {
         Shape s = (Shape) o;
         return (   mPoints.equals( s.mPoints )
                 && Arrays.equals( mVertexCounts, s.mVertexCounts ));
         }
      return false;
      }

   @Override
   public int hashCode()
      {
      return mPoints.hashCode()
             ^ Arrays.hashCode( mVertexCounts );
      }

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

   private void readExternal( InputStream in )
   throws IOException, ClassNotFoundException
      {
      mPoints = new PointArray( in );
      int num_vcs = Util.safeInt( ULong.externalAsLong( in ) );
      ULong[] vcs = DvtpObject.readArray( in, num_vcs, ULong.class, 0 );
      mVertexCounts = new int[ num_vcs ];
      for ( int i = 0; i < vcs.length; ++i )
         mVertexCounts[ i ] = Util.safeInt( vcs[ i ].toLong() );
      }

   public void writeExternal( OutputStream out ) throws IOException
      {
      mPoints.writeExternal( out );
      ULong.longAsExternal( out, mVertexCounts.length );
      for ( int vc : mVertexCounts )
         ULong.longAsExternal( out, vc );
      }

   public String prettyPrint()
      {
      StringBuilder ret = new StringBuilder();
      ret.append( "(Shape " );
      ret.append( Util.prettyPrintList( mPoints ) );
      ret.append( " '(" );
      for ( int i : mVertexCounts )
         {
         ret.append( i );
         ret.append( ' ' );
         }
      ret.deleteCharAt( ret.length() - 1 );
      ret.append( "))" );
      return ret.toString();
      }

   // TODO Add texture fields and methods.
   private PointArray mPoints;
   private int[]      mVertexCounts;
   }

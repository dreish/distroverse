package org.distroverse.dvtp;

import java.io.*;
import java.util.*;
import javax.vecmath.*;
import javax.media.j3d.*;

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
   public Shape()
      {
      // TODO Auto-generated constructor stub
      }
   
   public Shape( List<Point3d> points, int[] vertex_counts )
      {
      mPoints = new PointArray( (Point3d[]) points.toArray() );
      mVertexCounts = vertex_counts;
      }
   
   public TriangleStripArray newTriangleStripArray()
      {
      TriangleStripArray ret 
         = new TriangleStripArray( mPoints.p.length,
                                   GeometryArray.COORDINATES 
                                   | GeometryArray.BY_REFERENCE,
                                   mVertexCounts );
      return ret;
      }

   private PointArray mPoints;
   private int[]      mVertexCounts;
   static final long serialVersionUID = 1;
   }

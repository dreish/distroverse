/*
 * Copyright (c) 2007 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * Lesser General Public License (GPL) version 3 or later</a>
 */
package org.distroverse.dvtp;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.vecmath.Point3d;

/**
 * @author dreish
 */
public class PointArray implements Serializable
   {
   /**
    * The float buffer.
    */
   private FloatBuffer fb;
   
   /**
    * Constructor with the size of the array, in points (not floats).
    * @param n - capacity in number of (x,y,z) tuples
    */
   public PointArray( int n )
      {
      allocate( n );
      }
   
   /**
    * Constructor with an existing array of Point3ds.
    * @param ap
    */
   public PointArray( Point3d[] ap )
      {
      allocate( ap.length );
      for ( Point3d point : ap )
         {
         fb.put( (float) point.x );
         fb.put( (float) point.y );
         fb.put( (float) point.z );
         }
      fb.rewind();
      }
   
   private void allocate( int n_points )
      {
      fb = ByteBuffer.allocateDirect( (n_points * 3) * 4 )
                     .asFloatBuffer();  
      }
   
   // FIXME Implement PointArray.writeObject()
   // FIXME Implement PointArray.readObject()

   private static final long serialVersionUID = 1;

   public FloatBuffer asFloatBuffer()
      {  return fb;  }

   public int length()
      {  return fb.limit();  }
   }

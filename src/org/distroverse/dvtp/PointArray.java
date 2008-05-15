/*
 * Copyright (c) 2007 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * Lesser General Public License (GPL) version 3 or later</a>
 */
package org.distroverse.dvtp;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.vecmath.Point3d;

import org.distroverse.core.Util;

import com.jme.math.Vector3f;
import com.jme.util.geom.BufferUtils;

/**
 * The external format begins with the number of tuples, followed by
 * those tuples of double-precision IEEE 754 numbers.  This
 * implementation discards precision, storing the points as three
 * floats, rather than three double, because jMonkeyEngine only uses
 * floats.
 * @author dreish
 */
public class PointArray implements DvtpExternalizable
   {
   /**
    * The float buffer.
    */
   private FloatBuffer mFb;
   
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
      Vector3f[] ap_f = new Vector3f[ ap.length ];
      for ( int i = 0; i < ap.length; ++i )
         ap_f[ i ] = new Vector3f( (float)ap[i].x, 
                                   (float)ap[i].y, 
                                   (float)ap[i].z );
      mFb = BufferUtils.createFloatBuffer( ap_f );
      }
   
   private void allocate( int n_points )
      {
      mFb = ByteBuffer.allocateDirect( (n_points * 3) * 4 )
                     .asFloatBuffer();  
      }
   
   private static final long serialVersionUID = 1;

   public FloatBuffer asFloatBuffer()
      {  return mFb;  }

   /**
    * @return - the number of points
    */
   public int length()
      {  return mFb.limit() / 3;  }

   public int getClassNumber()
      {  return 4;  }

   public void readExternal( ObjectInput in ) throws IOException
      {
      int len = Util.safeInt( CompactUlong.externalAsLong( in ) * 3 );
      Vector3f[] ap_f = new Vector3f[ len ];
      for ( int i = 0; i < len * 3; ++i )
         ap_f[ i ] = new Vector3f( (float) in.readDouble(),
                                   (float) in.readDouble(),
                                   (float) in.readDouble() );
      mFb = BufferUtils.createFloatBuffer( ap_f );
      }

   public void writeExternal( ObjectOutput out ) throws IOException
      {
      CompactUlong.longAsExternal( out, length() );
      float[] fa = mFb.array();
      for ( float f : fa )
         out.writeDouble( f );
      }
   }

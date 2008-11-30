/*
 * Copyright (c) 2007-2008 Dan Reish.
 *
 * For license details, see the file COPYING-L in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/lgpl.html">GNU
 * Lesser General Public License (LGPL) version 3 or later</a>
 */
package org.distroverse.dvtp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.FloatBuffer;

import org.distroverse.core.Util;

import com.jme.math.Vector3f;
import com.jme.util.geom.BufferUtils;

//immutable

/**
 * The external format begins with the number of tuples, followed by
 * those tuples of double-precision IEEE 754 numbers.  This
 * implementation discards precision, storing the points as three
 * floats, rather than three double, because jMonkeyEngine only uses
 * floats.
 * @author dreish
 */
public final class PointArray implements DvtpExternalizable
   {
   public PointArray( InputStream in ) throws IOException
      {
      super();
      readExternal( in );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings( "unused" )
   private PointArray()
      {
      super();
      mFb = null;
      }

   /**
    * Constructor with an existing array of Vector3fs.
    * @param ap
    */
   public PointArray( Vector3f[] ap )
      {
      mFb = BufferUtils.createFloatBuffer( ap ).asReadOnlyBuffer();
      }

   public FloatBuffer asFloatBuffer()
      {  return mFb.asReadOnlyBuffer();  }

   /**
    * @return - the number of points
    */
   public int length()
      {  return mFb.limit() / 3;  }

   public int getClassNumber()
      {  return 4;  }

   @Override
   public boolean equals( Object o )
      {
      return (o instanceof PointArray
              && ((PointArray) o).mFb.equals( mFb ));
      }

   @Override
   public int hashCode()
      {
      return mFb.hashCode();
      }

   private void readExternal( InputStream in ) throws IOException
      {
      int len = Util.safeInt( ULong.externalAsLong( in ) );
      Vector3f[] ap_f = new Vector3f[ len ];
      for ( int i = 0; i < len; ++i )
         ap_f[ i ] = new Vector3f( Flo.externalAsFloat( in ),
                                   Flo.externalAsFloat( in ),
                                   Flo.externalAsFloat( in ) );
      mFb = BufferUtils.createFloatBuffer( ap_f ).asReadOnlyBuffer();
      }

   public void writeExternal( OutputStream out ) throws IOException
      {
      ULong.longAsExternal( out, length() );
      float[] fa;
      if ( mFb.hasArray() )
         fa = mFb.array();
      else
         {
         fa = new float[ mFb.limit() ];
         mFb.get( fa );
         mFb.rewind();
         }
      for ( float f : fa )
         Flo.floatAsExternal( out, f );
      }

   public String prettyPrint()
      {
      return "(PointArray " + printFloatBuffer() + ")";
      }

   private String printFloatBuffer()
      {
      StringBuilder ret = new StringBuilder( "{" );
      float[] fa = new float[ mFb.limit() ];
      mFb.get( fa );
      mFb.rewind();
      for ( float f : fa )
         {
         ret.append( f );
         ret.append( ' ' );
         }
      ret.deleteCharAt( ret.length() - 1 );
      ret.append( '}' );
      return ret.toString();
      }

   private FloatBuffer mFb;
   }

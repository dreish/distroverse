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
import java.util.Arrays;

import org.distroverse.core.Util;

import com.jme.math.Vector3f;

/**
 * Defines movement and rotation in three dimensions:
 * - Move
 *   - A polynomial degree (N)
 *   - N+1 vectors
 *   - A sinusoidal count (M)
 *   - M vectors
 *   - M period floats
 *   - M offset floats
 * - Rotation
 *   - A polynomial degree (N)
 *   - N+1 quaternions
 *   - A sinusoidal count (M)
 *   - M quaternions
 *   - M period floats
 *   - M offset floats
 * - A duration -- negative means "until replaced"
 *
 * Time is measured in seconds, and vectors are in meters.
 *
 * @author dreish
 */
public class Move implements DvtpExternalizable
   {
   public Move()
      {
      super();
      mMoveDegree = 0;
      mMovePolyVecs = new Vec[ 0 ];
      mMoveSins = 0;
      mMoveSinVecs = new Vec[ 0 ];

      mRotSinPeriods = mRotSinOffsets
         = mMoveSinPeriods = mMoveSinOffsets
         = new Flo[ 0 ];
      mRotSins = 0;

      mRotDegree = 0;
      mRotPolyQuats = new Quat[ 0 ];
      mRotSinQuats = new Quat[ 0 ];

      mDuration = new Flo( -1.0f );
      }

   /**
    * Constructs a simple stationary Move of infinite duration.
    * @param pos
    * @param rot
    */
   public Move( Vec pos, Quat rot )
      {
      super();
      short_init( pos, rot, new Flo( -1 ) );
      }

   /**
    * Constructs a simple stationary Move of the specified duration.
    * @param pos
    * @param rot
    */
   public Move( Vec pos, Quat rot, Flo dur )
      {
      super();
      short_init( pos, rot, dur );
      }

   void short_init( Vec pos, Quat rot, Flo dur )
      {
      mMoveDegree = 1;
      mMovePolyVecs = new Vec[ 1 ];
      mMovePolyVecs[ 0 ] = pos;
      mMoveSins = 0;
      mMoveSinVecs = new Vec[ 0 ];

      mRotSinPeriods = mRotSinOffsets
         = mMoveSinPeriods = mMoveSinOffsets
         = new Flo[ 0 ];
      mRotSins = 0;

      mRotDegree = 1;
      mRotPolyQuats = new Quat[ 1 ];
      mRotPolyQuats[ 0 ] = rot;
      mRotSinQuats = new Quat[ 0 ];

      mDuration = dur;
      }

   public Move( Vec[] poly_vecs, Vec[] sin_vecs,
                Flo[] move_periods, Flo[] move_offsets,

                Quat[] poly_quats, Quat[] sin_quats,
                Flo[] rot_periods, Flo[] rot_offsets,

                Flo duration )
      {
      super();
      if ( poly_vecs.length == 0 )
         throw new IllegalArgumentException( "poly_vecs must contain"
                                             + " at least one vector" );
      if ( poly_quats.length == 0 )
         throw new IllegalArgumentException( "poly_quats must contain"
                                         + " at least one quaternion" );

      mMoveDegree     = poly_vecs.length;
      mMovePolyVecs   = poly_vecs.clone();
      mMoveSins       = sin_vecs.length;
      mMoveSinVecs    = sin_vecs.clone();
      mMoveSinPeriods = move_periods.clone();
      mMoveSinOffsets = move_offsets.clone();

      mRotDegree      = poly_quats.length;
      mRotPolyQuats   = poly_quats.clone();
      mRotSins        = rot_periods.length;
      mRotSinQuats    = sin_quats.clone();
      mRotSinPeriods  = rot_periods.clone();
      mRotSinOffsets  = rot_offsets.clone();

      mDuration = duration;
      }

   public int getClassNumber()
      {  return 13;  }

   @Override
   public boolean equals( Object o )
      {
      if ( o.getClass().equals( this.getClass() ) )
         {
         Move m = ((Move) o);
         return (m.mMoveDegree == mMoveDegree
                 &&  Arrays.equals( m.mMovePolyVecs, mMovePolyVecs )
                 &&  m.mMoveSins == mMoveSins
                 &&  Arrays.equals( m.mMoveSinVecs, mMoveSinVecs )
                 &&  Arrays.equals( m.mMoveSinPeriods, mMoveSinPeriods )
                 &&  Arrays.equals( m.mMoveSinOffsets, mMoveSinOffsets )
                 &&  m.mRotDegree == mRotDegree
                 &&  Arrays.equals( m.mRotPolyQuats, mRotPolyQuats )
                 &&  m.mRotSins == mRotSins
                 &&  Arrays.equals( m.mRotSinQuats, mRotSinQuats )
                 &&  Arrays.equals( m.mRotSinPeriods, mRotSinPeriods )
                 &&  Arrays.equals( m.mRotSinOffsets, mRotSinOffsets )
                 &&  m.mDuration.equals( mDuration ));
         }
      return false;
      }

   @Override
   public int hashCode()
      {
      return mMoveDegree
             ^ Arrays.hashCode( mMovePolyVecs   )
             ^ mMoveSins  * 923521
             ^ Arrays.hashCode( mMoveSinVecs    )
             ^ Arrays.hashCode( mMoveSinPeriods )
             ^ Arrays.hashCode( mMoveSinOffsets )
             ^ mRotDegree * 6436343
             ^ Arrays.hashCode( mRotPolyQuats   )
             ^ mRotSins   * 2476099
             ^ Arrays.hashCode( mRotSinQuats    )
             ^ Arrays.hashCode( mRotSinPeriods  )
             ^ Arrays.hashCode( mRotSinOffsets  );
      }

   // XXX needs accessors

   public Vector3f initialPosition()
      {
      Vector3f ret = mMovePolyVecs[ 0 ].asVector3f();
      for ( int i = 0; i < mMoveSins; ++i )
         {
         float coefficient
            = (float) Math.sin( mMoveSinOffsets[ i ].asFloat() );
         ret.addLocal( mMoveSinVecs[ i ].asVector3f()
                                        .mult( coefficient ) );
         }
      return ret;
      }

   /* (non-Javadoc)
    * @see java.io.Externalizable#readExternal(java.io.InputStream)
    */
   public void readExternal( InputStream in )
   throws IOException, ClassNotFoundException
      {
      mMoveDegree = Util.safeInt( ULong.externalAsLong( in ) + 1 );
      mMovePolyVecs
         = DvtpObject.readArray( in, mMoveDegree, Vec.class );
      mMoveSins =  Util.safeInt( ULong.externalAsLong( in ) );
      mMoveSinVecs = DvtpObject.readArray( in, mMoveSins, Vec.class );
      mMoveSinPeriods
         = DvtpObject.readArray( in, mMoveSins, Flo.class );
      mMoveSinOffsets
         = DvtpObject.readArray( in, mMoveSins, Flo.class );

      mRotDegree = Util.safeInt( ULong.externalAsLong( in ) + 1 );
      mRotPolyQuats
         = DvtpObject.readArray( in, mRotDegree, Quat.class );
      mRotSins =  Util.safeInt( ULong.externalAsLong( in ) );
      mRotSinQuats = DvtpObject.readArray( in, mRotSins, Quat.class );
      mRotSinPeriods
         = DvtpObject.readArray( in, mRotSins, Flo.class );
      mRotSinOffsets
         = DvtpObject.readArray( in, mRotSins, Flo.class );

      (mDuration = new Flo()).readExternal( in );
      }

   /* (non-Javadoc)
    * @see java.io.Externalizable#writeExternal(java.io.OutputStream)
    */
   public void writeExternal( OutputStream out ) throws IOException
      {
      ULong.longAsExternal( out, mMoveDegree - 1 );
      DvtpObject.writeArray( out, mMovePolyVecs );
      ULong.longAsExternal( out, mMoveSins );
      DvtpObject.writeArray( out, mMoveSinVecs );
      DvtpObject.writeArray( out, mMoveSinPeriods );
      DvtpObject.writeArray( out, mMoveSinOffsets );

      ULong.longAsExternal( out, mRotDegree - 1 );
      DvtpObject.writeArray( out, mRotPolyQuats );
      ULong.longAsExternal( out, mRotSins );
      DvtpObject.writeArray( out, mRotSinQuats );
      DvtpObject.writeArray( out, mRotSinPeriods );
      DvtpObject.writeArray( out, mRotSinOffsets );

      mDuration.writeExternal( out );
      }

   public String prettyPrint()
      {
      return "(Move "
             + Util.prettyPrintList( mMoveDegree, mMovePolyVecs,
                   mMoveSins, mMoveSinVecs, mMoveSinPeriods,
                   mMoveSinOffsets, mRotDegree, mRotPolyQuats, mRotSins,
                   mRotSinQuats, mRotSinPeriods, mRotSinOffsets,
                   mDuration )
             + ")";
      }

   // Here and below, "degree" is actually the degree of the polynomial
   // plus one.
   private int mMoveDegree;
   private Vec[] mMovePolyVecs;
   private int mMoveSins;
   private Vec[] mMoveSinVecs;
   private Flo[] mMoveSinPeriods;
   private Flo[] mMoveSinOffsets;

   private int mRotDegree;
   private Quat[] mRotPolyQuats;
   private int mRotSins;
   private Quat[] mRotSinQuats;
   private Flo[] mRotSinPeriods;
   private Flo[] mRotSinOffsets;

   private Flo mDuration;
   }

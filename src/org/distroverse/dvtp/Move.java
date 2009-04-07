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
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;

import org.distroverse.core.Util;

import com.jme.math.Matrix4f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

//immutable

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
   public Move( InputStream in )
   throws IOException, ClassNotFoundException
      {
      super();
      mMoveDegree = Util.safeInt( ULong.externalAsLong( in ) + 1 );
      mMovePolyVecs
         = DvtpObject.readArray( in, mMoveDegree, Vec.class, 11 );
      mMoveSins =  Util.safeInt( ULong.externalAsLong( in ) );
      mMoveSinVecs
         = DvtpObject.readArray( in, mMoveSins, Vec.class, 11 );
      mMoveSinPeriods
         = DvtpObject.readArray( in, mMoveSins, Real.class, 33 );
      mMoveSinOffsets
         = DvtpObject.readArray( in, mMoveSins, Flo.class, 15 );

      mRotDegree = Util.safeInt( ULong.externalAsLong( in ) + 1 );
      mRotPolyQuats
         = DvtpObject.readArray( in, mRotDegree, Quat.class, 16 );
      mRotSins =  Util.safeInt( ULong.externalAsLong( in ) );
      mRotSinQuats
         = DvtpObject.readArray( in, mRotSins, Quat.class, 16 );
      mRotSinPeriods
         = DvtpObject.readArray( in, mRotSins, Real.class, 33 );
      mRotSinOffsets
         = DvtpObject.readArray( in, mRotSins, Flo.class, 15 );

      mDuration = new Real( in );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings("unused")
   private Move()
      {
      super();
      mMoveDegree = 0;
      mMovePolyVecs = null;
      mMoveSins = 0;
      mMoveSinVecs = null;

      mRotSinPeriods = mMoveSinPeriods = null;
      mRotSinOffsets = mMoveSinOffsets = null;

      mRotSins = 0;

      mRotDegree = 1;
      mRotPolyQuats = null;
      mRotSinQuats = null;

      mDuration = null;
      }

   /**
    * Constructs a simple stationary Move of infinite duration.
    * @param pos
    * @param rot
    */
   public static Move getNew( Vec pos, Quat rot )
      {
      return new Move( pos, rot, new Real( new BigDecimal( -1 ) ) );
      }

   /**
    * Constructs a simple stationary Move of the specified duration.
    * @param pos
    * @param rot
    */
   public Move( Vec pos, Quat rot, Real dur )
      {
      super();
      mMoveDegree = 1;
      mMovePolyVecs = new Vec[ 1 ];
      mMovePolyVecs[ 0 ] = pos;
      mMoveSins = 0;
      mMoveSinVecs = new Vec[ 0 ];

      mRotSinPeriods = mMoveSinPeriods = new Real[ 0 ];
      mRotSinOffsets = mMoveSinOffsets = new Flo[ 0 ];
      mRotSins = 0;

      mRotDegree = 1;
      mRotPolyQuats = new Quat[ 1 ];
      mRotPolyQuats[ 0 ] = rot;
      mRotSinQuats = new Quat[ 0 ];

      mDuration = dur;
      }

   public Move( Vec[] poly_vecs, Vec[] sin_vecs,
                Real[] move_periods, Flo[] move_offsets,

                Quat[] poly_quats, Quat[] sin_quats,
                Real[] rot_periods, Flo[] rot_offsets,

                Real duration )
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


   public int getMoveDegree()
      {
      return mMoveDegree;
      }

   public Vec getMovePolyVecs( int n )
      {
      return mMovePolyVecs[ n ];
      }

   public int getMoveSins()
      {
      return mMoveSins;
      }

   public Vec getMoveSinVecs( int n )
      {
      return mMoveSinVecs[ n ];
      }

   public Real getMoveSinPeriods( int n )
      {
      return mMoveSinPeriods[ n ];
      }

   public Flo getMoveSinOffsets( int n )
      {
      return mMoveSinOffsets[ n ];
      }

   public int getRotDegree()
      {
      return mRotDegree;
      }

   public Quat getRotPolyQuats( int n )
      {
      return mRotPolyQuats[ n ];
      }

   public int getRotSins()
      {
      return mRotSins;
      }

   public Quat getRotSinQuats( int n )
      {
      return mRotSinQuats[ n ];
      }

   public Real getRotSinPeriods( int n )
      {
      return mRotSinPeriods[ n ];
      }

   public Flo getRotSinOffsets( int n )
      {
      return mRotSinOffsets[ n ];
      }

   public Real getDuration()
      {
      return mDuration;
      }

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

   public Quaternion initialRotation()
      {
      Quaternion ret = mRotPolyQuats[ 0 ].asQuaternion();
      for ( int i = 0; i < mRotSins; ++i )
         {
         float coefficient
            = (float) Math.sin( mRotSinOffsets[ i ].asFloat() );
         ret.slerp( ret.mult( mRotPolyQuats[ i ].asQuaternion() ),
                    coefficient );
         }
      return ret;
      }

   private Vector3f positionAt( BigDecimal bd_time )
      {
      Vector3f ret = mMovePolyVecs[ 0 ].asVector3f();

      BigDecimal poly_term = bd_time;
      MathContext mult_context = new MathContext( bd_time.scale() * 2 );
      for ( int i = 1; i < mMoveDegree; ++i )
         {
         poly_term = poly_term.multiply( bd_time, mult_context );
         float coefficient = poly_term.floatValue();

         ret.addLocal( mMovePolyVecs[ i ].asVector3f()
                                         .mult( coefficient ) );
         }

      for ( int i = 0; i < mMoveSins; ++i )
         {
         float time_div_period
            = bd_time.divide( mMoveSinPeriods[ i ].toBigDecimal() )
                     .remainder( BigDecimal.ONE )
                     .floatValue();
         if ( time_div_period < 0 )
            ++time_div_period;
         float coefficient
            = (float) Math.sin( mMoveSinOffsets[ i ].asFloat()
                                + time_div_period * 2.0 * Math.PI );
         ret.addLocal( mMoveSinVecs[ i ].asVector3f()
                                        .mult( coefficient ) );
         }

      return ret;
      }

   private Quaternion rotationAt( BigDecimal bd_time )
      {
      Quaternion ret = mRotPolyQuats[ 0 ].asQuaternion();

      BigDecimal poly_term = bd_time;
      MathContext mult_context = new MathContext( bd_time.scale() * 2 );
      for ( int i = 1; i < mRotDegree; ++i )
         {
         poly_term = poly_term.multiply( bd_time, mult_context );
         float coefficient = poly_term.floatValue();

         ret.slerp( ret.mult( mRotPolyQuats[ i ].asQuaternion() ),
                    coefficient );
         }

      for ( int i = 0; i < mRotSins; ++i )
         {
         float time_div_period
            = bd_time.divide( mRotSinPeriods[ i ].toBigDecimal() )
                     .remainder( BigDecimal.ONE )
                     .floatValue();
         if ( time_div_period < 0 )
            ++time_div_period;
         float coefficient
            = (float) Math.sin( mRotSinOffsets[ i ].asFloat()
                                + time_div_period * 2.0 * Math.PI );
         ret.slerp( ret.mult( mRotSinQuats[ i ].asQuaternion() ),
                    coefficient );
         }

      return ret;
      }

   /**
    * Returns the transformation for this Move at the given time, if the
    * Move were unbounded in time.  (Typically, it should be >= 0 and
    * <= mDuration.)
    * @param bd_time
    * @return
    */
   public Matrix4f transformAt( BigDecimal bd_time )
      {
      Matrix4f ret = new Matrix4f();
      ret.setTranslation( positionAt( bd_time ) );
      ret.setRotationQuaternion( rotationAt( bd_time ) );
      return ret;
      }

   /**
    * Returns the radius of a sphere around the origin beyond which the
    * Move is guaranteed not to extend beyond.  The Move path may or may
    * not extend to the edge of the sphere.
    * @return
    */
   public float getRange()
      {
      if ( mCalculatedRange )
         return mRange;

      float range = 0;
      float time_adj = 1;
      float dur = mDuration.toFloat();
      dur += Math.ulp( dur );
      for ( int i = 0; i < mMoveDegree; ++i )
         {
         range += mMovePolyVecs[ i ].asVector3f().length() * time_adj;
         time_adj *= dur;
         }

      for ( int i = 0; i < mMoveSins; ++i )
         range += mMoveSinVecs[ i ].asVector3f().length();

      mRange = range;
      mCalculatedRange = true;

      return range;
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
   private final int mMoveDegree;
   private final Vec[] mMovePolyVecs;
   private final int mMoveSins;
   private final Vec[] mMoveSinVecs;
   private final Real[] mMoveSinPeriods;
   private final Flo[] mMoveSinOffsets;

   private final int mRotDegree;
   private final Quat[] mRotPolyQuats;
   private final int mRotSins;
   private final Quat[] mRotSinQuats;
   private final Real[] mRotSinPeriods;
   private final Flo[] mRotSinOffsets;

   private final Real mDuration;

   private boolean mCalculatedRange;
   private float   mRange;
   }

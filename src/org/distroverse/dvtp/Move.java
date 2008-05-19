package org.distroverse.dvtp;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.distroverse.core.Util;

import com.jme.math.Quaternion;
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
 * - A duration
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
      mMoveDegree = 1;
      mMovePolyVecs = new Vec[ 1 ];
      mMovePolyVecs[ 0 ] = pos;
      mMoveSins = 0;
      mMoveSinVecs = new Vec[ 0 ];
      
      mRotSinPeriods = mRotSinOffsets 
         = mMoveSinPeriods = mMoveSinOffsets 
         = new Flo[ 0 ];
      
      mRotDegree = 1;
      mRotPolyQuats = new Quat[ 1 ];
      mRotPolyQuats[ 0 ] = rot;
      mRotSinQuats = new Quat[ 0 ];
      
      mDuration = new Flo( -1.0f );
      }

   public int getClassNumber()
      {  return 13;  }

   /* (non-Javadoc)
    * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
    */
   public void readExternal( ObjectInput in ) 
   throws IOException, ClassNotFoundException
      {
      mMoveDegree = Util.safeInt( CompactUlong.externalAsLong( in ) );
      mMovePolyVecs
         = DvtpObject.readArray( in, mMoveDegree, Vec.class );
      mMoveSins =  Util.safeInt( CompactUlong.externalAsLong( in ) );
      mMoveSinVecs = DvtpObject.readArray( in, mMoveSins, Vec.class );
      mMoveSinPeriods
         = DvtpObject.readArray( in, mMoveSins, Flo.class );
      mMoveSinOffsets
         = DvtpObject.readArray( in, mMoveSins, Flo.class );
      
      mRotDegree = Util.safeInt( CompactUlong.externalAsLong( in ) );
      mRotPolyQuats
         = DvtpObject.readArray( in, mRotDegree, Quat.class );
      mRotSins =  Util.safeInt( CompactUlong.externalAsLong( in ) );
      mRotSinQuats = DvtpObject.readArray( in, mRotSins, Quat.class );
      mRotSinPeriods
         = DvtpObject.readArray( in, mRotSins, Flo.class );
      mRotSinOffsets
         = DvtpObject.readArray( in, mRotSins, Flo.class );
      
      (mDuration = new Flo()).readExternal( in );
      }

   /* (non-Javadoc)
    * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
    */
   public void writeExternal( ObjectOutput out ) throws IOException
      {
      CompactUlong.longAsExternal( out, mMoveDegree );
      DvtpObject.writeArray( out, mMovePolyVecs );
      CompactUlong.longAsExternal( out, mMoveSins );
      DvtpObject.writeArray( out, mMoveSinVecs );
      DvtpObject.writeArray( out, mMoveSinPeriods );
      DvtpObject.writeArray( out, mMoveSinOffsets );
      
      CompactUlong.longAsExternal( out, mRotDegree );
      DvtpObject.writeArray( out, mRotPolyQuats );
      CompactUlong.longAsExternal( out, mRotSins );
      DvtpObject.writeArray( out, mRotSinQuats );
      DvtpObject.writeArray( out, mRotSinPeriods );
      DvtpObject.writeArray( out, mRotSinOffsets );
      
      mDuration.writeExternal( out );
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

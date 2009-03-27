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
import java.util.Arrays;

import org.distroverse.core.Util;

import com.jme.math.Matrix4f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

//immutable

public final class MoveSeq implements DvtpExternalizable
   {
   public enum RepeatType { ONCE, LOOP, BOUNCE };

   public MoveSeq( InputStream in )
   throws IOException, ClassNotFoundException
      {
      super();
      int num_moves = Util.safeInt( ULong.externalAsLong( in ) );
      if ( num_moves == 0 )
         throw new IOException( "Malformed MoveSeq in input" );
      mMoves = DvtpObject.readArray( in, num_moves, Move.class, 13 );
      int repeat_int
         = Util.safeInt( ULong.externalAsLong( in ) );
      checkRepeatType( repeat_int );
      mRepeatType = RepeatType.values()[ repeat_int ];
      mBeginTime = new Real( in );

      BigDecimal dur = BigDecimal.ZERO;
      for ( Move m : mMoves )
         dur = dur.add( m.getDuration().toBigDecimal() );
      if ( mRepeatType == RepeatType.BOUNCE )
         mTotalDuration = dur.multiply( new BigDecimal( 2 ) );
      else
         mTotalDuration = dur;
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings( "unused" )
   private MoveSeq()
      {
      super();
      mMoves = null;
      mRepeatType = null;
      mBeginTime = null;
      mTotalDuration = null;
      }

   public MoveSeq( Move[] moves, RepeatType repeat_type,
                   Real begin_time )
      {
      super();
      if ( moves.length == 0 )
         throw new IllegalArgumentException( "A MoveSeq must contain"
                                             + " at least one Move" );
      mMoves         = moves.clone();
      mRepeatType    = repeat_type;
      mBeginTime     = begin_time;
      BigDecimal dur = BigDecimal.ZERO;
      for ( Move m : mMoves )
         dur = dur.add( m.getDuration().toBigDecimal() );
      if ( mRepeatType == RepeatType.BOUNCE )
         mTotalDuration = dur.multiply( new BigDecimal( 2 ) );
      else
         mTotalDuration = dur;
      }

   public MoveSeq( Move m )
      {
      super();
      mMoves         = new Move[] { m };
      mRepeatType    = RepeatType.LOOP;
      mBeginTime     = new Real( BigDecimal.ZERO );
      mTotalDuration = m.getDuration().toBigDecimal();
      }

   public int getClassNumber()
      {  return 18;  }

   @Override
   public boolean equals( Object o )
      {
      if ( o instanceof MoveSeq )
         {
         MoveSeq ms = (MoveSeq) o;
         return (   mRepeatType.equals( ms.mRepeatType )
                 && Arrays.equals( mMoves, ms.mMoves ));
         }
      return false;
      }

   @Override
   public int hashCode()
      {
      return (mMoves.length * 3
              + mRepeatType.ordinal())
             ^ Arrays.hashCode( mMoves );
      }

   public Move getMove( int n )
      {  return mMoves[ n ];  }
   public RepeatType getRepeatType()
      {  return mRepeatType;  }

   public Vector3f initialPosition()
      {
      return mMoves[ 0 ].initialPosition();
      }

   public Quaternion initialRotation()
      {
      return mMoves[ 0 ].initialRotation();
      }

   public Matrix4f initialTransform()
      {
      Matrix4f ret = new Matrix4f();
      ret.setTranslation( initialPosition() );
      ret.setRotationQuaternion( initialRotation() );
      return ret;
      }

   public Matrix4f transformAt( Real time )
      {
      return transformAt( time.toBigDecimal() );
      }

   public Matrix4f transformAt( BigDecimal time )
      {
      BigDecimal bd_time = time.subtract( mBeginTime.toBigDecimal() );

      // Transform time according to repeat rules.
      if ( mRepeatType == RepeatType.ONCE )
         {
         if ( bd_time.compareTo( BigDecimal.ZERO ) <= 0 )
            return initialTransform();
         if ( bd_time.compareTo( mTotalDuration ) >= 0 )
            bd_time = mTotalDuration;
         }
      else
         {
         bd_time = bd_time.remainder( mTotalDuration );
         if ( bd_time.compareTo( BigDecimal.ZERO ) < 0 )
            bd_time = bd_time.add( mTotalDuration );

         if ( mRepeatType == RepeatType.BOUNCE
              &&  bd_time.compareTo(
                        mTotalDuration.divide( new BigDecimal( 2 ) ) )
                  > 0 )
            bd_time = mTotalDuration.subtract( bd_time );
         }

      int move_idx = 0;
      while ( move_idx < mMoves.length - 1
              &&  bd_time.compareTo( mMoves[ move_idx ].getDuration()
                                                       .toBigDecimal() )
                  > 0 )
         {
         bd_time
            = bd_time.subtract( mMoves[ move_idx ].getDuration()
                                                  .toBigDecimal() );
         ++move_idx;
         }

      return mMoves[ move_idx ].transformAt( bd_time );
      }

   private void checkRepeatType( int r ) throws ClassNotFoundException
      {
      if ( r < 0  ||  r >= RepeatType.values().length )
         throw new ClassNotFoundException( "Repeat type " + mRepeatType
                                           + " out of range" );
      }

   public void writeExternal( OutputStream out ) throws IOException
      {
      if ( mMoves.length == 0 )
         throw new IOException( "Cannot write empty MoveSeq" );
      ULong.longAsExternal( out, mMoves.length );
      DvtpObject.writeArray( out, mMoves );
      ULong.longAsExternal( out, mRepeatType.ordinal() );
      mBeginTime.writeExternal( out );
      }

   public String prettyPrint()
      {
      return "(MoveSeq " + mMoves.length + " "
             + Util.prettyPrintList( mMoves, mRepeatType ) + ")";
      }

   private final Move[]     mMoves;
   private final RepeatType mRepeatType;
   private final Real       mBeginTime;
   private final BigDecimal mTotalDuration;
   }

package org.distroverse.dvtp;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.distroverse.core.Util;

import com.jme.math.Vector3f;

public class MoveSeq implements ProxySendable
   {
   public enum RepeatType { ONCE, LOOP, BOUNCE };
   
   public MoveSeq()
      {
      super();
      }
   
   public MoveSeq( Move[] moves, RepeatType repeat_type )
      {
      super();
      mNumMoves   = moves.length;
      mMoves      = moves;
      mRepeatType = repeat_type;
      }

   public int getClassNumber()
      {  return 18;  }
   
   public Move[] getMoves()
      {  return mMoves;  }
   public RepeatType getRepeatType()
      {  return mRepeatType;  }

   public Vector3f initialPosition()
      {
      // TODO Auto-generated method stub
      return null;
      }

   public void readExternal( ObjectInput in )
   throws IOException, ClassNotFoundException
      {
      mNumMoves = Util.safeInt( CompactUlong.externalAsLong( in ) );
      mMoves = DvtpObject.readArray( in, mNumMoves, Move.class );
      int repeat_int 
         = Util.safeInt( CompactUlong.externalAsLong( in ) );
      checkRepeatType( repeat_int );
      mRepeatType = RepeatType.values()[ repeat_int ];
      }

   private void checkRepeatType( int r ) throws ClassNotFoundException
      {
      if ( r < 0  ||  r >= RepeatType.values().length )
         throw new ClassNotFoundException( "Repeat type " + mRepeatType
                          + " out of range" );
      }

   public void writeExternal( ObjectOutput out ) throws IOException
      {
      CompactUlong.longAsExternal( out, mNumMoves );
      DvtpObject.writeArray( out, mMoves );
      CompactUlong.longAsExternal( out, mRepeatType.ordinal() );
      }

   private int mNumMoves;
   private Move[] mMoves;
   private RepeatType mRepeatType;
   }

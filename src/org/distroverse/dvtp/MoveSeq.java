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
      }

   public MoveSeq( Move[] moves, RepeatType repeat_type )
      {
      super();
      if ( moves.length == 0 )
         throw new IllegalArgumentException( "A MoveSeq must contain"
                                             + " at least one Move" );
      mMoves      = moves.clone();
      mRepeatType = repeat_type;
      }
   
   public MoveSeq( Move m )
      {
      super();
      mMoves      = new Move[] { m };
      mRepeatType = RepeatType.LOOP;
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
      }

   public String prettyPrint()
      {
      return "(MoveSeq " + mMoves.length + " "
             + Util.prettyPrintList( mMoves, mRepeatType ) + ")";
      }

   private final Move[] mMoves;
   private final RepeatType mRepeatType;
   }

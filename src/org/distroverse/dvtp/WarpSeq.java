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

/**
 * @author dreish
 */
public final class WarpSeq implements DvtpExternalizable
   {
   public enum RepeatType { ONCE, LOOP, BOUNCE };

   /**
    *
    */
   public WarpSeq( InputStream in )
   throws IOException, ClassNotFoundException
      {
      super();
      readExternal( in );
      }

   public WarpSeq()
      {
      super();
      mWarps      = null;
      mRepeatType = RepeatType.ONCE;
      }

   public WarpSeq( Warp[] warps, RepeatType repeat_type )
      {
      super();
      mWarps      = warps;
      mRepeatType = repeat_type;
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   public int getClassNumber()
      {  return 35;  }

   @Override
   public boolean equals( Object o )
      {
      if ( o instanceof WarpSeq )
         {
         WarpSeq ws = (WarpSeq) o;
         return (   mRepeatType.equals( ws.mRepeatType )
                 && Arrays.equals( mWarps, ws.mWarps ));
         }
      return false;
      }

   @Override
   public int hashCode()
      {
      return (mWarps.length * 3
              + mRepeatType.ordinal())
             ^ Arrays.hashCode( mWarps );
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#prettyPrint()
    */
   public String prettyPrint()
      {
      return "(WarpSeq " + mWarps.length + " "
             + Util.prettyPrintList( mWarps, mRepeatType ) + ")";
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#readExternal(java.io.InputStream)
    */
   private void readExternal( InputStream in ) throws IOException,
                                             ClassNotFoundException
      {
      int num_warps = Util.safeInt( ULong.externalAsLong( in ) );
      mWarps = DvtpObject.readArray( in, num_warps, Warp.class, 34 );
      int repeat_int
         = Util.safeInt( ULong.externalAsLong( in ) );
      checkRepeatType( repeat_int );
      mRepeatType = RepeatType.values()[ repeat_int ];
      }

   private void checkRepeatType( int r ) throws ClassNotFoundException
      {
      if ( r < 0  ||  r >= RepeatType.values().length )
         throw new ClassNotFoundException( "Repeat type " + mRepeatType
                                           + " out of range" );
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#writeExternal(java.io.OutputStream)
    */
   public void writeExternal( OutputStream out ) throws IOException
      {
      ULong.longAsExternal( out, mWarps.length );
      DvtpObject.writeArray( out, mWarps );
      ULong.longAsExternal( out, mRepeatType.ordinal() );
      }

   private Warp[] mWarps;
   private RepeatType mRepeatType;
   }

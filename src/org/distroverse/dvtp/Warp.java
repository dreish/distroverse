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

//immutable

/**
 * @author dreish
 *
 */
public class Warp implements DvtpExternalizable
   {
   /**
    *
    */
   public Warp( InputStream in )
   throws IOException, ClassNotFoundException
      {
      super();
      mDegree = Util.safeInt( ULong.externalAsLong( in ) );
      mPolyWarps
         = DvtpObject.readArray( in, mDegree, PointArray.class, 4 );
      mSins = Util.safeInt( ULong.externalAsLong( in ) );
      mSinWarps
         = DvtpObject.readArray( in, mSins, PointArray.class, 4 );
      mSinPeriods
         = DvtpObject.readArray( in, mSins, Flo.class, 15 );
      mSinOffsets
         = DvtpObject.readArray( in, mSins, Flo.class, 15 );
      
      mDuration = new Flo( in );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings( "unused" )
   private Warp()
      {
      super();
      mDegree     = 0;
      mPolyWarps  = null;
      mSins       = 0;
      mSinWarps   = null;
      mSinPeriods = null;
      mSinOffsets = null;
      mDuration   = null;
      }

   public Warp( PointArray[] pw )
      {
      super();
      mDegree     = pw.length;
      mPolyWarps  = pw.clone();
      mSins       = 0;
      mSinWarps   = new PointArray[ 0 ];
      mSinPeriods = new Flo[ 0 ];
      mSinOffsets = new Flo[ 0 ];
      mDuration   = new Flo( -1 );
      }
   
   // XXX Needs a full constructor

   // XXX Needs accessors

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   public int getClassNumber()
      {  return 34;  }

   @Override
   public boolean equals( Object o )
      {
      if ( o instanceof Warp )
         {
         Warp w = (Warp) o;
         return w.mDegree == mDegree
                &&  w.mSins == mSins
                &&  w.mDuration.equals( mDuration )
                &&  Arrays.equals( w.mPolyWarps, mPolyWarps )
                &&  Arrays.equals( w.mSinOffsets, mSinOffsets )
                &&  Arrays.equals( w.mSinPeriods, mSinPeriods )
                &&  Arrays.equals( w.mSinWarps, mSinWarps );
         }
      return false;
      }

   @Override
   public int hashCode()
      {
      return mDegree
             ^ (mSins * 65536)
             ^ mDuration.hashCode()
             ^ Arrays.hashCode( mPolyWarps )
             ^ Arrays.hashCode( mSinOffsets )
             ^ Arrays.hashCode( mSinPeriods )
             ^ Arrays.hashCode( mSinWarps );
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#prettyPrint()
    */
   public String prettyPrint()
      {
      return "(Warp "
             + Util.prettyPrintList( mDegree, mPolyWarps, mSins,
                                mSinWarps, mSinPeriods, mSinOffsets,
                                mDuration )
             + ")";
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#writeExternal(java.io.OutputStream)
    */
   public void writeExternal( OutputStream out ) throws IOException
      {
      ULong.longAsExternal( out, mDegree );
      DvtpObject.writeArray( out, mPolyWarps );
      ULong.longAsExternal( out, mSins );
      DvtpObject.writeArray( out, mSinWarps );
      DvtpObject.writeArray( out, mSinPeriods );
      DvtpObject.writeArray( out, mSinOffsets );

      mDuration.writeExternal( out );
      }

   private final int mDegree;
   private final PointArray[] mPolyWarps;
   private final int mSins;
   private final PointArray[] mSinWarps;
   private final Flo[] mSinPeriods;
   private final Flo[] mSinOffsets;

   private final Flo mDuration;
   }

/**
 *
 */
package org.distroverse.dvtp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.distroverse.core.Util;

/**
 * @author dreish
 *
 */
public class Warp implements DvtpExternalizable
   {
   /**
    *
    */
   public Warp()
      {
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
      mDegree     = pw.length;
      mPolyWarps  = pw;
      mSins       = 0;
      mSinWarps   = new PointArray[ 0 ];
      mSinPeriods = new Flo[ 0 ];
      mSinOffsets = new Flo[ 0 ];
      mDuration   = new Flo( -1 );
      }

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
    * @see org.distroverse.dvtp.DvtpExternalizable#readExternal(java.io.InputStream)
    */
   public void readExternal( InputStream in ) throws IOException,
                                             ClassNotFoundException
      {
      mDegree = Util.safeInt( ULong.externalAsLong( in ) );
      mPolyWarps
         = DvtpObject.readArray( in, mDegree, PointArray.class );
      mSins = Util.safeInt( ULong.externalAsLong( in ) );
      mSinWarps
         = DvtpObject.readArray( in, mSins, PointArray.class );
      mSinPeriods
         = DvtpObject.readArray( in, mSins, Flo.class );
      mSinOffsets
         = DvtpObject.readArray( in, mSins, Flo.class );

      (mDuration = new Flo()).readExternal( in );
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

   private int mDegree;
   private PointArray[] mPolyWarps;
   private int mSins;
   private PointArray[] mSinWarps;
   private Flo[] mSinPeriods;
   private Flo[] mSinOffsets;

   private Flo mDuration;
   }

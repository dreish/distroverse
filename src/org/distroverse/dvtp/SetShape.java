/**
 *
 */
package org.distroverse.dvtp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.distroverse.core.Util;

/**
 * @author dreish
 *
 */
public final class SetShape implements ProxySendable
   {
   /**
    *
    */
   public SetShape()
      {
      mId      = 0;
      mShape   = null;
      mWarpSeq = null;
      }

   public SetShape( long id, Shape shape, WarpSeq warp_seq )
      {
      mId      = id;
      mShape   = shape;
      mWarpSeq = warp_seq;
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   public int getClassNumber()
      {  return 136;  }

   @Override
   public boolean equals( Object o )
      {
      if ( o instanceof SetShape )
         {
         SetShape ss = (SetShape) o;
         return ss.mId == mId
                &&  ss.mShape.equals( mShape )
                &&  ss.mWarpSeq.equals( mWarpSeq );
         }
      return false;
      }

   @Override
   public int hashCode()
      {
      return ((int) mId)
             ^ mShape.hashCode()
             ^ mWarpSeq.hashCode();
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#prettyPrint()
    */
   public String prettyPrint()
      {
      return "(SetShape " + mId + " "
             + Util.prettyPrintList( mShape, mWarpSeq ) + ")";
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#readExternal(java.io.InputStream)
    */
   public void readExternal( InputStream in ) throws IOException,
                                             ClassNotFoundException
      {
      mId = ULong.externalAsLong( in );
      (mShape = new Shape()).readExternal( in );
      (mWarpSeq = new WarpSeq()).readExternal( in );
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#writeExternal(java.io.OutputStream)
    */
   public void writeExternal( OutputStream out ) throws IOException
      {
      ULong.longAsExternal( out, mId );
      mShape.writeExternal( out );
      mWarpSeq.writeExternal( out );
      }

   private long    mId;
   private Shape   mShape;
   private WarpSeq mWarpSeq;
   }

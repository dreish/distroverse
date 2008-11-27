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

import org.distroverse.core.Util;

/**
 * @author dreish
 *
 */
public final class WarpObject implements ProxySendable
   {
   /**
    *
    */
   public WarpObject()
      {
      super();
      mId      = 0;
      mWarpSeq = null;
      }

   public WarpObject( long id, WarpSeq ws )
      {
      super();
      mId      = id;
      mWarpSeq = ws;
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   public int getClassNumber()
      {  return 137;  }

   @Override
   public boolean equals( Object o )
      {
      if ( o instanceof WarpObject )
         {
         return ((WarpObject) o).mId == mId
                &&  ((WarpObject) o).mWarpSeq.equals( mWarpSeq );
         }
      return false;
      }

   @Override
   public int hashCode()
      {
      return ((int) mId)
             ^ mWarpSeq.hashCode();
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#prettyPrint()
    */
   public String prettyPrint()
      {
      return "(WarpObject " + mId + " "
             + Util.prettyPrintList( mWarpSeq ) + ")";
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#readExternal(java.io.InputStream)
    */
   public void readExternal( InputStream in ) throws IOException,
                                             ClassNotFoundException
      {
      mId = ULong.externalAsLong( in );
      (mWarpSeq = new WarpSeq()).readExternal( in );
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#writeExternal(java.io.OutputStream)
    */
   public void writeExternal( OutputStream out ) throws IOException
      {
      ULong.longAsExternal( out, mId );
      mWarpSeq.writeExternal( out );
      }

   private long mId;
   private WarpSeq mWarpSeq;
   }

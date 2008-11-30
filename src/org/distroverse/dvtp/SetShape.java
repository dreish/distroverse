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

//immutable

/**
 * @author dreish
 *
 */
public final class SetShape implements ProxySendable
   {
   /**
    *
    */
   public SetShape( InputStream in )
   throws IOException, ClassNotFoundException
      {
      super();
      readExternal( in );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings( "unused" )
   private SetShape()
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
   private void readExternal( InputStream in ) throws IOException,
                                             ClassNotFoundException
      {
      mId = ULong.externalAsLong( in );
      mShape = new Shape( in );
      mWarpSeq = new WarpSeq( in );
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

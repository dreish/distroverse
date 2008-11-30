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

//immutable

/**
 * @author dreish
 *
 */
public class ClearShape implements ProxySendable
   {
   /**
    *
    */
   public ClearShape( InputStream in ) throws IOException
      {
      super();
      readExternal( in );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings( "unused" )
   private ClearShape()
      {
      mId = 0;
      }

   /**
    *
    */
   public ClearShape( long id )
      {
      mId = id;
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   public int getClassNumber()
      {  return 139;  }

   @Override
   public boolean equals( Object o )
      {
      return (o instanceof ClearShape
              &&  ((ClearShape) o).mId == mId);
      }

   @Override
   public int hashCode()
      {
      return ((Long) mId).hashCode() ^ ClearShape.class.hashCode();
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#prettyPrint()
    */
   public String prettyPrint()
      {
      return "(ClearShape " + mId + ")";
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#readExternal(java.io.InputStream)
    */
   private void readExternal( InputStream in ) throws IOException
      {
      mId = ULong.externalAsLong( in );
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#writeExternal(java.io.OutputStream)
    */
   public void writeExternal( OutputStream out ) throws IOException
      {
      ULong.longAsExternal( out, mId );
      }

   private long mId;
   }

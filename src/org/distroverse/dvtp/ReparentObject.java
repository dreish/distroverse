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
public final class ReparentObject implements ProxySendable
   {

   /**
    *
    */
   public ReparentObject( InputStream in ) throws IOException
      {
      super();
      readExternal( in );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings( "unused" )
   private ReparentObject()
      {
      mId = mParentId = 0;
      }

   public ReparentObject( long id, long parent_id )
      {
      mId       = id;
      mParentId = parent_id;
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   public int getClassNumber()
      {  return 138;  }

   public long getId()        {  return mId;        }
   public long getParentId()  {  return mParentId;  }

   @Override
   public boolean equals( Object o )
      {
      if ( o instanceof ReparentObject )
         {
         ReparentObject ro = (ReparentObject) o;
         return (mId == ro.mId
                 &&  mParentId == ro.mParentId);
         }
      return false;
      }

   @Override
   public int hashCode()
      {
      return (int) mId ^ ((int) mParentId * 924535727);
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#prettyPrint()
    */
   public String prettyPrint()
      {
      return "(ReparentObject " + mId + " " + mParentId + ")";
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#readExternal(java.io.InputStream)
    */
   private void readExternal( InputStream in ) throws IOException
      {
      mId       = ULong.externalAsLong( in );
      mParentId = ULong.externalAsLong( in );
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#writeExternal(java.io.OutputStream)
    */
   public void writeExternal( OutputStream out ) throws IOException
      {
      ULong.longAsExternal( out, mId );
      ULong.longAsExternal( out, mParentId );
      }

   private long mId;
   private long mParentId;
   }

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
 * Turns visibility for an object (and its subobjects) on or off.
 * @author dreish
 */
public class SetVisible implements EnvoySendable
   {
   /**
    *
    */
   public SetVisible( InputStream in )
   throws IOException, ClassNotFoundException
      {
      super();
      mId   = ULong.externalAsLong( in );
      mFlag = Bool.externalAsBoolean( in );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings( "unused" )
   private SetVisible()
      {
      mId   = 0;
      mFlag = false;
      }

   public SetVisible( long id, boolean flag )
      {
      mId   = id;
      mFlag = flag;
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   public int getClassNumber()
      {  return 132;  }

   public long getId()
      {  return mId;  }
   public boolean getFlag()
      {  return mFlag;  }

   @Override
   public boolean equals( Object o )
      {
      return (o instanceof SetVisible
              &&  ((SetVisible) o).mId   == mId
              &&  ((SetVisible) o).mFlag == mFlag);
      }

   @Override
   public int hashCode()
      {
      return ((Long) mId).hashCode()
             ^ (mFlag ? True.class.hashCode() : False.class.hashCode())
             ^ SetVisible.class.hashCode();
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#prettyPrint()
    */
   public String prettyPrint()
      {
      return "(SetVisible " + mId + " " + mFlag + ")";
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#writeExternal(java.io.OutputStream)
    */
   public void writeExternal( OutputStream out ) throws IOException
      {
      ULong.longAsExternal( out, mId );
      Bool.booleanAsExternal( out, mFlag );
      }

   private final long    mId;
   private final boolean mFlag;
   }

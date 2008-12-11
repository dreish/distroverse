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
public final class DNodeRef implements DvtpExternalizable
   {

   /**
    *
    */
   public DNodeRef( InputStream in ) throws IOException
      {
      super();
      mRemoteHost = Str.externalAsString( in );
      mId = ULong.externalAsLong( in );
      mLastChangeTime = new Real( in );
      mDNode = null;
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings( "unused" )
   private DNodeRef()
      {
      super();
      mRemoteHost = null;
      mId = 0;
      mLastChangeTime = null;
      mDNode = null;
      }

   /**
    *
    * @param host
    * @param id
    * @param lct
    * @param n - not externalized, so this can be null
    */
   public DNodeRef( String host, long id, Real lct, DNode n )
      {
      super();
      mRemoteHost = host;
      mId = id;
      mLastChangeTime = lct;
      mDNode = n;
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   public int getClassNumber()
      {  return 29;  }

   public DNode getNode()
      {  return mDNode;  }
   
   public DNodeRef assocNode( DNode n )
      {
      return new DNodeRef( mRemoteHost, mId, mLastChangeTime, n );
      }

   public DNodeRef assocLCT( Real lct )
      {
      return new DNodeRef( mRemoteHost, mId, lct, mDNode );
      }

   /**
    * Two DNodeRefs are equal if they refer to the same DNode,
    * regardless of whether or how their DNodes are populated, or any
    * difference in their last updated times.
    * @param o - Object to compare to
    */
   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals( Object o )
      {
      if ( o instanceof DNodeRef )
         {
         DNodeRef dnr = (DNodeRef) o;
         return (dnr.mId == mId
                 &&  dnr.mRemoteHost.equals( mRemoteHost ));
         }
      return false;
      }

   @Override
   public int hashCode()
      {
      return mRemoteHost.hashCode()
             ^ (int) mId;
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#prettyPrint()
    */
   public String prettyPrint()
      {
      return "(DNodeRef " + mRemoteHost + " " + mId + " "
             + Util.prettyPrintList( mLastChangeTime, mDNode ) + ")";
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#writeExternal(java.io.OutputStream)
    */
   public void writeExternal( OutputStream out ) throws IOException
      {
      Str.stringAsExternal( out, mRemoteHost );
      ULong.longAsExternal( out, mId );
      mLastChangeTime.writeExternal( out );
      }

   private final String mRemoteHost;
   private final long   mId;
   private final Real   mLastChangeTime;
   private final DNode  mDNode;                   // Not externalized
   }

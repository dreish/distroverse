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
public final class DNodeRef implements DvtpExternalizable
   {

   /**
    *
    */
   public DNodeRef()
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
    */
   public DNodeRef( String host, long id, Real lct )
      {
      super();
      mRemoteHost = host;
      mId = id;
      mLastChangeTime = lct;
      mDNode = null;
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   public int getClassNumber()
      {  return 29;  }

   public DNode getNode()
      {  return mDNode;  }
   public DNodeRef setNode( DNode n )
      {
      mDNode = n;
      return this;
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
    * @see org.distroverse.dvtp.DvtpExternalizable#readExternal(java.io.InputStream)
    */
   public void readExternal( InputStream in ) throws IOException
      {
      mRemoteHost = Str.externalAsString( in );
      mId = ULong.externalAsLong( in );
      (mLastChangeTime = new Real()).readExternal( in );
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

   private String mRemoteHost;
   private long   mId;
   private Real   mLastChangeTime;
   private DNode  mDNode;                   // Not externalized
   }

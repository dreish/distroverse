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
 * Adds an object, with initial movement sequence.  If the MoveSeq is
 * empty, the object is not yet visible -- it will become visible when
 * it is first given a position with a nonempty MoveObject.
 * @author dreish
 */
public final class AddObject implements ProxySendable
   {
   public AddObject()
      {
      super();
      }
   
   public AddObject( Shape s, CompactUlong id, CompactUlong pid,
                     MoveSeq m )
      {
      super();
      mHasShape = true;
      mShape = s;
      mId = id;
      mParentId = pid;
      mMoveSeq = m;
      }

   public AddObject( CompactUlong id, CompactUlong pid, MoveSeq m )
      {
      super();
      mHasShape = false;
      mShape = null;
      mId = id;
      mParentId = pid;
      mMoveSeq = m;
      }

   public int getClassNumber()
      {  return 12;  }
   
   public Shape        getShape()     {  return mShape;     }
   public CompactUlong getId()        {  return mId;        }
   public CompactUlong getParentId()  {  return mParentId;  }
   public MoveSeq      getMoveSeq()   {  return mMoveSeq;   }

   public void readExternal( InputStream in )
   throws IOException, ClassNotFoundException
      {
      mHasShape = Bool.externalAsBoolean( in );
      if ( mHasShape )
         (mShape = new Shape()).readExternal( in );
      else
         mShape = null;
      (mId = new CompactUlong()).readExternal( in );
      (mParentId = new CompactUlong()).readExternal( in );
      (mMoveSeq = new MoveSeq()).readExternal( in );
      }

   public void writeExternal( OutputStream out ) throws IOException
      {
      Bool.booleanAsExternal( out, mHasShape );
      if ( mHasShape )
         mShape.writeExternal( out );
      mId.writeExternal( out );
      mParentId.writeExternal( out );
      mMoveSeq.writeExternal( out );
      }
   
   public String prettyPrint()
      {
      return "(AddObject "
             + Util.prettyPrintList( mHasShape, mShape, mId, mParentId,
                                     mMoveSeq ) + ")";
      }

   private boolean      mHasShape;
   private Shape        mShape;
   private CompactUlong mId;
   private CompactUlong mParentId;
   private MoveSeq      mMoveSeq;
   }

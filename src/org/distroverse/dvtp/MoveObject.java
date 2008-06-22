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
 * Takes an object ID and a MoveSeq, and moves it.
 * @author dreish
 */
public final class MoveObject implements ProxySendable
   {
   public MoveObject()
      {
      super();
      }
   
   public MoveObject( long id, MoveSeq m )
      {
      mId = id;
      mMoveSeq = m;
      }
   
   public int getClassNumber()
      {  return 14;  }
   
   public long    getId()       {  return mId;       }
   public MoveSeq getMoveSeq()  {  return mMoveSeq;  }

   public void readExternal( InputStream in )
   throws IOException, ClassNotFoundException
      {
      mId = CompactUlong.externalAsLong( in );
      (mMoveSeq = new MoveSeq()).readExternal( in );
      }

   public void writeExternal( OutputStream out ) throws IOException
      {
      CompactUlong.longAsExternal( out, mId );
      mMoveSeq.writeExternal( out );
      }
   
   public String prettyPrint()
      {
      return "(MoveObject " 
             + Util.prettyPrintList( mId, mMoveSeq ) + ")";
      }

   private long mId;
   private MoveSeq mMoveSeq;
   }

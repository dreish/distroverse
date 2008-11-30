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
   public MoveObject( InputStream in )
   throws IOException, ClassNotFoundException
      {
      super();
      readExternal( in );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   private MoveObject()
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

   @Override
   public boolean equals( Object o )
      {
      if ( o instanceof MoveObject )
         {
         MoveObject mo = (MoveObject) o;
         return (mId == mo.mId
                 && mMoveSeq.equals( mo.mMoveSeq ));
         }
      return false;
      }

   @Override
   public int hashCode()
      {
      return (int) mId
             ^ mMoveSeq.hashCode();
      }

   public long    getId()       {  return mId;       }
   public MoveSeq getMoveSeq()  {  return mMoveSeq;  }

   private void readExternal( InputStream in )
   throws IOException, ClassNotFoundException
      {
      mId = ULong.externalAsLong( in );
      mMoveSeq = new MoveSeq( in );
      }

   public void writeExternal( OutputStream out ) throws IOException
      {
      ULong.longAsExternal( out, mId );
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

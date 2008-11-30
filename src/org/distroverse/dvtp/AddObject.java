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
 * Adds an object, with initial movement sequence and shape warp
 * sequence.  The latter may be empty, the former not.
 * @author dreish
 */
public final class AddObject implements ProxySendable
   {
   public AddObject( InputStream in )
   throws IOException, ClassNotFoundException
      {
      super();
      readExternal( in );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings("unused")
   private AddObject()
      {
      super();
      }

   public AddObject( boolean v, Shape s, ULong id, ULong pid,
                     MoveSeq m, WarpSeq w )
      {
      super();
      mHasShape = true;
      mVisible = v;
      mShape = s;
      mId = id;
      mParentId = pid;
      mMoveSeq = m;
      mWarpSeq = w;
      }

   public AddObject( ULong id, ULong pid, MoveSeq m )
      {
      super();
      mHasShape = false;
      mVisible = false;
      mShape = null;
      mId = id;
      mParentId = pid;
      mMoveSeq = m;
      mWarpSeq = null;
      }

   public AddObject( InputStream in, boolean b )
   throws IOException, ClassNotFoundException
      {
      super();
      readWithoutId( in );
      }

   public int getClassNumber()
      {  return 12;  }

   @Override
   public boolean equals( Object o )
      {
      if ( o instanceof AddObject )
         {
         AddObject ao = (AddObject) o;
         if ( mHasShape )
            return (ao.mHasShape
                    && mVisible == ao.mVisible
                    && mShape.equals( ao.mShape )
                    && mId.equals( ao.mId )
                    && mParentId.equals( ao.mParentId )
                    && mMoveSeq.equals( ao.mMoveSeq )
                    && mWarpSeq.equals( ao.mWarpSeq ));
         return ((! ao.mHasShape)
                 && mId.equals( ao.mId )
                 && mParentId.equals( ao.mParentId )
                 && mMoveSeq.equals( ao.mMoveSeq ));
         }
      return false;
      }

   @Override
   public int hashCode()
      {
      return (mHasShape ? mShape.hashCode()
                          ^ mWarpSeq.hashCode()
                          + (mVisible ? 702113218 : 0)
                        : 0)
             ^ mId.hashCode()
             ^ mParentId.hashCode()
             ^ mMoveSeq.hashCode();
      }

   public boolean equalsWithoutId( Object o )
      {
      if ( o instanceof AddObject )
         {
         AddObject ao = (AddObject) o;
         if ( mHasShape )
            return (ao.mHasShape
                    && mVisible == ao.mVisible
                    && mShape.equals( ao.mShape )
                    && mMoveSeq.equals( ao.mMoveSeq )
                    && mWarpSeq.equals( ao.mWarpSeq ));
         return ((! ao.mHasShape)
                 && mMoveSeq.equals( ao.mMoveSeq ));
         }
      return false;
      }

   public int hashCodeWithoutId()
      {
      return (mHasShape ? mShape.hashCode()
                        ^ mWarpSeq.hashCode()
                        + (mVisible ? 702113218 : 0)
                        : 0)
                        ^ mMoveSeq.hashCode();
      }

   public boolean getHasShape()  {  return mHasShape;  }
   public boolean getVisible()   {  return mVisible;   }
   public Shape   getShape()     {  return mShape;     }
   public ULong   getId()        {  return mId;        }
   public ULong   getParentId()  {  return mParentId;  }
   public MoveSeq getMoveSeq()   {  return mMoveSeq;   }
   public WarpSeq getWarpSeq()   {  return mWarpSeq;   }

   private void readExternal( InputStream in )
   throws IOException, ClassNotFoundException
      {
      mHasShape = Bool.externalAsBoolean( in );
      if ( mHasShape )
         {
         mVisible = Bool.externalAsBoolean( in );
         mShape = new Shape( in );
         mWarpSeq = new WarpSeq( in );
         }
      else
         {
         mVisible = false;
         mShape = null;
         mWarpSeq = null;
         }
      mId = new ULong( in );
      mParentId = new ULong( in );
      mMoveSeq = new MoveSeq( in );
      }

   public void writeExternal( OutputStream out ) throws IOException
      {
      Bool.booleanAsExternal( out, mHasShape );
      if ( mHasShape )
         {
         Bool.booleanAsExternal( out, mVisible );
         mShape.writeExternal( out );
         mWarpSeq.writeExternal( out );
         }
      mId.writeExternal( out );
      mParentId.writeExternal( out );
      mMoveSeq.writeExternal( out );
      }

   private void readWithoutId( InputStream in )
   throws IOException, ClassNotFoundException
      {
      mHasShape = Bool.externalAsBoolean( in );
      if ( mHasShape )
         {
         mVisible = Bool.externalAsBoolean( in );
         mShape = new Shape( in );
         mWarpSeq = new WarpSeq( in );
         }
      else
         {
         mVisible = false;
         mShape = null;
         mWarpSeq = null;
         }
      mId       = new ULong( 0 );
      mParentId = new ULong( 0 );
      mMoveSeq = new MoveSeq( in );
      }

   public void writeWithoutId( OutputStream out ) throws IOException
      {
      Bool.booleanAsExternal( out, mHasShape );
      if ( mHasShape )
         {
         Bool.booleanAsExternal( out, mVisible );
         mShape.writeExternal( out );
         mWarpSeq.writeExternal( out );
         }
      mMoveSeq.writeExternal( out );
      }

   public String prettyPrint()
      {
      return "(AddObject "
             + Util.prettyPrintList( mHasShape, mShape, mId, mParentId,
                                     mMoveSeq ) + ")";
      }

   private boolean mHasShape;
   private boolean mVisible;
   private Shape   mShape;
   private ULong   mId;
   private ULong   mParentId;
   private MoveSeq mMoveSeq;
   private WarpSeq mWarpSeq;
   }

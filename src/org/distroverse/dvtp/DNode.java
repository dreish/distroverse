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
import java.util.Arrays;

import org.distroverse.core.Util;

//immutable

/**
 * @author dreish
 *
 */
public final class DNode implements DvtpExternalizable
   {
   /**
    *
    */
   public DNode( InputStream in )
   throws IOException, ClassNotFoundException
      {
      super();
      mBeing = new AddObject( in, false );
      mRadius = Flo.externalAsFloat( in );
      mThis = new DNodeRef( in );
      boolean has_parent = Bool.externalAsBoolean( in );
      if ( has_parent )
         mParent = new DNodeRef( in );
      else
         mParent = null;
      int num_children = Util.safeInt( ULong.externalAsLong( in ) );
      mChildren = DvtpObject.readArray( in, num_children,
                                        DNodeRef.class, 29 );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings( "unused" )
   private DNode()
      {
      mBeing = null;
      mRadius = 0;
      mThis = null;
      mParent = null;
      mChildren = null;
      }

   public DNode( AddObject b, float r, DNodeRef t, DNodeRef p,
                 DNodeRef[] c )
      {
      mBeing = b;
      mRadius = r;
      mThis = t;
      mParent = p;
      mChildren = c.clone();
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   public int getClassNumber()
      {  return 30;  }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#prettyPrint()
    */
   public String prettyPrint()
      {
      return "(DNode "
             + Util.prettyPrintList( mBeing, mRadius, mThis, mParent,
                                     mChildren )
             + ")";
      }

   @Override
   public boolean equals( Object o )
      {
      if ( o instanceof DNode )
         {
         DNode dn = (DNode) o;
         return (dn.mRadius == mRadius
                 &&  dn.mBeing.equalsWithoutId( mBeing )
                 &&  dn.mThis.equals( mThis )
                 &&  ((mParent == null && dn.mParent == null)
                      ||  dn.mParent.equals( mParent ))
                 &&  Arrays.equals( dn.mChildren, mChildren ));
         }
      return false;
      }

   @Override
   public int hashCode()
      {
      return ((Float) mRadius).hashCode()
             ^ mBeing.hashCodeWithoutId()
             ^ mThis.hashCode()
             ^ (mParent == null ? 0 : mParent.hashCode())
             ^ Arrays.hashCode( mChildren );
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#writeExternal(java.io.OutputStream)
    */
   public void writeExternal( OutputStream out ) throws IOException
      {
      mBeing.writeWithoutId( out );
      Flo.floatAsExternal( out, mRadius );
      mThis.writeExternal( out );
      if ( mParent == null )
         Bool.booleanAsExternal( out, false );   // has_parent
      else
         {
         Bool.booleanAsExternal( out, true );    // has_parent
         mParent.writeExternal( out );
         }
      ULong.longAsExternal( out, mChildren.length );
      DvtpObject.writeArray( out, mChildren );
      }

   private final AddObject  mBeing;
   private final float      mRadius;
   private final DNodeRef   mThis;
   private final DNodeRef   mParent;
   private final DNodeRef[] mChildren;
   }

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
      mGeneratedChildren = Bool.externalAsBoolean( in );
      if ( mGeneratedChildren )
         {
         mChildren = null;
         mGenerator = Str.externalAsString( in );
         int num_args = Util.safeInt( ULong.externalAsLong( in ) );
         mGeneratorArgs = new DvtpExternalizable[ num_args ];
         for ( int i = 0; i < num_args; ++i )
            mGeneratorArgs[ i ] = DvtpObject.parseObject( in );
         }
      else
         {
         int num_children = Util.safeInt( ULong.externalAsLong( in ) );
         mChildren = DvtpObject.readArray( in, num_children,
                                           DNodeRef.class, 29 );
         mGenerator = null;
         mGeneratorArgs = null;
         }
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
      mGeneratedChildren = false;
      mGenerator = null;
      mGeneratorArgs = null;
      }

   public DNode( AddObject b, float r, DNodeRef t, DNodeRef p,
                 DNodeRef[] c )
      {
      mBeing = b;
      mRadius = r;
      mThis = t;
      mParent = p;
      mChildren = c.clone();
      mGeneratedChildren = false;
      mGenerator = null;
      mGeneratorArgs = null;
      }

   public DNode( AddObject b, float r, DNodeRef t, DNodeRef p,
                 String g, DvtpExternalizable[] g_args )
      {
      mBeing = b;
      mRadius = r;
      mThis = t;
      mParent = p;
      mChildren = null;
      mGeneratedChildren = true;
      mGenerator = g;
      mGeneratorArgs = g_args;
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   public int getClassNumber()
      {  return 30;  }

   public int getNumChildren()
      {  return mChildren.length;  }

   public DNodeRef getChild( int n )
      {  return mChildren[ n ];  }

   public DNodeRef getThisRef()
      {  return mThis;  }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#prettyPrint()
    */
   public String prettyPrint()
      {
      if ( mGeneratedChildren )
         return "(DNode. "
                + Util.prettyPrintList( mBeing, mRadius, mThis, mParent,
                                        mGenerator, mGeneratorArgs )
                + ")";

      return "(DNode. "
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
                 &&  mGeneratedChildren == dn.mGeneratedChildren
                 &&  ((mGenerator == null && dn.mGenerator == null)
                      || (mGenerator != null
                          && mGenerator.equals( dn.mGenerator )))
                 &&  Arrays.equals( dn.mChildren, mChildren ))
                 &&  Arrays.equals( dn.mGeneratorArgs, mGeneratorArgs );
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
             ^ Arrays.hashCode( mChildren )
             ^ (mGenerator == null ? 0 : mGenerator.hashCode())
             ^ Arrays.hashCode( mGeneratorArgs );
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

      Bool.booleanAsExternal( out, mGeneratedChildren );
      if ( mGeneratedChildren )
         {
         Str.stringAsExternal( out, mGenerator );
         ULong.longAsExternal( out, mGeneratorArgs.length );
         for ( DvtpExternalizable arg : mGeneratorArgs )
            DvtpObject.writeInnerObject( out, arg );
         }
      else
         {
         ULong.longAsExternal( out, mChildren.length );
         DvtpObject.writeArray( out, mChildren );
         }
      }

   private final AddObject            mBeing;
   private final float                mRadius;
   private final DNodeRef             mThis;
   private final DNodeRef             mParent;
   private final boolean              mGeneratedChildren;
   private final DNodeRef[]           mChildren;
   private final String               mGenerator;
   private final DvtpExternalizable[] mGeneratorArgs;
   }

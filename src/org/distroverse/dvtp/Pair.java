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
 * A pair of DvtpExternalizable objects.  They get externalized
 * together.  No provision is made for skipping a pair without parsing
 * both objects.
 * @author dreish
 */
public final class Pair implements DvtpExternalizable
   {
   /**
    * This default constructor is pretty much only useful in conjunction
    * with readExternal().
    */
   public Pair( InputStream in )
   throws IOException, ClassNotFoundException
      {
      super();
      mFirst  = DvtpObject.parseObject( in );
      mSecond = DvtpObject.parseObject( in );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings( "unused" )
   private Pair()
      {  mFirst = mSecond = null;  }

   public Pair( DvtpExternalizable first,
                DvtpExternalizable second )
      {
      mFirst = first;
      mSecond = second;
      }

   public Object getFirst()
      {  return mFirst;  }
   public Object getSecond()
      {  return mSecond;  }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   public int getClassNumber()
      {  return 1;  }

   @Override
   public boolean equals( Object o )
      {
      if ( o instanceof Pair )
         {
         Pair p = (Pair) o;
         return p.mFirst.equals( mFirst )
                &&  p.mSecond.equals( mSecond );
         }
      return false;
      }

   @Override
   public int hashCode()
      {
      return mFirst.hashCode()
           ^ mSecond.hashCode();
      }

   /* (non-Javadoc)
    * @see java.io.Externalizable#writeExternal(java.io.OutputStream)
    */
   public void writeExternal( OutputStream out ) throws IOException
      {
      DvtpObject.writeInnerObject( out, mFirst );
      DvtpObject.writeInnerObject( out, mSecond );
      }

   public String prettyPrint()
      {
      return "(Pair "
             + Util.prettyPrintList( mFirst, mSecond ) + ")";
      }

   private final DvtpExternalizable mFirst;
   private final DvtpExternalizable mSecond;
   }

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
public class Real implements DvtpExternalizable
   {
   /**
    *
    */
   public Real( InputStream in ) throws IOException
      {
      super();
      mIntegerPart = DLong.externalAsLong( in );
      mFractionPart = new Frac( in );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings( "unused" )
   private Real()
      {
      mIntegerPart  = 0;
      mFractionPart = null;
      }

   public Real( long integer_part, Frac fraction_part )
      {
      mIntegerPart  = integer_part;
      mFractionPart = fraction_part;
      }

   /**
    * @param x
    * @param precision
    */
   public Real( double x, int denomionator_bits )
      {
      mIntegerPart  = (long) Math.floor( x );
      double fraction_part = x - mIntegerPart;
      mFractionPart = Frac.getNew( fraction_part, denomionator_bits );
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   public int getClassNumber()
      {  return 33;  }

   public float toFloat()
      {
      return mIntegerPart + mFractionPart.toFloat();
      }

   public double toDouble()
      {
      return mIntegerPart + mFractionPart.toDouble();
      }

   @Override
   public boolean equals( Object o )
      {
      return (o.getClass().equals( this.getClass() )
              &&  ((Real) o).mIntegerPart == mIntegerPart
              &&  ((Real) o).mFractionPart.equals( mFractionPart ) );
      }

   @Override
   public int hashCode()
      {
      return ((Long) mIntegerPart).hashCode()
             ^ mFractionPart.hashCode()
             ^ this.getClass().hashCode();
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#prettyPrint()
    */
   public String prettyPrint()
      {
      return "(Real "
             + Util.prettyPrintList( mIntegerPart, mFractionPart )
             + ")";
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#writeExternal(java.io.OutputStream)
    */
   public void writeExternal( OutputStream out ) throws IOException
      {
      DLong.longAsExternal( out, mIntegerPart );
      mFractionPart.writeExternal( out );
      }

   private final long mIntegerPart;
   private final Frac mFractionPart;
   }

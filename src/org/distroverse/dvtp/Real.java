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
import java.math.BigDecimal;
import java.math.MathContext;

//immutable

/**
 * @author dreish
 *
 */
public class Real implements DvtpExternalizable
   {
   public static final Real ZERO = new Real( BigDecimal.ZERO );

   /**
    *
    */
   public Real( InputStream in ) throws IOException
      {
      super();
      mVal = Real.externalAsBigDecimal( in );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings( "unused" )
   private Real()
      {
      mVal = null;
      }

   public Real( BigDecimal v )
      {
      mVal = convBigDecimal( v );
      }

   public Real( double d )
      {
      mVal = convBigDecimal( new BigDecimal( d ) );
      }

   public Real( double d, int precision )
      {
      mVal = convBigDecimal( new BigDecimal( d,
                                       new MathContext( precision ) ) );
      }

   private BigDecimal convBigDecimal( BigDecimal v )
      {
      if ( v.scale() == 0 )
         return v.setScale( 2 );
      if ( v.scale() % 2 == 0 )
         return v;
      return v.setScale( v.scale() + 1 );
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   public int getClassNumber()
      {  return 33;  }

   public float toFloat()
      {
      return toBigDecimal().floatValue();
      }

   public double toDouble()
      {
      return toBigDecimal().doubleValue();
      }

   public BigDecimal toBigDecimal()
      {
      return mVal;
      }

   @Override
   public boolean equals( Object o )
      {
      return (o.getClass().equals( this.getClass() )
              &&  ((Real) o).mVal.equals( mVal ) );
      }

   @Override
   public int hashCode()
      {
      return mVal.hashCode()
             ^ this.getClass().hashCode();
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#prettyPrint()
    */
   public String prettyPrint()
      {
      return "(Real. " + mVal + "M)";
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#writeExternal(java.io.OutputStream)
    */
   public void writeExternal( OutputStream out ) throws IOException
      {
      Real.bigDecimalAsExternal( out, mVal );
      }

   public static BigDecimal externalAsBigDecimal( InputStream in )
   throws IOException
      {
      StringBuilder srep = new StringBuilder();

      byte b = (byte) in.read();
      if ( b == 100 )
         srep.append( '-' );
      else
         srep.append( (b & 127) % 100 );

      while ( (b & 128) != 128 )
         {
         b = (byte) in.read();
         if ( (b & 127) > 99 )
            throw new IOException( "Malformed Real in input" );
         srep.append( (b & 127) / 10 );
         srep.append( (b & 127) % 10 );
         }

      srep.append( '.' );

      while ( true )
         {
         b = (byte) in.read();
         if ( (b & 127) > 99 )
            throw new IOException( "Malformed Real in input" );
         srep.append( (b & 127) / 10 );
         srep.append( (b & 127) % 10 );
         if ( (b & 128) == 128 )
            return new BigDecimal( srep.toString() );
         }
      }

   private static int encodeStr( String s )
      {
      return Integer.valueOf( s );
      }

   public static void bigDecimalAsExternal( OutputStream out,
                                            BigDecimal val )
   throws IOException
     {
     if ( val.signum() == -1 )
        {
        out.write( 100 );
        bigDecimalAsExternal( out, val.negate() );
        return;
        }

     String srep = val.toPlainString();
     String[] srep_parts = srep.split( "\\.", 2 );
     String int_part = srep_parts[ 0 ];
     String dec_part = (srep_parts.length > 1) ? srep_parts[ 1 ] : "0";

     writeCentesimal( out, int_part, false );
     writeCentesimal( out, dec_part, true );
     }

   private static void writeCentesimal( OutputStream out,
                                        String arg_num_str,
                                        boolean append_zero )
   throws IOException
      {
      String num_str = arg_num_str;
      if ( (num_str.length() % 2) == 1 )
         {
         if ( append_zero )
            num_str = num_str + "0";
         else
            num_str = "0" + num_str;
         }

      int pos;
      for ( pos = 0;
            pos < num_str.length() - 2;
            pos += 2 )
         out.write( encodeStr( num_str.substring( pos, pos + 2 ) ) );

      out.write( encodeStr( num_str.substring( pos, pos + 2 ) )
                 + 128 );
      }

   private final BigDecimal mVal;
   }

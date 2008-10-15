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
 * Provides a numeric error code, and a string for an optional
 * explanatory message.  The string need not describe the error code; it
 * is assumed that the client will interpret the code and explain it to
 * the user.
 * @author dreish
 *
 */
public class Err implements DvtpExternalizable
   {
   public Err( String message, int code )
      {
      super();
      mMessage = message;
      mCode = code;
      }

   public Err()
      {
      super();
      }

   public int getClassNumber()
      {  return 131;  }

   @Override
   public boolean equals( Object o )
      {
      if ( o.getClass().equals( this.getClass() ) )
         {
         Err e = (Err) o;
         return (mCode == e.mCode && mMessage.equals( e.mMessage ));
         }
      return false;
      }

   @Override
   public int hashCode()
      {
      // Why 1,419,857?  Because I said so.
      return mMessage.hashCode() ^ (mCode * 1419857);
      }

   public String getMessage()
      {  return mMessage;  }
   public int getCode()
      {  return mCode;  }

   public void readExternal( InputStream in ) throws IOException
      {
      mMessage = Str.externalAsString( in );
      mCode    = Util.safeInt( CompactUlong.externalAsLong( in ) );
      }

   public void writeExternal( OutputStream out ) throws IOException
      {
      Str.stringAsExternal( out, mMessage );
      CompactUlong.longAsExternal( out, mCode );
      }

   public String prettyPrint()
      {
      return "(Err " + Util.prettyPrintList( mMessage, mCode ) + ")";
      }

   private String mMessage;
   private int    mCode;
   }

package org.distroverse.dvtp;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

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
   
   public String getMessage()
      {  return mMessage;  }
   public int getCode()
      {  return mCode;  }

   public void readExternal( ObjectInput in ) throws IOException
      {
      mMessage = Str.externalAsString( in );
      mCode    = Util.safeInt( CompactUlong.externalAsLong( in ) );
      }

   public void writeExternal( ObjectOutput out ) throws IOException
      {
      Str.stringAsExternal( out, mMessage );
      CompactUlong.longAsExternal( out, mCode );
      }
   
   private String mMessage;
   private int    mCode;
   }

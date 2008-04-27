/*
 * Copyright (c) 2007 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * Lesser General Public License (GPL) version 3 or later</a>
 */
package org.distroverse.dvtp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.distroverse.core.Log;
import org.distroverse.core.Util;

/**
 * This class collects routines used in externalizing DVTP objects,
 * which actually implement DvtpExternalizable rather than inheriting
 * from this class.
 * @author dreish
 */
public abstract class DvtpObject
   {
   public static final Class< ? > mClassList[] 
      = { 
        CompactUlong.class,     // 0
        Pair.class,             // 1
        Str.class,              // 2
        BigInt.class,           // 3
        PointArray.class,       // 4
        
        null
        };
   public static final int mSerializedClassNumber = 0xB00BAD;
   
   /**
    * Returns a new object (constructed with the default constructor) of
    * the given class number.
    * @param class_number
    * @return
    */
   @SuppressWarnings("unchecked")
   public static DvtpExternalizable getNew( int class_number )
      {
      if ( class_number < mClassList.length
           &&  class_number >= 0 )
         {
         Class< ? extends DvtpExternalizable > newclass
            = (Class< ? extends DvtpExternalizable >) 
              mClassList[ class_number ];
         DvtpExternalizable ret;
         try  {  ret = newclass.newInstance();  }
         catch ( Exception e )
            {
            Log.p( "Impossible exception: " + e, 
                   Log.DVTP | Log.UNHANDLED, 100 );
            Log.p( e, Log.DVTP | Log.UNHANDLED, 100 );
            System.exit( 42 );
            return null;   // I have to do this to avoid an error?
            }
         assert( ret.getClassNumber() == class_number );
         return ret;
         }
      else if ( class_number == mSerializedClassNumber )
         return new Any();
      return null;
      }
   
   public static int getSerializedClassNumber()
      {  return mSerializedClassNumber;  }
   
   public static DvtpExternalizable parseObject( ObjectInput in ) 
   throws IOException
      {
      int class_number 
         = Util.safeInt( CompactUlong.externalAsLong( in ) );
      return parseObject( in, class_number );
      }
   
   /**
    * 
    * @param in
    * @param class_number
    * @return
    */
   public static DvtpExternalizable parseObject( ObjectInput in,
                                                 int class_number )
   throws IOException
      {
      DvtpExternalizable ob = getNew( class_number );
      try
         {
         if ( ob != null )
            ob.readExternal( in );
         }
      catch ( ClassNotFoundException e )
         {
         Log.p( "Impossible exception: " + e, Log.DVTP | Log.UNHANDLED,
                100 );
         Log.p( e, Log.DVTP | Log.UNHANDLED, 100 );
         }
      return ob;
      }

   /**
    * Writes a complete NUL/length/type-prefixed DVTP object to the
    * given ObjectOutput.
    * @param oo - Output to which to write 'de'
    * @param de - Object to write
    * @throws IOException
    */
   public static void writeObject( ObjectOutput oo, 
                                   DvtpExternalizable de )
   throws IOException
      {
      ByteArrayOutputStream rawob = new ByteArrayOutputStream();
      ObjectOutput rawob_oo = new ObjectOutputStream( rawob );
      CompactUlong type = new CompactUlong( de.getClassNumber() );
      type.writeExternal( rawob_oo );
      de.writeExternal( rawob_oo );
      
      // NUL,
      oo.write( 0 );
      // length,
      CompactUlong length = new CompactUlong( rawob.size() );
      length.writeExternal( oo );
      // type, and the object itself
      oo.write( rawob.toByteArray() );
      }
   }

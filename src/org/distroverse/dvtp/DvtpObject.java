/*
 * Copyright (c) 2007 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * Lesser General Public License (GPL) version 3 or later</a>
 */
package org.distroverse.dvtp;

import java.io.ObjectInput;

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
        
        null
        };
   public static final BigInt mSerializedClassNumber 
      = new BigInt( 0xB00BAD );
   
   /**
    * Returns a new object (constructed with the default constructor) of
    * the given class number.
    * @param class_number
    * @return
    */
   @SuppressWarnings("unchecked")
   public static DvtpExternalizable getNew( BigInt class_number )
      {
      if ( class_number.compareTo(
                           new BigInt( mClassList.length ) ) < 0
           &&  class_number.compareTo( BigInt.ZERO ) >= 0 )
         {
         Class< ? extends DvtpExternalizable > newclass
            = (Class< ? extends DvtpExternalizable >) 
              mClassList[ class_number.intValue() ];
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
      else if ( class_number.equals( mSerializedClassNumber ) )
         return new Any();
      return null;
      }
   
   public static BigInt getSerializedClassNumber()
      {  return mSerializedClassNumber;  }
   
   public static DvtpExternalizable parseObject( ObjectInput in )
      {
      int class_number 
         = Util.safeInt( CompactUlong.externalAsLong( in ) );
      return parseObject( in, class_number );
      }
   
   public static DvtpExternalizable parseObject( ObjectInput in,
                                                 int class_number )
      {
      
      }
   }

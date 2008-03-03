/**
 * 
 */
package org.distroverse.dvtp;

import org.distroverse.core.Log;

/**
 * This class collects routines used in externalizing DVTP objects,
 * which actually implement DvtpExternalizable rather than inheriting
 * from this class.
 * @author dreish
 */
public abstract class DvtpObject
   {
   static final Class< ? > mClassList[] 
      = { 
        CompactUlong.class,     // 0
        Pair.class,             // 1
        Str.class,              // 2
        BigInt.class,           // 3
        
        null
        };
   static final BigInt mSerializedClassNumber 
      = new BigInt( 0xB00BAD );
   
   /**
    * Returns a new object (constructed with the default constructor) of
    * the given class number.
    * @param class_number
    * @return
    */
   @SuppressWarnings("unchecked")
   static DvtpExternalizable getNew( BigInt class_number )
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
   
   static BigInt getSerializedClassNumber()
      {  return mSerializedClassNumber;  }
   }

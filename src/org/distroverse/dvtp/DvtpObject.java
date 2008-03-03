/**
 * 
 */
package org.distroverse.dvtp;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.math.BigInteger;

import org.distroverse.core.Log;

/**
 * @author dreish
 *
 */
public abstract class DvtpObject
   {
   static final Class< ? > mClassList[] 
      = { 
        BigInt.class,     // 0
        Pair.class,       // 1
        String.class      // 2
        };
   static final BigInt mSerializedClassNumber 
      = (BigInt) BigInteger.valueOf( 0xB00BAD );
   
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
              BigInteger.valueOf( mClassList.length ) ) < 0
           &&  class_number.compareTo( BigInteger.ZERO ) >= 0 )
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

   /* (non-Javadoc)
    * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
    */
   public void readExternal( ObjectInput in ) throws IOException,
                                             ClassNotFoundException
      {
      // TODO Auto-generated method stub
      
      }

   /* (non-Javadoc)
    * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
    */
   public void writeExternal( ObjectOutput out ) throws IOException
      {
      // TODO Auto-generated method stub

      }

   }

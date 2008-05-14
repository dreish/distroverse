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
public final class DvtpObject
   {
   /**
    * Maps integer to class.  To go the other way, call
    * getClassNumber().
    */
   public static final Class< ? > mClassList[] 
      = { 
        CompactUlong.class,     // 0
        Pair.class,             // 1
        Str.class,              // 2
        BigInt.class,           // 3
        PointArray.class,       // 4
        False.class,            // 5
        True.class,             // 6
        ProxySpec.class,        // 7
        ProxyDefer.class,       // 8
        SetUrl.class,           // 9
        Shape.class,            // 10
        Vec.class,              // 11
        AddObject.class,        // 12
        Movement.class,         // 13
        MoveObject.class,       // 14
        Flo.class,              // 15
        
        null
        };
   public static final int mSerializedClassNumber = 0xB00BAD;
   
   /**
    * Returns a new object (constructed with the default constructor) of
    * the given class number.
    * @param class_number
    * @return
    * @throws ClassNotFoundException 
    */
   @SuppressWarnings("unchecked")
   public static DvtpExternalizable getNew( int class_number ) 
   throws ClassNotFoundException
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
      else 
         throw new ClassNotFoundException( "No such"
             + " DvtpExternalizable class number: " + class_number );
      }
   
   public static int getSerializedClassNumber()
      {  return mSerializedClassNumber;  }
   
   /**
    * This class parses everything except an initial NUL and length:
    * class number followed by the object itself.
    * @param in
    * @return
    * @throws IOException
    * @throws ClassNotFoundException
    */
   public static DvtpExternalizable parseObject( ObjectInput in ) 
   throws IOException, ClassNotFoundException
      {
      int class_number 
         = Util.safeInt( CompactUlong.externalAsLong( in ) );
      return parseObject( in, class_number );
      }
   
   /**
    * This class parses an object of a known class.
    * @param in - an ObjectInput stream
    * @param class_number - the class number of the object to parse
    * @return - an object of the requested class
    * @throws ClassNotFoundException 
    */
   public static DvtpExternalizable parseObject( ObjectInput in,
                                                 int class_number )
   throws IOException, ClassNotFoundException
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
      DvtpObject.writeInnerObject( rawob_oo, de );
      
      // NUL,
      oo.write( 0 );
      // length,
      CompactUlong length = new CompactUlong( rawob.size() );
      length.writeExternal( oo );
      // type, and the object itself
      oo.write( rawob.toByteArray() );
      }

   /**
    * Writes a type-prefixed DVTP object to the given ObjectOutput.
    * @param oo - Output to which to write 'de'
    * @param de - Object to write
    * @throws IOException
    */
   public static void writeInnerObject( ObjectOutput oo, 
                                        DvtpExternalizable de )
   throws IOException
      {
      CompactUlong type = new CompactUlong( de.getClassNumber() );
      type.writeExternal( oo );
      de.writeExternal( oo );
      }

   /**
    * This class cannot be constructed.  It is just a collection of
    * static methods.
    */
   private DvtpObject() { /* Nothing */ }
   }

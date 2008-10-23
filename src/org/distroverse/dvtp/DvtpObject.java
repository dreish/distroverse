/*
 * Copyright (c) 2007-2008 Dan Reish.
 *
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * Lesser General Public License (GPL) version 3 or later</a>
 */
package org.distroverse.dvtp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;

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
        DisplayUrl.class,       // 7
        RedirectUrl.class,      // 8
        SetUrl.class,           // 9
        Shape.class,            // 10
        Vec.class,              // 11
        AddObject.class,        // 12
        Move.class,             // 13
        MoveObject.class,       // 14
        Flo.class,              // 15
        Quat.class,             // 16
        DeleteObject.class,     // 17
        MoveSeq.class,          // 18
        Keystroke.class,        // 19
        KeyDown.class,          // 20
        KeyUp.class,            // 21
        Click.class,            // 22
        Click2.class,           // 23
        MoreDetail.class,       // 24
        Blob.class,             // 25
        GetCookie.class,        // 26
        SendCookie.class,       // 27
        Dict.class,             // 28

        null
        };

   public static final Class< ? > mExtendedClassList[]
      = {
        DList.class,            // 128
        FunCall.class,          // 129
        FunRet.class,           // 130
        Err.class,              // 131
        ConPerm.class,          // 132
        ProxySpec.class,        // 133
        AskInv.class,           // 134
        ReplyInv.class,         // 135

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
      Class< ? extends DvtpExternalizable > newclass;
      if ( class_number < mClassList.length - 1
            &&  class_number >= 0 )
         {
         newclass = (Class< ? extends DvtpExternalizable >)
                    mClassList[ class_number ];
         }
      else if ( class_number < mExtendedClassList.length + 127
                &&  class_number >= 128 )
         {
         newclass = (Class< ? extends DvtpExternalizable >)
                    mExtendedClassList[ class_number - 128 ];
         }
      else if ( class_number == mSerializedClassNumber )
         return new Any();
      else
         throw new ClassNotFoundException( "No such"
             + " DvtpExternalizable class number: " + class_number );

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
   public static DvtpExternalizable parseObject( InputStream in )
   throws IOException, ClassNotFoundException
      {
      int class_number
         = Util.safeInt( CompactUlong.externalAsLong( in ) );
      return parseObject( in, class_number );
      }

   /**
    * This class parses an object of a known class.
    * @param in - an InputStream stream
    * @param class_number - the class number of the object to parse
    * @return - an object of the requested class
    * @throws ClassNotFoundException
    */
   public static DvtpExternalizable parseObject( InputStream in,
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
         Log.p( "Exception while parsing external object: " + e,
                Log.DVTP | Log.UNHANDLED, 100 );
         Log.p( e, Log.DVTP | Log.UNHANDLED, 100 );
         }
      return ob;
      }

   /**
    * Writes a complete NUL/length/type-prefixed DVTP object to the
    * given OutputStream.
    * @param oo - Output to which to write 'de'
    * @param de - Object to write
    * @throws IOException
    */
   public static void writeObject( OutputStream oo,
                                   DvtpExternalizable de )
   throws IOException
      {
      ByteArrayOutputStream rawob = new ByteArrayOutputStream();
      DvtpObject.writeInnerObject( rawob, de );

      // NUL,
      oo.write( 0 );
      // length,
      CompactUlong length = new CompactUlong( rawob.size() );
      length.writeExternal( oo );
      // type, and the object itself
      oo.write( rawob.toByteArray() );
      }

   /**
    * Writes a type-prefixed DVTP object to the given OutputStream.
    * @param oo - Output to which to write 'de'
    * @param de - Object to write
    * @throws IOException
    */
   public static void writeInnerObject( OutputStream oo,
                                        DvtpExternalizable de )
   throws IOException
      {
      CompactUlong type = new CompactUlong( de.getClassNumber() );
      type.writeExternal( oo );
      de.writeExternal( oo );
      }

   /**
    * Reads 'n' objects of type T from 'in'.
    * @param <T>
    * @param in
    * @param n
    * @param t
    * @return an array, T[n].
    * @throws ClassNotFoundException
    * @throws IOException
    * @throws ClassNotFoundException
    */
   @SuppressWarnings("unchecked")
   public static <T extends DvtpExternalizable>
   T[] readArray( InputStream in, int n, Class<T> t )
   throws IOException, ClassNotFoundException
      {
      T[] ret = (T[]) Array.newInstance( t, n );
      for ( int i = 0; i < n; ++i )
         {
         try
            {
            ret[ i ] = t.newInstance();
            }
         catch ( InstantiationException e )
            {
            Log.p( "Impossible exception: " + e,
                   Log.DVTP | Log.UNHANDLED, 100 );
            Log.p( e, Log.DVTP | Log.UNHANDLED, 100 );
            }
         catch ( IllegalAccessException e )
            {
            Log.p( "Impossible exception: " + e,
                   Log.DVTP | Log.UNHANDLED, 100 );
            Log.p( e, Log.DVTP | Log.UNHANDLED, 100 );
            }
         ret[ i ].readExternal( in );
         }

      return ret;
      }

   /**
    * This writes a NON-LENGTH-PREFIXED array.  You'll need to store the
    * length in its own field.
    * @param out
    * @param arr
    * @throws IOException
    */
   public static <T extends DvtpExternalizable>
   void writeArray( OutputStream out, T[] arr )
   throws IOException
      {
      for ( T o : arr )
         o.writeExternal( out );
      }

   /**
    * This class cannot be constructed.  It is just a collection of
    * static methods.
    */
   private DvtpObject() { /* Nothing */ }

   }

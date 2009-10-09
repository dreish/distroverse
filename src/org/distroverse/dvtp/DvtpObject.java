/*
 * Copyright (c) 2007-2008 Dan Reish.
 *
 * For license details, see the file COPYING-L in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/lgpl.html">GNU
 * Lesser General Public License (LGPL) version 3 or later</a>
 */
package org.distroverse.dvtp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;

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
        ULong.class,            // 0
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
        Cookie.class,           // 27
        Dict.class,             // 28
        DNodeRef.class,         // 29
        DNode.class,            // 30
        DLong.class,            // 31
        Frac.class,             // 32
        Real.class,             // 33
        Warp.class,             // 34
        WarpSeq.class,          // 35

        null
        };

   public static final Class< ? > mExtendedClassList[]
      = {
        DList.class,            // 128
        FunCall.class,          // 129
        FunRet.class,           // 130
        Err.class,              // 131
        SetVisible.class,       // 132
        EnvoySpec.class,        // 133
        AskInv.class,           // 134
        ReplyInv.class,         // 135
        SetShape.class,         // 136
        WarpObject.class,       // 137
        ReparentObject.class,   // 138
        ClearShape.class,       // 139
        CTrans.class,           // 140
        SetFora.class,          // 141

        null
        };

   public static Class< ? > getClassByNumber( int class_number )
   throws ClassNotFoundException
      {
      if ( class_number < mClassList.length )
         return mClassList[ class_number ];
      else if ( class_number >= 128
                &&  class_number - 128 < mExtendedConstructors.length )
         return mExtendedClassList[ class_number - 128 ];
      else
         throw new ClassNotFoundException( "No such DvtpExternalizable"
                                           + "class: " + class_number );
      }

   /**
    * Returns a new object (constructed with the default constructor) of
    * the given class number.
    * @param class_number
    * @return
    * @throws ClassNotFoundException
    * @throws IOException
    */
   @SuppressWarnings("unchecked")
   public static DvtpExternalizable getNew( int class_number,
                                            InputStream in )
   throws ClassNotFoundException
      {
      if ( mConstructors == null )
         initConstructors();

      Constructor< ? extends DvtpExternalizable > ctor;
      if ( class_number < mConstructors.length )
         ctor = mConstructors[ class_number ];
      else if ( class_number >= 128
                &&  class_number - 128 < mExtendedConstructors.length )
         ctor = mExtendedConstructors[ class_number - 128 ];
      else
         throw new ClassNotFoundException( "No such DvtpExternalizable"
                                           + "class: " + class_number );

      DvtpExternalizable ret;
      try
         {
         ret = ctor.newInstance( in );
         }
      catch ( Exception e )
         {
         throw new RuntimeException( e );
//         throw new IOException( "Exception from constructor for DVTP"
//                             + " class number " + class_number + ": "
//                             + e.getMessage() );
         }

      assert( ret.getClassNumber() == class_number );
      return ret;
      }

   @SuppressWarnings("unchecked")
   private static void initConstructors()
      {
      mConstructors = new Constructor[ mClassList.length ];
      mExtendedConstructors
         = new Constructor[ mExtendedClassList.length ];
      for ( int i = 0; i < mClassList.length - 1; ++i )
         {
         mConstructors[ i ] =
            getConstructor( (Class< ? extends DvtpExternalizable >)
                            mClassList[ i ] );
         }
      for ( int i = 0; i < mExtendedClassList.length - 1; ++i )
         {
         mExtendedConstructors[ i ] =
            getConstructor( (Class< ? extends DvtpExternalizable >)
                            mExtendedClassList[ i ] );
         }
      mConstructors[ mConstructors.length - 1 ] = null;
      mExtendedConstructors[ mExtendedConstructors.length - 1 ] = null;
      }

   private static Constructor< ? extends DvtpExternalizable >
   getConstructor( Class< ? extends DvtpExternalizable > c )
      {
      try
         {
         return c.getConstructor( InputStream.class );
         }
      catch ( Exception e )
         {
         e.printStackTrace();
         }
      return null;
      }

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
      int class_number = Util.safeInt( ULong.externalAsLong( in ) );
      return getNew( class_number, in );
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
   throws ClassNotFoundException
      {
      return getNew( class_number, in );
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
      ULong length = new ULong( rawob.size() );
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
      ULong type = new ULong( de.getClassNumber() );
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
   T[] readArray( InputStream in, int n, Class<T> t, int class_number )
   throws ClassNotFoundException
      {
      T[] ret = (T[]) Array.newInstance( t, n );
      for ( int i = 0; i < n; ++i )
         {
         ret[ i ] = (T) getNew( class_number, in );
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

   private static Constructor< ? extends DvtpExternalizable >[]
      mConstructors;
   private static Constructor< ? extends DvtpExternalizable >[]
      mExtendedConstructors;
   }

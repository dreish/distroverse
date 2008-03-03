/*
 * Copyright (c) 2007 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * General Public License (GPL) version 3 or later</a>
 */

package org.distroverse.core;

// import java.lang.reflect.*;

/**
 * Util contains static methods that are bafflingly missing from
 * the standard Java libraries.
 * 
 * WARNING: Don't start reading here!  You'll ruin the surprise!  Try
 * the README files in dvop, dvtp, or viewer.
 */

public final class Util
   {
   /**
    * Safely casts a long to an int, or throws an exception if n is
    * not within the range of legal values for an int.  It would be nice
    * if there were some reasonable way to do this generically.
    * TODO - improve Java's Number class hierarchy, or add templates or
    * macros, and make this generic
    * @param n - a long
    * @return (int) n, if it is within the range of integers 
    */
   public static int safeInt( long n )
      {
      if ( n > Integer.MAX_VALUE )
         throw new IllegalArgumentException( 
                      "long-to-int cast overflow" );
      if ( n < Integer.MIN_VALUE )
         throw new IllegalArgumentException( 
                      "long-to-int cast underflow" );
      return (int) n;
      }
   
   /**
    * An anonymous function class taking two values of a type and
    * returning one value of that type.  For use with foldL, or wherever
    * functions taking two arguments of a type and returning one are
    * needed.
    */
   public static interface FoldingFunction< T >
      {  public T call( T a, T b );  }

   /**
    * Repeatedly applies 'folding_function' to the first two (Leftmost)
    * elements of 'list', replacing them with the result, until there is
    * only one element left; then returns that one element. In the
    * spirit of functional programming, does not actually modify list
    * (unless 'function' modifies its parameters, which is strongly
    * discouraged).
    * 
    * TODO Look for a way to generalize 'list' to any iterable object.
    * 
    * @param <T> -
    *           Class of list, generally inferred
    * @param <F> -
    *           Class of functor, generally inferred
    * @param list -
    *           The array to fold
    * @param folding_function -
    *           A folding function
    * @return folded value
    */
   public static < T, F extends FoldingFunction< T > >
                 T foldL( T[] list, F folding_function )
      {
      T folded = null;
      if ( list.length > 0 )
         {
         folded = list[ 0 ];
         for ( int i = 1; i < list.length; ++i )
            folded = folding_function.call( folded, list[ i ] );
         }
      return folded;
      }

   /**
    * Returns the maximum element of the given list, according to
    * compareTo(). Can be called with an array or an argument list.
    * Returns null if given an empty list. In the case where multiple
    * maximum elements are equivalent, returns the first such
    * occurrence.
    * 
    * @param <T> -
    *           Any Comparable type
    * @param list -
    *           Any list of Ts
    * @return Maximum element of list
    */
   public static < T extends Comparable< T > > T max( T... list )
      {
      return foldL( list, new FoldingFunction<T> ()
         { public T call( T a, T b )
            { return (a.compareTo( b ) > 0 ? a : b); } } );
      }

   /**
    * Returns the minimum element of the given list, according to
    * compareTo(). Can be called with an array or an argument list.
    * Returns null if given an empty list. In the case where multiple
    * minimum elements are equivalent, returns the first such
    * occurrence.
    * 
    * @param <T> -
    *           Any Comparable type
    * @param list -
    *           Any list of Ts
    * @return Minimum element of list
    */
   public static < T extends Comparable< T > > T min( T... list )
      {
      return foldL( list, new FoldingFunction<T> ()
         { public T call( T a, T b )
            { return (a.compareTo( b ) < 0 ? a : b); } } );
      }

   // TODO Perf test max() against max1(), if it ever matters.
   @Deprecated public static < T extends Comparable< T > >
   T max1( T... list )
      {
      T max = null;
      if ( list.length > 0 )
         {
         max = list[0];
         for ( int i = 1; i < list.length; ++i )
            if ( list[i].compareTo( max ) > 0 ) 
               max = list[i];
         }
      return max;
      }

   @Deprecated public static < T extends Comparable< T > > 
   T min1( T... list )
      {
      T min = null;
      if ( list.length > 0 )
         {
         min = list[0];
         for ( int i = 1; i < list.length; ++i )
            if ( list[i].compareTo( min ) < 0 ) 
               min = list[i];
         }
      return min;
      }
   
   /**
    * This is like String.StartsWith(), but is case insensitive.
    * @param full - The string to check
    * @param prefix - The prefix to look for
    * @return Whether "full" begins with "prefix"
    */
   public static boolean stringStartsIgnoreCase( String full, 
                                                 String prefix )
      {
      if ( prefix.length() > full.length() )
         return false;
      return ( full.substring( 0, prefix.length() )
                   .compareToIgnoreCase( prefix )
               == 0 );
      }
   

   }

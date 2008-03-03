package org.distroverse.core;

// import java.lang.*;

/**
 * Util contains static methods that are bafflingly missing from
 * the standard Java libraries.
 */

public final class Util
   {
   /**
    * Safely casts a long to an int, or throws an exception if n is
    * not within the range of legal values for an int.
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
    * Returns the maximum element of the given list, according to
    * compareTo().  Can be called with an array or an argument
    * list.  Returns null if given an empty list.  In the case
    * where multiple maximum elements are equivalent, returns the
    * first such occurence.
    * @param <T> - Any Comparable type
    * @param list - Any list of Ts
    * @return Maximum element of list
    */
   public static <T extends Comparable<T>> T max( T... list )
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

   /**
    * Returns the maximum element of the given list, according to
    * compareTo().  Can be called with an array or an argument
    * list.  Returns null if given an empty list.  In the case
    * where multiple minimum elements are equivalent, returns the
    * first such occurence.
    * @param <T> - Any Comparable type
    * @param list - Any list of Ts
    * @return Maximum element of list
    */
   public static <T extends Comparable<T>> T min( T... list )
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
   }

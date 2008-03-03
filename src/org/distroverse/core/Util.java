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
   
   public static int max( int first, int... rest )
      {
      int max = first;
      for ( int i : rest )
         if ( i > max )  max = i;
      return max;
      }
   }

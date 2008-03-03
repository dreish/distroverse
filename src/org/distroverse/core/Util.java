package org.distroverse.core;

// import java.lang.*;

public final class Util
   {
   public static int SafeInt( long n )
      {
      if ( n > Integer.MAX_VALUE )
         throw new IllegalArgumentException( 
                      "long-to-int cast overflow" );
      if ( n < Integer.MIN_VALUE )
         throw new IllegalArgumentException( 
                      "long-to-int cast underflow" );
      return (int) n;
      }
   }

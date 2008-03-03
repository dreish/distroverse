package org.distroverse.core;

public class Log
   {
   public static final int UNHANDLED = 1;
   public static final int NET       = 2;

   /**
    * Write message to the appropriate log or logs for the given type
    * and level.  Higher numbers indicate increasingly important
    * messages, with positive numbers requiring writing to some sort
    * of permanent log file.  (Most messages should have a negative
    * level.)
    * @param message - Any freeform text
    * @param type - A bitmask of the general category(ies) of the log
    * @param level - The importance of the log message
    */
   @SuppressWarnings( "unused" )
   public static void p( String message, int type, int level )
      {
      // TODO bring this method up to the above specification.
      if ( mLogging )
         {
         ++mNumLogMessages;
         System.err.println( message );
         if ( mNumLogMessages >= 500 )
            {
            System.err.println( "Log size limit reached; logging"
                                + " stopped" );
            mLogging = false;
            }
         }
      }

   @SuppressWarnings( "unused" )
   public static void p( Exception e, int type, int level )
      {
      // TODO Turn the stack trace into a string and give it to p
      mNumLogMessages += 10;
      e.printStackTrace();
      }
   
   private static int     mNumLogMessages = 0;
   private static boolean mLogging        = true;
   }
package test;

public class FunnyThreadTest
   {
   public static void main( String[] args ) throws InterruptedException
      {
      /* I had expected this would exhibit the same concurrency bug I
       * was getting in DvtpMultiplexedConnection, but it doesn't,
       * with or without the sleep( 100 ).
       */
      FunnyThread f = new FunnyThread();
      f.start();
      Thread.sleep( 100 );
      f.funnyTwo();
      }
   }

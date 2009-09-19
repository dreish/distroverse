package test;

public class FunnyThread extends Thread
   {
   public FunnyThread()
      {
      mutex = new Object();
      }
   
   @Override
   public void run()
      {
      try
         {
         funnyOne();
         }
      catch ( InterruptedException e )
         {
         throw new RuntimeException( e );
         }
      }

   public static void pr( String s )
      {
      System.out.println( s );
      }
   
   public void funnyOne() throws InterruptedException
      {
      pr( "Entering critical section 1 ..." );
      synchronized ( mutex )
         {
         pr( "Inside critical section 1, sleeping for 10 ..." );
         Thread.sleep( 10000 );
         pr( "Critical section 1 done sleeping." );
         }
      pr( "Left critical section 1." );
      }

   public void funnyTwo() throws InterruptedException
      {
      pr( "@@ Entering critical section 2 ..." );
      synchronized ( mutex )
         {
         pr( "@@ Inside critical section 2, sleeping for 10 ..." );
         Thread.sleep( 10000 );
         pr( "@@ Critical section 2 done sleeping." );
         }
      pr( "@@ Left critical section 2." );
      }

   public Object mutex;
   }

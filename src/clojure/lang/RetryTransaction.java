package clojure.lang;

public class RetryTransaction
   {
   /**
    * I can almost guarantee Rich would hate this.  Don't call it unless
    * you are prepared for the scorn.
    */
   public static void retry()
      {
      throw new LockingTransaction.RetryEx();
      }
   }

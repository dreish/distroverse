/**
 * 
 */
package org.distroverse.core.net;

import java.util.Queue;

/**
 * Pushes incoming objects onto a queue, paired with their NetInQueues.
 * @author dreish
 */
public class InvertingInQueueObjectWatcher
extends NetInQueueWatcher< Object >
   {
   public class EventPair
      {
      public EventPair( Object a, Object b )
         {
         mA = a;
         mB = b;
         }
      
      @Override
      public String toString()
         {
         return "[,," + mA.toString() + " " + mB.toString() + "]"; 
         }
      
      public Object a()  {  return mA;  }
      public Object b()  {  return mB;  }
      
      private final Object mA;
      private final Object mB;
      }

   /**
    * Default constructor is disallowed; must have a queue to push to. 
    */
   @SuppressWarnings("unused")
   private InvertingInQueueObjectWatcher()
      {
      mQueue = null;
      }
   
   public InvertingInQueueObjectWatcher( Queue< Object > q )
      {
      mQueue = q;
      }

   /* (non-Javadoc)
    * @see org.distroverse.core.net.NetInQueueWatcher#handleNetInObject(java.lang.Object, org.distroverse.core.net.NetInQueue)
    */
   @Override
   protected void handleNetInObject( Object net_in_object,
                                     NetInQueue< Object > queue )
      {
      mQueue.offer( new EventPair( queue, net_in_object ) );
      }

   private final Queue< Object > mQueue;
   }

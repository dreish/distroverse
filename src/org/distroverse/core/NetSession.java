package org.distroverse.core;

public class NetSession< T >
   {
   public NetSession( NetInQueue < T > niqs,
                      NetOutQueue< T > noqs )
      {
      super();
      niqs.setSession( this );
      noqs.setSession( this );
      mNetInQueue  = niqs;
      mNetOutQueue = noqs;
      }
   
   public NetInQueue< T > getNetInQueue()
      {  return mNetInQueue;  }
   public NetOutQueue< T > getNetOutQueue()
      {  return mNetOutQueue;  }

   private NetInQueue< T >  mNetInQueue;
   private NetOutQueue< T > mNetOutQueue;
   }

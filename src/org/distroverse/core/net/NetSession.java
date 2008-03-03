package org.distroverse.core.net;

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
   public < AT > void setAttachment( Class< AT > attachment_class,
                                     AT attachment )
      {
      mAttachmentClass = attachment_class;
      mAttachment      = attachment;
      }

   /**
    * Returns an object of class attachment_class if this NetSession has
    * such an object attached to it.  Otherwise, if this NetSession has
    * nothing attached to it at all, or has an object of a class other
    * than attachment_class attached to it, this method returns null.  
    * Never throws an exception.
    * @param <AT> - Type of attachment_class; inferred
    * @param attachment_class - Class to look for
    * @return - The object attached to this NetSession
    */
   @SuppressWarnings("unchecked")
   public < AT > AT getAttachmentOrNull( Class< AT > attachment_class )
      {
      if ( mAttachment != null
           &&  attachment_class.isAssignableFrom( mAttachmentClass ) )
         return (AT) mAttachment;
      return null;
      }
   
   /**
    * Old-skool attachment getter, for people who are set in their ways.
    * You should probably use getAttachmentOrNull().
    * @return - 
    */
   public Object getAttachment()
      {  return mAttachment;  }

   private NetInQueue< T >  mNetInQueue;
   private NetOutQueue< T > mNetOutQueue;
   private Object           mAttachment;
   private Class< ? >       mAttachmentClass;
   }

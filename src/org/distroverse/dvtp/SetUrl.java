package org.distroverse.dvtp;

/**
 * Sent by a proxy to a client to tell it to go to a different
 * location URL, which can be anywhere.  Bear in mind that a client
 * could be programmed to ignore these, so they do not provide any real
 * keep-away security.
 * 
 * Also sent by a client to a proxy to request that it switch to a
 * different URL.  If the proxy agrees, it will send back a SetUrl
 * object with the same URL, which will be what actually causes the
 * client to show a different URL.
 * 
 * For the moment, this is just a trivial extension of Str.
 * @author dreish
 */
public class SetUrl extends Str
   {
   public SetUrl()
      {  super();  }
   public SetUrl( String url )
      {  super( url );  }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.Str#getClassNumber()
    */
   @Override
   public int getClassNumber()
      {  return 9;  }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.Str#isSendableByProxy()
    */
   @Override
   public boolean isSendableByProxy()
      {  return true;  }
   
   /**
    * @return - the URL to switch to
    */
   public String getUrl()
      {  return toString();  }
   }

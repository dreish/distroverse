package org.distroverse.dvtp;

/**
 * Sent by a proxy to a client to tell it to go to a URL at a different
 * site.  The URL contained in this object must NOT be part of the site
 * whose proxy sent the object.  Bear in mind that a client could be
 * programmed to ignore these, so they do not provide any real keep-away
 * security.
 * 
 * For the moment, this is just a trivial extension of Str.
 * @author dreish
 */
public class RedirectUrl extends Str implements ProxySendable
   {
   public RedirectUrl()
      {  super();  }
   public RedirectUrl( String url )
      {  super( url );  }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.Str#getClassNumber()
    */
   @Override
   public int getClassNumber()
      {  return 11;  }
   
   /**
    * @return - the URL to switch to
    */
   public String getUrl()
      {  return toString();  }
   }

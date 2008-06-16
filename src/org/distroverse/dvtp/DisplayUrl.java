package org.distroverse.dvtp;

import org.distroverse.core.Util;

/**
 * Sent by a proxy to a client to tell it to show a different URL in the
 * location widget.  The URL contained in this object must be part of
 * the site whose proxy sent the object.
 * 
 * For the moment, this is just a trivial extension of Str.
 * @author dreish
 */
public final class DisplayUrl extends Str implements ProxySendable
   {
   public DisplayUrl()
      {  super();  }
   public DisplayUrl( String url )
      {  super( url );  }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.Str#getClassNumber()
    */
   @Override
   public int getClassNumber()
      {  return 7;  }

   /**
    * @return - the URL to switch to
    */
   public String getUrl()
      {  return toString();  }

   @Override
   public String prettyPrint()
      {
      return "(DisplayUrl " + Util.prettyPrintList( getUrl() ) + ")";
      }
   }

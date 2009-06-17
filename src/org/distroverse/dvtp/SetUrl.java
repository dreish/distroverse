/*
 * Copyright (c) 2007-2008 Dan Reish.
 *
 * For license details, see the file COPYING-L in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/lgpl.html">GNU
 * Lesser General Public License (LGPL) version 3 or later</a>
 */
package org.distroverse.dvtp;

import java.io.IOException;
import java.io.InputStream;

//immutable

/**
 * Sent by a client to a envoy to request that it switch to a
 * different URL.  If the envoy agrees, it will send back a DisplayUrl
 * object with the same URL, which will be what actually causes the
 * client to show a different URL.
 *
 * For the moment, this is just a trivial extension of Str.
 * @author dreish
 */
public final class SetUrl extends Str implements ClientSendable
   {
   public SetUrl( InputStream in ) throws IOException
      {
      super( in );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings("unused")
   private SetUrl()
      {  super( "" );  }
   public SetUrl( String url )
      {  super( url );  }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.Str#getClassNumber()
    */
   @Override
   public int getClassNumber()
      {  return 9;  }

   /**
    * @return - the URL to switch to
    */
   public String getUrl()
      {  return toString();  }

   @Override
   public boolean equals( Object o )
      {
      return (o instanceof SetUrl
              && ((SetUrl) o).getUrl()
                             .equals( getUrl() ) );
      }

   @Override
   public int hashCode()
      {
      return super.hashCode() ^ SetUrl.class.hashCode();
      }
   }

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

import org.distroverse.core.Util;

//immutable

/**
 * Sent by an envoy to a client to tell it to go to a URL at a different
 * site.  The URL contained in this object must NOT be part of the site
 * whose envoy sent the object.  Bear in mind that a client could be
 * programmed to ignore these, so they do not provide any real keep-away
 * security.
 *
 * For the moment, this is just a trivial extension of Str.
 * @author dreish
 */
public final class RedirectUrl extends Str implements EnvoySendable
   {
   public RedirectUrl( InputStream in ) throws IOException
      {
      super( in );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings( "unused" )
   private RedirectUrl()
      {  super( "" );  }
   public RedirectUrl( String url )
      {  super( url );  }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.Str#getClassNumber()
    */
   @Override
   public int getClassNumber()
      {  return 8;  }

   /**
    * @return - the URL to switch to
    */
   public String getUrl()
      {  return toString();  }

   @SuppressWarnings( "cast" )
   @Override
   public boolean equals( Object o )
      {
      return (o instanceof RedirectUrl
              && ((RedirectUrl) o).getUrl()
                                  .equals( getUrl() ) );
      }

   @Override
   public int hashCode()
      {
      return super.hashCode() ^ RedirectUrl.class.hashCode();
      }

   @Override
   public String prettyPrint()
      {
      return "(RedirectUrl "
             + Util.prettyPrintList( getUrl() ) + ")";
      }
   }

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
 * Sent by an envoy to a client to tell it to show a different URL in the
 * location widget.  The URL contained in this object must be part of
 * the site whose envoy sent the object.
 *
 * For the moment, this is just a trivial extension of Str.
 * @author dreish
 */
public final class DisplayUrl extends Str implements EnvoySendable
   {
   public DisplayUrl( InputStream in ) throws IOException
      {
      super( in );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings("unused")
   private DisplayUrl()
      {  super( (String) null );  }
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
      return "(org.distroverse.dvtp.DisplayUrl. "
             + Util.prettyPrintList( getUrl() ) + ")";
      }
   }

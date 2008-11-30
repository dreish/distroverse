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
import java.io.OutputStream;

import org.distroverse.core.Util;

//immutable

/**
 * Sent by a proxy to a client to request a cookie from the client's
 * cookie store.
 * @author dreish
 */
public final class GetCookie implements ProxySendable
   {
   /**
    *
    */
   public GetCookie( InputStream in )
   throws IOException, ClassNotFoundException
      {
      super();
      readExternal( in );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings( "unused" )
   private GetCookie()
      {
      mKey = null;
      }

   public GetCookie( DvtpExternalizable key )
      {
      mKey = key;
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   public int getClassNumber()
      {  return 26;  }

   /**
    * @return - the cookie key being requested
    */
   public DvtpExternalizable getKey()
      {  return mKey;  }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#prettyPrint()
    */
   public String prettyPrint()
      {
      return "(GetCookie " + Util.prettyPrintList( mKey ) + ")";
      }

   @Override
   public boolean equals( Object o )
      {
      return (o instanceof GetCookie
              && ((GetCookie) o).getKey()
                                .equals( getKey() ) );
      }

   @Override
   public int hashCode()
      {
      return mKey.hashCode() ^ GetCookie.class.hashCode();
      }

   private void readExternal( InputStream in ) throws IOException,
                                             ClassNotFoundException
      {
      mKey = DvtpObject.parseObject( in );
      }

   public void writeExternal( OutputStream out ) throws IOException
      {
      DvtpObject.writeInnerObject( out, mKey );
      }

   private DvtpExternalizable mKey;
   }

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

/**
 * This class is the only valid non-error response to a LOCATION query.
 * It gives the URL of a proxy to use to connect to this site at the
 * requested location, and a resource regular expression specifying the
 * range of URLs (at the same host) covered by this proxy.  For example,
 * a simple site covered in its entirety by a single proxy could return
 * ".*" for the resource regexp.
 *
 * @author dreish
 */
public class ProxySpec implements DvtpExternalizable
   {
   public ProxySpec( InputStream in ) throws IOException
      {
      super();
      readExternal( in );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   private ProxySpec()
      {
      mProxyUrl = mResourceRegexp = null;
      }

   public ProxySpec( Str proxy_url, Str resource_regexp,
                     Str proxy_name )
      {
      mProxyUrl = proxy_url;
      mResourceRegexp = resource_regexp;
      mProxyName = proxy_name;
      }

   public ProxySpec( String proxy_url, String resource_regexp,
                     String proxy_name )
      {
      mProxyUrl = new Str( proxy_url );
      mResourceRegexp = new Str( resource_regexp );
      mProxyName = new Str( proxy_name );
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   public int getClassNumber()
      {  return 133;  }

   @Override
   public boolean equals( Object o )
      {
      if ( o.getClass().equals( this.getClass() ) )
         {
         ProxySpec ps = (ProxySpec) o;
         return (   mProxyUrl.equals( ps.mProxyUrl )
                 && mResourceRegexp.equals( ps.mResourceRegexp )
                 && mProxyName.equals( ps.mProxyName ));
         }
      return false;
      }

   @Override
   public int hashCode()
      {
      return   mProxyUrl.hashCode()
             ^ mResourceRegexp.hashCode() * 529
             ^ mProxyName.hashCode() * 279841
             ^ this.getClass().hashCode();
      }

   /* (non-Javadoc)
    * @see java.io.Externalizable#readExternal(java.io.InputStream)
    */
   private void readExternal( InputStream in )
   throws IOException, ClassCastException
      {
      mProxyUrl = new Str( in );
      mResourceRegexp = new Str( in );
      mProxyName = new Str( in );
      }

   /* (non-Javadoc)
    * @see java.io.Externalizable#writeExternal(java.io.OutputStream)
    */
   public void writeExternal( OutputStream out ) throws IOException
      {
      mProxyUrl.writeExternal( out );
      mResourceRegexp.writeExternal( out );
      mProxyName.writeExternal( out );
      }

   public String prettyPrint()
      {
      return "(ProxySpec "
             + Util.prettyPrintList( mProxyUrl, mResourceRegexp,
                                     mProxyName ) + ")";
      }

   public Str getProxyUrl()  {  return mProxyUrl;  }
   public Str getResourceRegexp()  {  return mResourceRegexp;  }
   public Str getProxyName()  {  return mProxyName;  }

   private Str mProxyUrl;
   private Str mResourceRegexp;
   private Str mProxyName;
   }

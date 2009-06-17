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
 * This class is the only valid non-error response to a LOCATION query.
 * It gives the URL of a envoy to use to connect to this site at the
 * requested location, and a resource regular expression specifying the
 * range of URLs (at the same host) covered by this envoy.  For example,
 * a simple site covered in its entirety by a single envoy could return
 * ".*" for the resource regexp.
 *
 * @author dreish
 */
public class EnvoySpec implements DvtpExternalizable
   {
   public EnvoySpec( InputStream in ) throws IOException
      {
      super();
      mEnvoyUrl       = new Str( in );
      mResourceRegexp = new Str( in );
      mEnvoyName      = new Str( in );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings( "unused" )
   private EnvoySpec()
      {
      mEnvoyUrl = mResourceRegexp = mEnvoyName = null;
      }

   public EnvoySpec( Str envoy_url, Str resource_regexp,
                     Str envoy_name )
      {
      mEnvoyUrl = envoy_url;
      mResourceRegexp = resource_regexp;
      mEnvoyName = envoy_name;
      }

   public EnvoySpec( String envoy_url, String resource_regexp,
                     String envoy_name )
      {
      mEnvoyUrl = new Str( envoy_url );
      mResourceRegexp = new Str( resource_regexp );
      mEnvoyName = new Str( envoy_name );
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
         EnvoySpec ps = (EnvoySpec) o;
         return (   mEnvoyUrl.equals( ps.mEnvoyUrl )
                 && mResourceRegexp.equals( ps.mResourceRegexp )
                 && mEnvoyName.equals( ps.mEnvoyName ));
         }
      return false;
      }

   @Override
   public int hashCode()
      {
      return   mEnvoyUrl.hashCode()
             ^ mResourceRegexp.hashCode() * 529
             ^ mEnvoyName.hashCode() * 279841
             ^ this.getClass().hashCode();
      }

   /* (non-Javadoc)
    * @see java.io.Externalizable#writeExternal(java.io.OutputStream)
    */
   public void writeExternal( OutputStream out ) throws IOException
      {
      mEnvoyUrl.writeExternal( out );
      mResourceRegexp.writeExternal( out );
      mEnvoyName.writeExternal( out );
      }

   public String prettyPrint()
      {
      return "(EnvoySpec "
             + Util.prettyPrintList( mEnvoyUrl, mResourceRegexp,
                                     mEnvoyName ) + ")";
      }

   public Str getEnvoyUrl()        {  return mEnvoyUrl;  }
   public Str getResourceRegexp()  {  return mResourceRegexp;  }
   public Str getEnvoyName()       {  return mEnvoyName;  }

   private final Str mEnvoyUrl;
   private final Str mResourceRegexp;
   private final Str mEnvoyName;
   }

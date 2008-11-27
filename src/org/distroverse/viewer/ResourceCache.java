/*
 * <copyleft>
 *
 * Copyright 2007-2008 Dan Reish
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or
 * combining it with Clojure (or a modified version of that program)
 * or clojure-contrib (or a modified version of that library),
 * containing parts covered by the terms of the Common Public License,
 * the licensors of this Program grant you additional permission to
 * convey the resulting work. {Corresponding Source for a non-source
 * form of such a combination shall include the source code for the
 * parts of Clojure and clojure-contrib used as well as that of the
 * covered work.}
 *
 * </copyleft>
 */
package org.distroverse.viewer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.distroverse.dvtp.Blob;

/**
 * This is the beloved browser cache.  Remote files are often downloaded
 * into the cache and then used locally.  For now, this is a trivial
 * design with a lot of important missing features, just to serve the
 * purpose of getting files to the client.
 * TODO implement cache expiration
 * TODO store a URL-to-filename map in a file and use shorter filenames
 * TODO send a version number with cacheable data; recheck version
 * @author dreish
 */
public abstract class ResourceCache
   {
   /**
    * Returns a local file URL for the requested resource, downloading
    * the resource if necessary, and counting as a hit for the purposes
    * of expiring the resource from the cache if not.  Theoretically, if
    * the cache is expiring very rapidly, it is possible files could be
    * deleted from the cache before they are used.
    * @param url - remote URL
    * @return local file:// URL
    * @throws URISyntaxException
    * @throws IOException
    * @throws ClassNotFoundException
    */
   public static final String internalizeResourceUrl( String url )
   throws URISyntaxException, IOException, ClassNotFoundException
      {
      checkInit();
      synchronized ( mCanClean )
         {  mCanClean = false;  }

      String ret = localizeUrl( url );
      if ( fileUrlExists( ret ) )
         registerUse( ret );
      else
         downloadUrl( url, ret );
      mCanClean = true;

      return ret;
      }

   /**
    * Returns true if the given remote URL is locally cached, and counts
    * as a hit for the purposes of expiring the resource from the cache.
    * @param url - remote URL
    * @return is it cached?
    * @throws URISyntaxException
    */
   public static final boolean checkResourceUrl( String url )
   throws URISyntaxException
      {
      checkInit();
      synchronized ( mCanClean )
         {  mCanClean = false;  }

      String local_url = localizeUrl( url );
      boolean ret = false;
      if ( fileUrlExists( local_url ) )
         {
         registerUse( local_url );
         ret = true;
         }
      mCanClean = true;

      return ret;
      }

   private static void checkInit()
      {
      if ( ! mInitialized )
         {
         mCanClean = true;
         mInitialized = true;
         }
      }

   private static String localizeUrl( String remote_url )
      {
      // FIXME This hardcoded directory is obviously bogus and stupid
      // FIXME This set of escaped chars is not nearly portable
      return "file:///Users/dreish/.dv/cache/"
             + remote_url.replaceAll( "_", "__" )
                         .replaceAll( "/", "_%" )
                         .replaceAll( ":", "_." )
                         .replaceAll(  "%", "%25" );
      }

   private static boolean fileUrlExists( String file_url )
   throws URISyntaxException
      {
      return new File( new URI( file_url ) ).exists();
      }

   private static void registerUse( String file_url )
      {
      // TODO Implement this, and expiration in general
      }

   /**
    * Downloads the given remote URL, which may use the HTTP, HTTPS,
    * FTP, or DRTP protocols, to the given local file URL.
    * @param remote_url
    * @param file_url
    * @throws URISyntaxException
    * @throws IOException
    * @throws ClassNotFoundException
    */
   public static void downloadUrl( String remote_url, String file_url )
   throws URISyntaxException, IOException, ClassNotFoundException
      {
      File target    = new File( new URI( file_url ) );
      URI remote_uri = new URI( remote_url );
      String scheme  = remote_uri.getScheme();

      if (    scheme.equalsIgnoreCase( "http" )
           || scheme.equalsIgnoreCase( "https" )
           || scheme.equalsIgnoreCase( "ftp" ) )
         standardUrlDownload( remote_uri, target );
      else if ( scheme.equalsIgnoreCase( "drtp" ) )
         drtpUrlDownload( remote_uri, target );
      else
         throw new IllegalArgumentException( "downloadUrl() can only"
                         + " handle HTTP, HTTPS, FTP, and DRTP, not "
                         + scheme );
      }

   private static void standardUrlDownload( URI remote_uri,
                                            File target )
   throws IOException
      {
      URL remote_url = remote_uri.toURL();
      InputStream remote_data = remote_url.openStream();
      OutputStream local_data = new FileOutputStream( target );
      byte[] buffer = new byte[ 4096 ];

      boolean eof = false;
      while ( ! eof )
         {
         int n_read = remote_data.read( buffer );
         if ( n_read < 0 )
            eof = true;
         else
            local_data.write( buffer, 0, n_read );
         }
      }

   private static void drtpUrlDownload( URI remote_uri, File target )
   throws IOException, ClassNotFoundException
      {
      DvtpServerConnection remote_site
         = new DvtpServerConnection( remote_uri );
      String tmpname = target + "_tmp";
      RandomAccessFile local_data
         = new RandomAccessFile( tmpname, "rw" );

      Blob first_blob = remote_site.get( remote_uri );
      long file_length = first_blob.getFileLength();
      long fetched     = first_blob.getBytes().length;

      local_data.seek( first_blob.getPos() );
      local_data.write( first_blob.getBytes() );

      while ( fetched < file_length )
         {
         Blob next_blob = (Blob) remote_site.getObject();

         local_data.seek( next_blob.getPos() );
         local_data.write( next_blob.getBytes() );
         }

      local_data.close();
      (new File( tmpname )).renameTo( target );
      }

   /**
    * cleanCache() removes the least recently used items from the cache
    */
   private static void cleanCache()
      {
      synchronized ( mCanClean )
         {
         if ( mCanClean )
            {
            // TODO implement cleanCache()
            }
         }
      }

   private static boolean mInitialized = false;
   private static Boolean mCanClean;
   }

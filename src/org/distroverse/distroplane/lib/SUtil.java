/*
 * <copyleft>
 *
 * Copyright 2007-2009 Dan Reish
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
 * containing parts covered by the terms of the Eclipse Public
 * License, the licensors of this Program grant you additional
 * permission to convey the resulting work. {Corresponding Source for
 * a non-source form of such a combination shall include the source
 * code for the parts of Clojure and clojure-contrib used as well as
 * that of the covered work.}
 *
 * </copyleft>
 */
package org.distroverse.distroplane.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.distroverse.core.net.NetOutQueue;
import org.distroverse.dvtp.Blob;

public final class SUtil
   {
   /**
    * Breaks a file into pieces of no more than 1400 bytes each, and
    * feeds them through the noq.
    * @param filename
    * @param noq
    * @throws IOException
    */
   public static void sendFile( String filename,
                                String resource,
                                NetOutQueue< Object > noq )
   throws IOException
      {
      sendBlobs( blobsFromFile( filename, resource, 1400 ), noq );
      }

   private static List< Blob > blobsFromFile( String filename,
                                              String resource,
                                              int max_blob_size )
   throws IOException
      {
      // TODO: replace this with a fake list of dynamically-generated
      // virtual blobs.
      File f = new File( filename );
      FileInputStream fis = new FileInputStream( f );
      long file_length = f.length();
      ArrayList< Blob > ret 
         = new ArrayList< Blob >( (int) (file_length / max_blob_size) );

      long pos = 0;
      byte[] next_bytes = new byte[ max_blob_size ];
      while ( true )
         {
         int n_read = fis.read( next_bytes );
         if ( n_read < 1 )
            break;
         ret.add( new Blob( next_bytes, n_read, resource, pos,
                            file_length ) );
         }
      return ret;
      }

   private static void sendBlobs( List< Blob > blobs,
                                  NetOutQueue< Object > noq )
   throws IOException
      {
      for ( Blob b : blobs )
         noq.add( b );
      }
   }

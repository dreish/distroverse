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
      while ( true )
         {
         byte[] next_bytes = new byte[ max_blob_size ];
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

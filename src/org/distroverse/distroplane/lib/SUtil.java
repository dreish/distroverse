package org.distroverse.distroplane.lib;

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
                                NetOutQueue< Object > noq )
   throws IOException
      {
      sendBlobs( blobsFromFile( filename, 1400 ), noq );
      }

   private static List< Blob > blobsFromFile( String filename, 
                                              int max_blob_size )
   throws IOException
      {
      // TODO: replace this with a fake list.
      ArrayList< Blob > ret = new ArrayList< Blob >();
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

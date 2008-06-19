package org.distroverse.distroplane.lib;

import org.distroverse.dvtp.Blob;

public class VirtualBlob extends Blob
   {
   /**
    * This constructor is not allowed for VirtualBlob.
    * @param bytes - unused
    * @param n_read - unused
    * @param resource - unused
    * @param pos - unused
    * @param file_length - unused
    */
   public VirtualBlob( byte[] bytes, int n_read, String resource,
                       long pos, long file_length )
      {
      super();
      throw new IllegalArgumentException( "VirtualBlobs can only be"
                   + " constructed from filenames, not file contents" );
      }

   }
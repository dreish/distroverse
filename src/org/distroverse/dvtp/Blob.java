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
import java.net.ProtocolException;

import org.distroverse.core.Util;

/**
 * The current maximum blob size is 65,536 bytes, though this limit
 * could easily be increased in the future if appropriate, up to 2^63
 * bytes.
 * @author dreish
 */
public class Blob implements DvtpExternalizable
   {
   /**
    * Does NOT take ownership of 'bytes'
    * @param bytes
    * @param n_read
    * @param resource
    * @param pos
    * @param file_length
    * @throws ProtocolException 
    */
   public Blob( byte[] bytes, int n_read, String resource,
                long pos, long file_length )
      {
      super();
      if ( bytes.length > 65536 )
         throw new IllegalArgumentException( "Blob above legal maximum"
                                             + " size" );
      if ( n_read == bytes.length )
         mBytes = bytes.clone();
      else
         {
         mBytes = new byte[ n_read ];
         System.arraycopy( bytes, 0, mBytes, 0, n_read );
         }
      mResource = new Str( resource );
      mPos = pos;
      mFileLength = file_length;
      }

   public Blob()
      {
      super();
      }

   public int getClassNumber()
      {  return 26;  }
   public byte[] getBytes()
      {  return mBytes;  }
   public Str getResource()
      {  return mResource;  }
   public long getPos()
      {  return mPos;  }
   public long getFileLength()
      {  return mFileLength;  }

   public void readExternal( InputStream in ) throws IOException
      {
      int bytes_len = Util.safeInt( CompactUlong.externalAsLong( in ) );
      if ( bytes_len > 65536 )
         throw new ProtocolException( "Blob above legal maximum size" );
      mBytes = new byte[ bytes_len ];
      if ( in.read( mBytes ) != bytes_len )
         throw new ProtocolException( "End of file while reading"
                                      + " Blob" );
      (mResource = new Str()).readExternal( in );
      mPos = CompactUlong.externalAsLong( in );
      mFileLength = CompactUlong.externalAsLong( in );
      }

   public void writeExternal( OutputStream out ) throws IOException
      {
      CompactUlong.longAsExternal( out, mBytes.length );
      out.write( mBytes );
      mResource.writeExternal( out );
      CompactUlong.longAsExternal( out, mPos );
      CompactUlong.longAsExternal( out, mFileLength );
      }
   
   public String prettyPrint()
      {
      return "(Blob " + Util.prettyPrintList( mBytes, mResource,
                                              mPos, mFileLength ) + ")";
      }

   private byte[] mBytes;
   private Str mResource;
   private long mPos;
   private long mFileLength;
   }

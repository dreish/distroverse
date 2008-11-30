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
import java.util.Arrays;

import org.distroverse.core.Util;

//immutable

/**
 * The current maximum blob size is 65,536 bytes, though this limit
 * could easily be increased in the future if appropriate, up to 2^63
 * bytes.  It is limited solely to discourage the use of huge message
 * objects.
 * @author dreish
 */
public class Blob implements DvtpExternalizable
   {
   /**
    * Copies, and does NOT take ownership of 'bytes'.
    * @param bytes
    * @param n_read
    * @param resource
    * @param pos - blob's position within the larger resource
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

   public Blob( InputStream in ) throws IOException
      {
      super();
      readExternal( in );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings( "unused" )
   private Blob()
      {
      super();
      }

   public int getClassNumber()
      {  return 25;  }

   @Override
   public boolean equals( Object o )
      {
      if ( o.getClass().equals( this.getClass() ) )
         {
         Blob b = (Blob) o;
         return (    mPos == b.mPos
                 &&  mFileLength == b.mFileLength
                 &&  mResource.equals( b.mResource )
                 &&  Arrays.equals( mBytes, b.mBytes ));
         }
      return false;
      }

   @Override
   public int hashCode()
      {
      return Arrays.hashCode( mBytes )
             ^ mResource.hashCode()
             ^ ((Long) mPos).hashCode()
             ^ ((Long) mFileLength).hashCode();
      }

   public byte[] getBytes()
      {  return mBytes;  }
   public Str getResource()
      {  return mResource;  }
   public long getPos()
      {  return mPos;  }
   public long getFileLength()
      {  return mFileLength;  }

   private void readExternal( InputStream in ) throws IOException
      {
      int bytes_len = Util.safeInt( ULong.externalAsLong( in ) );
      if ( bytes_len > 65536 )
         throw new ProtocolException( "Blob above legal maximum size" );
      mBytes = new byte[ bytes_len ];
      if ( in.read( mBytes ) != bytes_len )
         throw new ProtocolException( "End of file while reading"
                                      + " Blob" );
      mResource = new Str( in );
      mPos = ULong.externalAsLong( in );
      mFileLength = ULong.externalAsLong( in );
      }

   public void writeExternal( OutputStream out ) throws IOException
      {
      ULong.longAsExternal( out, mBytes.length );
      out.write( mBytes );
      mResource.writeExternal( out );
      ULong.longAsExternal( out, mPos );
      ULong.longAsExternal( out, mFileLength );
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

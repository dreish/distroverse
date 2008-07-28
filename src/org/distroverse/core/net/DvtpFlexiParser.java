/*
 * Copyright (c) 2007-2008 Dan Reish.
 *
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * General Public License (GPL) version 3 or later</a>
 */
package org.distroverse.core.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.distroverse.core.Log;
import org.distroverse.core.Util;
import org.distroverse.dvtp.CompactUlong;
import org.distroverse.dvtp.DvtpExternalizable;
import org.distroverse.dvtp.DvtpObject;

public class DvtpFlexiParser extends ObjectParser< Object >
   {
   private static final int MAX_OBJECT_LENGTH = 1024 * 1024 * 2;

   public DvtpFlexiParser( ByteBuffer b )
      {
      super( b );
      mNextObject = new ByteArrayOutputStream( 1024 );
      }

   /* (non-Javadoc)
    * @see org.distroverse.core.net.ObjectParser#parseObjects(java.io.ByteArrayOutputStream, org.distroverse.core.net.NetInQueue)
    */
   @Override
   protected void parseObjects( ByteArrayOutputStream baos,
                                NetInQueue< Object > queue )
   throws Exception
      {
      // FIXME This whole method is ugly.
      boolean cont = true;
      while ( cont )
         {
         mNextObject.write( baos.toByteArray() );
         baos.reset();
         byte[] next_object = mNextObject.toByteArray();

         if ( beginsWithNul( next_object ) )
            {
            // This is an arbitrary DvtpExternalizable object.  Find the
            // length and find out whether we have the whole thing yet.
            InputStream in = Util.baToInput( next_object );
            // Discard initial NUL.
            in.read();
            int ob_len = objectLength( in );
            boolean writeback = false;

            if ( ob_len == 0 )
               {
               throw new IOException( "zero-length object in input" );
               }
            else if ( ob_len <= in.available() )
               {
               // Convert and add to the queue.
               writeback = true;
               int before_length = in.available();
               DvtpExternalizable o = DvtpObject.parseObject( in );
               int after_length  = in.available();
               int actual_ob_len = before_length - after_length;
               if ( actual_ob_len != ob_len )
                  Log.p( "DVTP object length not as promised: was "
                         + actual_ob_len + ". not " + ob_len,
                         Log.DVTP, 5 );
               queue.add( o );
               }
            else
               cont = false;

            if ( writeback )
               {
               /* Delete parsed object from mNextObject by writing
                * whatever is left in 'in' back to mNextObject.
                */
               byte[] in_buffer = new byte[ in.available() ];
               in.read( in_buffer );
               mNextObject.reset();
               mNextObject.write( in_buffer );
               }
            }
         else
            {
            // This is a raw string.  Find out whether we have the
            // newline terminator yet.
            cont = false;
            for ( int i = 0; i < next_object.length - 1; ++i )
               if (    next_object[ i   ] == '\r'
                    && next_object[ i+1 ] == '\n' )
                  {
                  // Yes.
                  byte[] string_part = new byte[ i ];
                  System.arraycopy( next_object, 0, string_part, 0, i );
                  ByteArrayOutputStream string_baos
                     = new ByteArrayOutputStream();
                  string_baos.write( string_part );
                  queue.add( string_baos.toString( "UTF-8" ) );

                  byte[] remainder = new byte[ next_object.length
                                               - i - 2 ];
                  System.arraycopy( next_object, i + 2, remainder, 0,
                                    next_object.length - i - 2 );
                  mNextObject.reset();
                  mNextObject.write( remainder );

                  if ( remainder.length > 0 )
                     cont = true;
                  break;
                  }
            }
         }
      }

   private boolean beginsWithNul( byte[] ba )
      {
      return  ba.length > 0  &&  ba[ 0 ] == '\0';
      }

   /* Reminder: length will include the initial NUL, and the length
    * number itself.  Returns -1 if not enough has been ready to even
    * compute the length.
    */
   private int objectLength( InputStream in ) throws IOException
      {
      int ret = -1;

      try
         {
         ret = Util.safeInt( CompactUlong.externalAsLong( in ) );
         }
      catch ( IOException e )
         {
         ret = -1;
         }

      if ( ret > MAX_OBJECT_LENGTH )
         throw new IOException( "Object length " + ret + " exceeds"
                                + " limit " + MAX_OBJECT_LENGTH );

      return ret;
      }

   ByteArrayOutputStream mNextObject;
   }

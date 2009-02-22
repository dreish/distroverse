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
package org.distroverse.core.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.distroverse.core.Log;
import org.distroverse.core.Util;
import org.distroverse.dvtp.DvtpObject;
import org.distroverse.dvtp.ULong;
import org.distroverse.dvtp.DvtpExternalizable;

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
         ret = Util.safeInt( ULong.externalAsLong( in ) );
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

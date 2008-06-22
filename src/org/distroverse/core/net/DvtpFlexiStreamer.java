/*
 * Copyright (c) 2007-2008 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * General Public License (GPL) version 3 or later</a>
 */
package org.distroverse.core.net;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import org.distroverse.core.Log;
import org.distroverse.dvtp.CompactUlong;
import org.distroverse.dvtp.DvtpExternalizable;
import org.distroverse.dvtp.Str;

public class DvtpFlexiStreamer extends ObjectStreamer< Object >
   {
   public DvtpFlexiStreamer( ByteBuffer b )
      {
      super( b );
      }

   @Override
   synchronized protected void 
   streamNextObject( ByteArrayOutputStream baos,
                     NetOutQueue< Object > queue )
   throws Exception
      {
      synchronized ( queue )
         {
         Object next_object = null;
         if ( queue.size() > 0 )
            next_object = queue.remove();
         if ( next_object == null )
            return;

         if ( next_object instanceof String )
            {
            if ( safelyStreamableString( (String) next_object ) )
               {
               baos.write( ((String) next_object + "\r\n")
                           .getBytes( "UTF-8" ) );
               return;
               }
            next_object = new Str( (String) next_object );
            }
         
         if ( next_object instanceof DvtpExternalizable )
            {
            DvtpExternalizable no_de = (DvtpExternalizable) next_object;
            ByteArrayOutputStream externalized_object
               = new ByteArrayOutputStream();
            CompactUlong class_num 
               = new CompactUlong( no_de.getClassNumber() );
            class_num.writeExternal( externalized_object );
            no_de.writeExternal( externalized_object );
            
            // Write initial NUL to baos
            baos.write( 0 );
            // Write object length to baos
            CompactUlong object_length
               = new CompactUlong( externalized_object.size() );
            object_length.writeExternal( baos );
            /* Write object itself (already prefixed with class number)
             * to baos
             */
            externalized_object.writeTo( baos );
            }
         else
            {
            Log.p( "DvtpFlexiParser.streamObjects can't"
                   + " handle non-DVTP objects", Log.NET, 1 );
            throw new RuntimeException( "unimplemented object type" );
            }
         }
      }

   public static boolean safelyStreamableString( String s )
      {
      if ( s.charAt( 0 ) == '\0'
           ||  s.indexOf( "\r\n" ) != -1 )
         return false;
      return true;
      }
   }

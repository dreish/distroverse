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
package org.distroverse.core.net;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import org.distroverse.core.Log;
import org.distroverse.dvtp.ULong;
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
            ULong class_num 
               = new ULong( no_de.getClassNumber() );
            class_num.writeExternal( externalized_object );
            no_de.writeExternal( externalized_object );
            
            // Write initial NUL to baos
            baos.write( 0 );
            // Write object length to baos
            ULong object_length
               = new ULong( externalized_object.size() );
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

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
/**
 * Outputs each string given as a line terminated by CR-LF.
 */
package org.distroverse.core.net;

import java.io.ByteArrayOutputStream;
//import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * @author dreish
 *
 */
public class StringLineStreamer extends ObjectStreamer< String >
   {
   /**
    * @param b
    */
   public StringLineStreamer( ByteBuffer b )
      {
      super( b );
      }

   /* (non-Javadoc)
    * @see org.distroverse.core.ObjectStreamer#getNextObject(java.io.ByteArrayOutputStream, org.distroverse.core.NetOutQueue)
    */
   @Override
   synchronized protected void
   streamNextObject( ByteArrayOutputStream baos,
                     NetOutQueue< String > queue )
   throws Exception
      {
      synchronized ( queue )
         {
         if ( queue.size() > 0 )
            {
            String s = queue.remove();
            if ( s.indexOf( "\r\n" ) >= 0 )
               throw new IllegalArgumentException( "String containing"
                            + " a newline found in StringLineStreamer"
                            + " queue" );
            byte[] string_as_bytes = s.getBytes( "UTF-8" );
            baos.write( string_as_bytes );
            baos.write( "\r\n".getBytes( "UTF-8" ) );
            }
         }
      }
   }

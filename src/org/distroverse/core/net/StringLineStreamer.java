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

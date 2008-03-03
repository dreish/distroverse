package org.distroverse.core.net;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import org.distroverse.core.*;

public class DvtpFlexiStreamer extends ObjectStreamer< Object >
   {

   public DvtpFlexiStreamer( ByteBuffer b )
      {
      super( b );
      // TODO Auto-generated constructor stub
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
            {
            // Do nothing.
            }
         else if ( next_object instanceof String )
            {
            baos.write( ((String) next_object + "\r\n")
                        .getBytes( "UTF-8" ) );
            }
         else
            {
            Log.p( "DvtpFlexiParser.streamObjects can't"
                   + " yet handle non-string objects", Log.NET, 1 );
            throw new RuntimeException( "unimplemented object type" );
            }
         }
      }

   }

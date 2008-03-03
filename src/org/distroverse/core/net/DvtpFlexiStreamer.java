package org.distroverse.core.net;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class DvtpFlexiStreamer extends ObjectStreamer< Object >
   {

   public DvtpFlexiStreamer( ByteBuffer b )
      {
      super( b );
      // TODO Auto-generated constructor stub
      }

   @Override
   protected void streamNextObject( ByteArrayOutputStream baos,
                                    NetOutQueue< Object > queue )
   throws Exception
      {
      Object next_object = queue.remove();
      if ( next_object instanceof String )
         {
         baos.write( ((String) next_object).getBytes( "UTF-8" ) );
         }
      else
         {
         System.err.println( "DvtpFlexiParser.streamObjects can't yet"
                             + " handle non-string objects" );
         throw new RuntimeException( "unimplemented object type" );
         }
      }

   }

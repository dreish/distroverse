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
      // TODO Auto-generated method stub

      }

   }

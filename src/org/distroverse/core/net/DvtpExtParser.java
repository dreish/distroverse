package org.distroverse.core.net;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import org.distroverse.dvtp.DvtpExternalizable;

public class DvtpExtParser extends ObjectParser< DvtpExternalizable >
   {
   public DvtpExtParser( ByteBuffer b )
      {
      super( b );
      // TODO Auto-generated constructor stub
      }

   @Override
   protected void parseObjects( ByteArrayOutputStream baos,
                                NetInQueue< DvtpExternalizable > queue )
   throws Exception
      {
      // TODO Auto-generated method stub

      }

   }

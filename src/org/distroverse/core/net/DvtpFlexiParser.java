package org.distroverse.core.net;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import org.distroverse.core.*;

public class DvtpFlexiParser extends ObjectParser< Object >
   {

   public DvtpFlexiParser( ByteBuffer b )
      {
      super( b );
      mNextString = "";
      }

   @Override
   // TODO don't forget to remove this after implementing both sides
   @SuppressWarnings("all")
   protected void parseObjects( ByteArrayOutputStream baos,
                                NetInQueue< Object > queue )
   throws Exception
      {
      if ( beginsWithNul( baos ) )
         {
         Log.p( "DvtpFlexiParser.parseObjects can't yet"
                + " handle non-string objects", Log.NET, 1 );
         throw new RuntimeException( "unimplemented object format" );
         }
      else
         {
         /* This is especially bad for assuming that whatever comes
          * after the string will also be a string, but doing otherwise
          * turns out to be tricky enough that I don't want to bother
          * with it now.
          */
         final String to_add = baos.toString( "UTF-8" ); 
         for ( int i = 0; i < to_add.length(); ++i )
            {
            mNextString += to_add.charAt( i );
            if ( mNextString.length() > 1
                 &&  mNextString.substring( mNextString.length() - 2 ) 
                                .equals( "\r\n" ) )
               {
               queue.add( mNextString
                          .substring( 0, mNextString.length() - 2 ) );
               mNextString = "";
               }
            }
         baos.reset();
         }
      }

   private boolean beginsWithNul( ByteArrayOutputStream baos )
      {
      byte[] baos_contents = baos.toByteArray();
      return  baos_contents.length > 0
              &&  baos_contents[ 0 ] == '\0';
      }

   String mNextString;
   }

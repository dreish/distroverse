package org.distroverse.core.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.distroverse.core.*;
import org.distroverse.dvtp.CompactUlong;

public class DvtpFlexiParser extends ObjectParser< Object >
   {

   public DvtpFlexiParser( ByteBuffer b )
      {
      super( b );
      mNextObject = new ByteArrayOutputStream( 1024 );
      }

   @Override
   // TODO don't forget to remove this after implementing both sides
   @SuppressWarnings("all")
   protected void parseObjects( ByteArrayOutputStream baos,
                                NetInQueue< Object > queue )
   throws Exception
      {
      // FIXME This is a grossly suboptimal implementation, and
      // FIXME (second opinion) it's ugly too
      mNextObject.write( baos.toByteArray() );
      byte[] next_object = mNextObject.toByteArray();
      
      if ( beginsWithNul( next_object ) )
         {
         // This is an arbitrary DvtpExternalizable object.  Find the
         // length and find out whether we have the whole thing yet.
         int ob_len = objectLength( next_object );
         if ( ob_len > 0  &&  ob_len <= next_object.length )
            {
            // XXX Convert and add to the queue.
            
            // XXX Delete parsed object from mNextObject.
            }
         }
      else
         {
         // This is a raw string.  Find out whether we have the newline
         // terminator yet.
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
               baos.reset();
               baos.write( remainder );
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
   private int objectLength( byte[] object )
      {
      try
         {
         long len = CompactUlong.externalAsLong( 
                       Util.baToObjectInput( object ) );
         return Util.safeInt( len );
         }
      catch ( IOException e )
         {
         return -1;
         }
      }

   ByteArrayOutputStream mNextObject;
   }

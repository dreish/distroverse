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

public class StringLineParser extends ObjectParser< String >
   {
   /**
    * @param b
    */
   public StringLineParser( ByteBuffer b )
      {
      super( b );
      mNextString = "";
      }

   /* (non-Javadoc)
    * @see org.distroverse.core.ObjectParser#parseObjects(java.io.ByteArrayOutputStream, org.distroverse.core.NetInQueue)
    */
   @Override
   protected void parseObjects( ByteArrayOutputStream baos,
                                NetInQueue< String > queue )
   throws Exception
      {
      String to_add = baos.toString( "UTF-8" ); 
      for ( int i = 0; i < to_add.length(); ++i )
         {
         mNextString += to_add.charAt( i );
         if ( mNextString.substring( mNextString.length() - 2 ) 
              == "\r\n" )
            {
            queue.add( mNextString );
            mNextString = "";
            }
         }
      }
   
   String mNextString;
   }

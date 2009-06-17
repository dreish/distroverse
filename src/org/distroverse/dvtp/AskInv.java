/*
 * Copyright (c) 2007-2008 Dan Reish.
 *
 * For license details, see the file COPYING-L in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/lgpl.html">GNU
 * Lesser General Public License (LGPL) version 3 or later</a>
 */
package org.distroverse.dvtp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.distroverse.core.Util;

//immutable

/**
 * @author dreish
 *
 */
public class AskInv implements EnvoySendable
   {
   /**
    *
    */
   public AskInv( InputStream in )
   throws IOException, ClassNotFoundException
      {
      super();
      mType = Str.externalAsString( in );
      mKey  = DvtpObject.parseObject( in );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings( "unused" )
   private AskInv()
      {
      mType = null;
      mKey  = null;
      }

   public AskInv( String t, DvtpExternalizable k )
      {
      mType = t;
      mKey  = k;
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   public int getClassNumber()
      {  return 134;  }

   public DvtpExternalizable getKey()
      {  return mKey;  }

   @Override
   public boolean equals( Object o )
      {
      if ( o.getClass().equals( getClass() ) )
         {
         return ((AskInv) o).mType.equals( mType )
                && ((AskInv) o).mKey.equals( mKey );
         }
      return false;
      }

   @Override
   public int hashCode()
      {
      return mType.hashCode()
             ^ mKey.hashCode()
             ^ getClass().hashCode();
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#prettyPrint()
    */
   public String prettyPrint()
      {
      return "(AskInv "
             + Util.prettyPrintList( mType, mKey )
             + ")";
      }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#writeExternal(java.io.OutputStream)
    */
   public void writeExternal( OutputStream out ) throws IOException
      {
      Str.stringAsExternal( out, mType );
      DvtpObject.writeInnerObject( out, mKey );
      }

   private final String             mType;
   private final DvtpExternalizable mKey;
   }

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
import java.util.Arrays;

import org.distroverse.core.Util;

public class DList implements DvtpExternalizable
   {
   public DList()
      {
      super();
      mContents = null;
      }

   public DList( DvtpExternalizable[] f )
      {
      super();
      mContents = f;
      }

   public DList( DvtpExternalizable f )
      {
      super();
      mContents = new DvtpExternalizable[] { f };
      }

   public int getClassNumber()
      {  return 128;  }

   @Override
   public boolean equals( Object o )
      {
      return (o.getClass().equals( this.getClass() )
              && Arrays.equals( mContents, ((DList) o).mContents ));
      }

   @Override
   public int hashCode()
      {
      return Arrays.hashCode( mContents )
             ^ this.getClass().hashCode();
      }

   public int getContentsLength()
      {  return mContents.length;  }
   public DvtpExternalizable getContents( int n )
      {  return mContents[ n ];  }

   protected DvtpExternalizable[] getContents()
      {  return mContents;  }

   public void readExternal( InputStream in ) throws IOException,
                                             ClassNotFoundException
      {
      int length = Util.safeInt( ULong.externalAsLong( in ) );
      mContents = new DvtpExternalizable[ length ];
      for ( int i = 0; i < length; ++i )
         mContents[ i ] = DvtpObject.parseObject( in );
      }

   public void writeExternal( OutputStream out ) throws IOException
      {
      ULong.longAsExternal( out, mContents.length );
      for ( DvtpExternalizable o : mContents )
         DvtpObject.writeInnerObject( out, o );
      }

   public String prettyPrint()
      {
      return "(DList "
             + Util.prettyPrintList( (Object[]) mContents ) + ")";
      }

   private DvtpExternalizable[] mContents;
   }

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

//immutable

public final class DeleteObject implements ProxySendable
   {
   public DeleteObject( InputStream in ) throws IOException
      {
      super();
      readExternal( in );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   private DeleteObject()
      {
      // (Leaving ID null)
      }

   public DeleteObject( long id )
      {  mId = id;  }

   public int getClassNumber()
      {  return 17;  }

   @Override
   public boolean equals( Object o )
      {
      return (o instanceof DeleteObject
              &&  mId.equals( ((DeleteObject) o).mId ));
      }

   @Override
   public int hashCode()
      {
      return mId.hashCode() ^ DeleteObject.class.hashCode();
      }

   public Long getId()
      {  return mId;  }

   private void readExternal( InputStream in ) throws IOException
      {
      mId = ULong.externalAsLong( in );
      }

   public void writeExternal( OutputStream out ) throws IOException
      {
      ULong.longAsExternal( out, mId );
      }

   public String prettyPrint()
      {
      return "(DeleteObject " + mId + ")";
      }

   private Long mId;
   }

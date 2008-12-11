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
      mId = ULong.externalAsLong( in );
      }

   /*
    * Default constructor is disallowed and useless, since this is an
    * immutable class.
    */
   @SuppressWarnings("unused")
   private DeleteObject()
      {
      mId = 0;
      }

   public DeleteObject( long id )
      {  mId = id;  }

   public int getClassNumber()
      {  return 17;  }

   @Override
   public boolean equals( Object o )
      {
      return (o instanceof DeleteObject
              &&  mId == ((DeleteObject) o).mId);
      }

   @Override
   public int hashCode()
      {
      return (int) mId ^ DeleteObject.class.hashCode();
      }

   public Long getId()
      {  return mId;  }

   public void writeExternal( OutputStream out ) throws IOException
      {
      ULong.longAsExternal( out, mId );
      }

   public String prettyPrint()
      {
      return "(DeleteObject " + mId + ")";
      }

   private final long mId;
   }

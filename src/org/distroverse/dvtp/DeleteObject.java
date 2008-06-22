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

public final class DeleteObject implements ProxySendable
   {
   public int getClassNumber()
      {  return 17;  }

   public Long getId()
      {  return mId;  }

   public void readExternal( InputStream in ) throws IOException
      {
      mId = CompactUlong.externalAsLong( in );
      }

   public void writeExternal( OutputStream out ) throws IOException
      {
      CompactUlong.longAsExternal( out, mId );
      }

   public String prettyPrint()
      {
      return "(DeleteObject " + mId + ")";
      }

   private Long mId;
   }

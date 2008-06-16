/*
 * Copyright (c) 2007 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * Lesser General Public License (GPL) version 3 or later</a>
 */
package org.distroverse.dvtp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.distroverse.core.Util;

/**
 * A pair of DvtpExternalizable objects.  They get externalized
 * together.  No provision is made for skipping a pair without parsing
 * both objects.
 * @author dreish
 */
public class Pair implements DvtpExternalizable
   {
   /**
    * This default constructor is pretty much only useful in conjunction
    * with readExternal().
    */
   public Pair()
      {  mFirst = mSecond = null;  }
   
   public Pair( DvtpExternalizable first,
                DvtpExternalizable second )
      {
      mFirst = first;
      mSecond = second;
      }

   public Object getFirst()
      {  return mFirst;  }
   public Object getSecond()
      {  return mSecond;  }

   /* (non-Javadoc)
    * @see org.distroverse.dvtp.DvtpExternalizable#getClassNumber()
    */
   public int getClassNumber()
      {  return 1;  }

   /* (non-Javadoc)
    * @see java.io.Externalizable#readExternal(java.io.InputStream)
    */
   public void readExternal( InputStream in ) 
   throws IOException, ClassNotFoundException
      {
      mFirst  = DvtpObject.parseObject( in );
      mSecond = DvtpObject.parseObject( in );
      }

   /* (non-Javadoc)
    * @see java.io.Externalizable#writeExternal(java.io.OutputStream)
    */
   public void writeExternal( OutputStream out ) throws IOException
      {
      DvtpObject.writeObject( out, mFirst );
      DvtpObject.writeObject( out, mSecond );
      }

   public String prettyPrint()
      {
      return "(Pair " 
             + Util.prettyPrintList( mFirst, mSecond ) + ")";
      }
   
   private DvtpExternalizable mFirst;
   private DvtpExternalizable mSecond;
   }

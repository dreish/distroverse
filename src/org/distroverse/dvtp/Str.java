/*
 * Copyright (c) 2007 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * Lesser General Public License (GPL) version 3 or later</a>
 */
package org.distroverse.dvtp;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Str implements DvtpExternalizable
   {
   public Str( String s )
      {  mVal = s;  }

   public void readExternal( ObjectInput in ) throws IOException,
                                             ClassNotFoundException
      {
      // TODO Auto-generated method stub

      }

   public void writeExternal( ObjectOutput out ) throws IOException
      {
      // TODO Auto-generated method stub

      }

   String mVal;

   public BigInt getClassNumber()
      {
      // TODO Auto-generated method stub
      return null;
      }
   }

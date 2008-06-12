package org.distroverse.dvtp;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class ConPerm implements DvtpExternalizable
   {
   public ConPerm()
      {
      super();
      }

   public ConPerm( boolean b, String string )
      {
      // TODO Auto-generated constructor stub
      }

   public int getClassNumber()
      {
      // TODO Auto-generated method stub
      return 132;
      }
   
   // XXX need access methods

   public void readExternal( ObjectInput arg0 ) throws IOException,
                                               ClassNotFoundException
      {
      // XXX Auto-generated method stub

      }

   public void writeExternal( ObjectOutput arg0 ) throws IOException
      {
      // XXX Auto-generated method stub

      }

   }

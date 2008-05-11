package org.distroverse.dvtp;

import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * A superclass for the two Boolean types, so that they're actually
 * useful programmatically.  This class has no real relevance to the
 * specification of DVTP the protocol.
 * @author dreish
 */
public abstract class Bool implements DvtpExternalizable
   {
   public Bool()
      {  super();  }
   
   public static Bool newInstance( boolean b )
      {
      if ( b )
         return new True();
      return new False();
      }

   public abstract boolean asBoolean();

   public boolean isSendableByProxy()
      {  return false;  }

   public void readExternal( @SuppressWarnings("unused")
                             ObjectInput in )
      {  /* Do nothing. */  }
   public void writeExternal( @SuppressWarnings("unused")
                              ObjectOutput out )
      {  /* Do nothing. */  }
   }
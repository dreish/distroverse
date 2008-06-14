package org.distroverse.dvtp;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * This message is sent from a server to a client in response to a KNOCK
 * command, requesting permission to connect from a proxy serving a
 * given location URL.  The Bool indicates whether the proxy is allowed
 * to connect.  The regular expression, if not "0", must match all
 * location URLs that are allowed to connect.  If the regular expression
 * is the string "0" (which does not match any valid location URL), the
 * server will be queried for connection permission for each new proxy
 * that is loaded.
 * @author dreish
 */
public class ConPerm implements DvtpExternalizable
   {
   public ConPerm()
      {
      super();
      }

   public ConPerm( boolean may_connect, String permit_regexp )
      {
      mMayConnect = may_connect;
      mPermitRegexp = new Str( permit_regexp );
      }

   public ConPerm( Bool may_connect, Str permit_regexp )
      {
      mMayConnect = may_connect.asBoolean();
      mPermitRegexp = permit_regexp;
      }

   public int getClassNumber()
      {  return 132;  }
   
   public boolean getMayConnect()
      {  return mMayConnect;  }
   public Str getPermitRegexp()
      {  return mPermitRegexp;  }

   public void readExternal( ObjectInput in ) 
   throws IOException, ClassNotFoundException
      {
      mMayConnect = Bool.externalAsBoolean( in );
      (mPermitRegexp = new Str()).readExternal( in );
      }

   public void writeExternal( ObjectOutput out ) throws IOException
      {
      Bool.booleanAsExternal( out, mMayConnect );
      mPermitRegexp.writeExternal( out );
      }

   private boolean mMayConnect;
   private Str     mPermitRegexp;
   }

package org.distroverse.dvtp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
   
   public static boolean externalAsBoolean( InputStream in ) 
   throws IOException, ClassNotFoundException
      {
      DvtpExternalizable o = DvtpObject.parseObject( in );
      if ( o instanceof Bool )
         return ((Bool) o).asBoolean();
      throw new ClassNotFoundException( "Expected Bool subclass, got: "
                                  + o.getClass().getCanonicalName() );
      }
   
   public static void booleanAsExternal( OutputStream out, boolean b )
   throws IOException
      {
      DvtpObject.writeInnerObject( out, Bool.newInstance( b ) );
      }

   public void readExternal( @SuppressWarnings("unused")
                             InputStream in )
      {  /* Do nothing. */  }
   public void writeExternal( @SuppressWarnings("unused")
                              OutputStream out )
      {  /* Do nothing. */  }
   
   public String prettyPrint()
      {
      return "(Bool " + asBoolean() + ")";
      }
   }
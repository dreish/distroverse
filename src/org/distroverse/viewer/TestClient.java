package org.distroverse.viewer;

import java.io.IOException;

import org.distroverse.dvtp.DvtpExternalizable;

public class TestClient
   {
   /**
    * @param args
    */
   public static void main( String[] args )
      {
      try
         {
         DvtpServerConnection dsc
            = new DvtpServerConnection( "localhost" );
         pr( "Querying localhost: LOCATION dvtp://localhost/foo" );
         Object response = dsc.location( "dvtp://localhost/foo" );
         pr( "Response:" );
         pr( response.getClass().getCanonicalName() );
         pr( ((DvtpExternalizable) response).prettyPrint() );
         dsc.close();
         }
      catch ( IOException e )
         {
         e.printStackTrace();
         }
      }
   
   private static void pr( String s )
      {  System.out.println( s );  }
   }

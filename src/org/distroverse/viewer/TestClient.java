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
//         String query = "LOCATION dvtp://localhost/foo";
         String query = "GET drtp://localhost/HelloSimpleProxy.jar";
         pr( "Querying localhost: " + query );
         Object response = dsc.query( query );
         pr( "Response:" );
         pr( response.getClass().getCanonicalName() );
         pr( ((DvtpExternalizable) response).prettyPrint() );
         dsc.close();
         }
      catch ( IOException e )
         {
         e.printStackTrace();
         }
      catch ( ClassNotFoundException e )
         {
         e.printStackTrace();
         }
      }
   
   private static void pr( String s )
      {  System.out.println( s );  }
   }

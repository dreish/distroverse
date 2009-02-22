/*
 * <copyleft>
 *
 * Copyright 2007-2009 Dan Reish
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or
 * combining it with Clojure (or a modified version of that program)
 * or clojure-contrib (or a modified version of that library),
 * containing parts covered by the terms of the Eclipse Public
 * License, the licensors of this Program grant you additional
 * permission to convey the resulting work. {Corresponding Source for
 * a non-source form of such a combination shall include the source
 * code for the parts of Clojure and clojure-contrib used as well as
 * that of the covered work.}
 *
 * </copyleft>
 */
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
//         String query = "GET drtp://localhost/HelloSimpleProxy.jar";
         String query = "PROXYOPEN";
         pr( "Querying localhost: " + query );
         Object response = dsc.query( query );
         pr( "Response:" );
         pr( response.getClass().getCanonicalName() );
         pr( ((DvtpExternalizable) response).prettyPrint() );

         response = dsc.getObject();
         pr( "Response #2:" );
         pr( response.getClass().getCanonicalName() );
         pr( ((DvtpExternalizable) response).prettyPrint() );

         response = dsc.getObject();
         pr( "Response #3:" );
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

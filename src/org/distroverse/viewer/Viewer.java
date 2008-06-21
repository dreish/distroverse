/**
 * 
 */
package org.distroverse.viewer;
import org.distroverse.core.Log;

/**
 * @author dreish
 *
 */
public class Viewer
   {
   /**
    * @param args
    */
   public static void main( String[] args )
      {
      ViewerWindow w = new ViewerWindow();
      try
         {
         w.requestUrl( "dvtp://localhost/" );
         }
      catch ( Exception e )
         {
         Log.p( "Exception going to about:hello URL!", Log.CLIENT,
                50 );
         Log.p( e, Log.CLIENT, 50 );
         }
      }
   }

/*
 * Copyright (c) 2007-2008 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * General Public License (GPL) version 3 or later</a>
 */
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
         // w.requestUrl( "dvtp://localhost/" );
         w.requestUrl( "about:hello" );
         }
      catch ( Exception e )
         {
         Log.p( "Exception going to about:hello URL!", Log.CLIENT,
                50 );
         Log.p( e, Log.CLIENT, 50 );
         }
      }
   }

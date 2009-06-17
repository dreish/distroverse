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
/**
 *
 */
package org.distroverse.viewer;

import java.util.ArrayList;

import org.distroverse.distroplane.lib.BallFactory;
import org.distroverse.dvtp.Shape;
import org.distroverse.viewer.gui.DvWindow;

import com.jme.bounding.BoundingSphere;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Tube;

/**
 * This controller pipeline knows how to generate about:* views, which
 * do not use a envoy.
 * @author dreish
 */
public class AboutControllerPipeline extends ControllerPipeline
   {
   /**
    * Constructor adds shapes.  URL is ignored for now
    * TODO - add other about: urls
    * @param url
    * @param game
    * @param window
    */
   public AboutControllerPipeline( String url, ViewerWindow window )
      {
      // TODO: examine url and produce different results depending.
      WorldGraph wg = window.getWorld();
      wg.clear();
      // TODO: load from a file instead of using distroplane.lib.
      Shape s = new BallFactory()
                    .setNumRows( 30 )
                    .setEquatorialRadius( 2 )
                    .generate();
      wg.addShape( s, 1L, 0L,
                   VUtil.simpleMove( new Vector3f( 0, 0, -100 ),
                                     new Quaternion( 1, 0, 0, 0 ) ) );
      Shape simple = simpleShape();
      wg.addShape( simple, 2L, 0L,
                   VUtil.simpleMove( new Vector3f( 0, 0, 0 ),
                                     new Quaternion( 1, 0, 0, 0 ) ) );

      Tube t = new Tube("Tube", 2, 1, 3);
      t.setRandomColors();
      DvWindow.publicRoot.attachChild( t );
      }

   // Returns a single right triangle, for testing.
   private Shape simpleShape()
      {
      ArrayList< Vector3f > alp = new ArrayList< Vector3f >();
      alp.add( new Vector3f( 0, 0, 1 ) );
      alp.add( new Vector3f( 1, 0, 1 ) );
      alp.add( new Vector3f( 0, 1, 1 ) );
      int[] vc = { 3 };

      return new Shape( alp, vc );
      }

   @Override
   public void close()
      {
      // Don't need to do anything; there's no state to save.
      }
   }

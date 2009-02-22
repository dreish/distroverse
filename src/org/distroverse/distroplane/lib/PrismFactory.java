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
package org.distroverse.distroplane.lib;

import java.util.ArrayList;
import java.util.List;

import org.distroverse.dvtp.Shape;

import com.jme.math.Vector3f;

/**
 * Also good for generating tubes.  The default prism is rectangular.
 * @author dreish
 */
public class PrismFactory extends FrustoidFactory
   {
   public PrismFactory()
      {
      super();
      setSides( 4 );
      }

   @Override
   public Shape generate()
      {
      List< List< Vector3f > > points 
         = new ArrayList< List< Vector3f > >( 2 );
      points.add( genBase( new Vector3f( 0, 0, -getHeight() ) ) );
      points.add( genBase( new Vector3f( 0, 0, getHeight() ) ) );
      return generateSurface( points );
      }
   }

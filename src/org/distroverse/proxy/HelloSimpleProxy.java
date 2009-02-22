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
package org.distroverse.proxy;

import org.distroverse.distroplane.lib.PrimFactory;
import org.distroverse.distroplane.lib.PrimFactory.PrimShape;
import org.distroverse.dvtp.ClientSendable;

/**
 * A simple demo proxy that does not connect to any server.
 * @author dreish
 */
public class HelloSimpleProxy extends ProxyBase
   {
   public void offer( ClientSendable o )
      {
      // Client input is ignored.
      }

   public void run()
      {
      PrimFactory pf = new PrimFactory();
      pf.setDims( 10.0, 1.0, 1.0 );
      addObject( pf.setPrimShape( PrimShape.SPHERE  ).generate(), 1L,
                 -30.0f, 10.0f, 40.0f );
      addObject( pf.setPrimShape( PrimShape.PYRAMID ).generate(), 2L,
                 +00.0f, 10.0f, 40.0f );
      addObject( pf.setPrimShape( PrimShape.CUBOID  ).generate(), 3L,
                 +30.0f, 10.0f, 40.0f );
      }
   }

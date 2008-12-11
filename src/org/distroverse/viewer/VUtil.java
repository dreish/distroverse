/*
 * <copyleft>
 *
 * Copyright 2007-2008 Dan Reish
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
 * containing parts covered by the terms of the Common Public License,
 * the licensors of this Program grant you additional permission to
 * convey the resulting work. {Corresponding Source for a non-source
 * form of such a combination shall include the source code for the
 * parts of Clojure and clojure-contrib used as well as that of the
 * covered work.}
 *
 * </copyleft>
 */
package org.distroverse.viewer;

import org.distroverse.dvtp.Move;
import org.distroverse.dvtp.MoveSeq;
import org.distroverse.dvtp.Quat;
import org.distroverse.dvtp.Vec;
import org.distroverse.dvtp.MoveSeq.RepeatType;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

public final class VUtil
   {
   /**
    * Creates a simple stationary MoveSeq out of a vector and
    * quaternion.
    * @param pos - Position
    * @param rot - Rotation
    * @return
    */
   public static MoveSeq simpleMove( Vector3f pos,
                                     Quaternion rot )
      {
      Move init = Move.getNew( new Vec( pos ), new Quat( rot ) );
      Move[] seq = { init };
      return new MoveSeq( seq, RepeatType.LOOP );
      }

   public static float vecDist( Vector3f a, Vector3f b )
      {
      return (float) Math.sqrt(   (a.x - b.x) * (a.x - b.x)
                                + (a.y - b.y) * (a.y - b.y)
                                + (a.z - b.z) * (a.z - b.z) );
      }
   }

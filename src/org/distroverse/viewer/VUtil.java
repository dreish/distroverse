/*
 * Copyright (c) 2007-2008 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * General Public License (GPL) version 3 or later</a>
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
      Move init = new Move( new Vec( pos ), new Quat( rot ) );
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

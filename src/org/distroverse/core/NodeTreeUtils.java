package org.distroverse.core;

import org.distroverse.dvtp.DNode;
import org.distroverse.dvtp.DNodeRef;

import com.jme.math.Matrix4f;
import com.jme.math.Vector3f;

public class NodeTreeUtils
   {

   /**
    * Compute a vector to 'to' from 'from's frame of reference.  This is
    * essentially a transliteration of the Clojure function
    * node-tree/rel-vector, but with a hook for converting DNodeRefs to
    * DNodes by fetching them from the cache.
    * @param from
    * @param to
    * @return
    */
   public static Vector3f vectorTo( DNode from_arg, DNode to_arg )
      {
      DNode a = from_arg;
      DNode b = to_arg;

      Matrix4f a_to_root = new Matrix4f();
      Matrix4f root_to_b = new Matrix4f();

      while ( true )
         {
         if ( a == null  ||  b == null )
            throw new RuntimeException( "vectorTo called on two nodes"
                                        + " in unconnected trees" );
         if ( a.equals( b ) )
            {
            Matrix4f a_to_b = a_to_root.mult( root_to_b );
            return a_to_b.mult( new Vector3f() );
            }

         int a_depth = a.getDepth();
         int b_depth = b.getDepth();

         if ( a_depth < b_depth )
            {
            DNodeRef bp_ref = b.getParent();
            }
         }
      }

   }

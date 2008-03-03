/**
 * 
 */
package org.distroverse.dvtp;

import java.io.Serializable;
import javax.vecmath.Point3d;

/**
 * This class intentionally provides no encapsulation.  It is intended
 * to be nothing more than an array of Point3ds with a custom
 * serialization.  This is to ensure an unchanging concrete I/O
 * specification.
 * @author dreish
 *
 */
public class PointArray implements Serializable
   {
   /**
    * The exposed array of Point3ds.
    */
   public Point3d[] p;

   /**
    * Default constructor, setting p to null.
    */
   public PointArray()
      {  p = null;  }
   
   /**
    * Constructor with the size of the array.
    * @param n
    */
   public PointArray( int n )
      {  p = new Point3d[ n ];  }
   
   /**
    * Constructor with an existing array of Point3ds.
    * @param ap
    */
   public PointArray( Point3d[] ap )
      {  p = ap;  }
   
   // FIXME Implement PointArray.writeObject()
   // FIXME Implement PointArray.readObject()

   private static final long serialVersionUID = 1;
   }

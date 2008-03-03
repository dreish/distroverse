/**
 * 
 */
package org.distroverse.distroplane.lib;

//import org.distroverse.core.*;
import org.distroverse.dvtp.*;

/**
 * A deformer is an object that modifies a Shape in some way.
 * Typically it has methods to configure the exact deformation
 * it performs.
 * 
 * @author dreish
 *
 */
public abstract class Deformer
   {
   public abstract void deform( Shape s );
   }

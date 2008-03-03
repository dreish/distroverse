package org.distroverse.distroplane;

import org.distroverse.core.Shape;
import javax.vecmath.*;

// import org.distroverse.core.*;

public abstract class ShapeFactory extends Factory
   {

   @Override
   abstract public Shape Generate();
   
   protected Shape GenerateSurface( Point3d[][] vertices )
      {
      // TODO implement GenerateSurface()
      return null;
      }
   }

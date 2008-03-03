package org.distroverse.distroplane;

import org.distroverse.core.Shape;

// import org.distroverse.core.*;

public abstract class ShapeFactory extends Factory
   {

   @Override
   abstract public Shape Generate();
   }

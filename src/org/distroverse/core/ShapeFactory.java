package org.distroverse.core;

import org.distroverse.core.Shape;

public abstract class ShapeFactory extends Factory
   {

   @Override
   abstract public Shape Generate();
   }

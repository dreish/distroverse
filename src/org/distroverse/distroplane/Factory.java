package org.distroverse.distroplane;

interface Factory
   {
   /**
    * This parameterless method returns whatever kind of object
    * the subclass of this class makes, after any necessary
    * parameters have been configured through subclass-specific
    * methods.
    * @return An object, the type of which is to be determined by
    * the subclass.
    */
   public abstract Object generate();
   }

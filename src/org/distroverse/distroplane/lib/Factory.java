/*
 * Copyright (c) 2007-2008 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * General Public License (GPL) version 3 or later</a>
 */
package org.distroverse.distroplane.lib;

public interface Factory
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

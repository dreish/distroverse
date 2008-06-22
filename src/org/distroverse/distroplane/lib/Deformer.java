/*
 * Copyright (c) 2007-2008 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * General Public License (GPL) version 3 or later</a>
 */
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

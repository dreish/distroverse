/*
 * Copyright (c) 2007 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * Lesser General Public License (GPL) version 3 or later</a>
 */
package org.distroverse.dvtp;

import java.io.Serializable;
import java.nio.FloatBuffer;

import javax.vecmath.Point3d;

/**
 * @author dreish
 */
public class PointArray implements Serializable
   {
   /**
    * The exposed array of Point3ds.
    */
   private Point3d[] p;

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

   public FloatBuffer asFloatBuffer()
      {
      // TODO Auto-generated method stub
      return null;
      }
   }

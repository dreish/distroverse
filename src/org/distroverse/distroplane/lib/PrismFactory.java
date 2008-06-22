/*
 * Copyright (c) 2007-2008 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * General Public License (GPL) version 3 or later</a>
 */
package org.distroverse.distroplane.lib;

import java.util.ArrayList;
import java.util.List;

import org.distroverse.dvtp.Shape;

import com.jme.math.Vector3f;

/**
 * Also good for generating tubes.  The default prism is rectangular.
 * @author dreish
 */
public class PrismFactory extends FrustoidFactory
   {
   public PrismFactory()
      {
      super();
      setSides( 4 );
      }

   @Override
   public Shape generate()
      {
      List< List< Vector3f > > points 
         = new ArrayList< List< Vector3f > >( 2 );
      points.add( genBase( new Vector3f( 0, 0, -getHeight() ) ) );
      points.add( genBase( new Vector3f( 0, 0, getHeight() ) ) );
      return generateSurface( points );
      }
   }

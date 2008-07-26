/*
 * Copyright (c) 2007-2008 Dan Reish.
 *
 * For license details, see the file COPYING-L in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/lgpl.html">GNU
 * Lesser General Public License (LGPL) version 3 or later</a>
 */
package org.distroverse.dvtp;

/**
 * Truth.
 * @author dreish
 */
public class True extends Bool
   {
   public True()
      {  /* Do nothing. */  }

   public int getClassNumber()
      {  return 6;  }
   @Override
   public boolean asBoolean()
      {  return true;  }

   @Override
   public boolean equals( Object o )
      {
      return o instanceof True;
      }

   @Override
   public int hashCode()
      {
      return True.class.hashCode();
      }

   }

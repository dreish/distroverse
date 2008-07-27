/*
 * Copyright (c) 2007-2008 Dan Reish.
 *
 * For license details, see the file COPYING-L in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/lgpl.html">GNU
 * Lesser General Public License (LGPL) version 3 or later</a>
 */
package org.distroverse.dvtp;

/**
 * Falsity.
 * @author dreish
 */
public final class False extends Bool
   {
   public False()
      {  /* Do nothing. */  }

   public int getClassNumber()
      {  return 5;  }
   @Override
   public boolean asBoolean()
      {  return false;  }

   @Override
   public boolean equals( Object o )
      {
      return o instanceof False;
      }

   @Override
   public int hashCode()
      {
      return False.class.hashCode();
      }
   }

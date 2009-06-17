/*
 * Copyright (c) 2007-2008 Dan Reish.
 * 
 * For license details, see the file COPYING-L in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/lgpl.html">GNU
 * Lesser General Public License (LGPL) version 3 or later</a>
 */
package org.distroverse.viewer;

import java.io.IOException;

/**
 * Signals that a running envoy has done something erroneous, such as
 * telling the client to display a URL that it does not have access to.
 * @author dreish
 */
public class EnvoyErrorException extends IOException
   {
   private static final long serialVersionUID = 6465783313329842296L;

   public EnvoyErrorException()
      {
      super();
      }

   public EnvoyErrorException( String s )
      {
      super( s );
      }
   }

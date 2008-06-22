/*
 * Copyright (c) 2007-2008 Dan Reish.
 * 
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * General Public License (GPL) version 3 or later</a>
 */
package org.distroverse.viewer.gui;

import com.jme.scene.Node;

/**
 * @author dreish
 */
public class TextInputBar extends TextDisplayBar
   {
   public TextInputBar( Node parent )
      {  super( parent );  }
   public TextInputBar( Node parent, String init_text )
      {  super( parent, init_text );  }

   // TODO add some way to actually edit the text
   }

package org.distroverse.viewer.gui;

import com.jme.scene.Node;

public class TextDisplayBar extends Element
   {
   public TextDisplayBar( Node parent )
      {
      super( parent );
      mText = "";
      }
   
   public TextDisplayBar( Node parent, String init_text )
      {
      super( parent );
      mText = init_text;
      }

   /**
    * Sets the text of this TextDisplayBar, and updates the element in
    * the GUI, if this object is visible.
    * @param text
    */
   public void setText( String text ) 
      {
      mText = text;
      // TODO update some sort of graphical thingy
      }
   public String getText()  {  return mText;  }

   private String mText;
   }

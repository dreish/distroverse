/*
 * Copyright (c) 2007-2008 Dan Reish.
 *
 * For license details, see the file COPYING in your distribution,
 * or the <a href="http://www.gnu.org/copyleft/gpl.html">GNU
 * General Public License (GPL) version 3 or later</a>
 */
package org.distroverse.viewer.gui;

import java.util.concurrent.Callable;

import org.fenggui.TextEditor;

import com.jme.util.GameTaskQueueManager;

public class TextDisplayBar extends Element
   {
   public TextDisplayBar( final DvWindow w )
      {
      super( w );
      mText = "";
      addToWindow( w, 0, 0 );
      }

   public TextDisplayBar( final DvWindow w, String init_text )
      {
      super( w );
      mText = init_text;
      addToWindow( w, 0, 0 );
      }

   public TextDisplayBar( final DvWindow w, float x, float y,
                          String init_text )
      {
      super( w );
      mText = init_text;
      addToWindow( w, x, y );
      }

   /**
    * Sets the text of this TextDisplayBar, and updates the element in
    * the GUI, if this object is visible.
    * @param text
    */
   public void setText( String text )
      {
      mText = text;
      if ( mTextObject != null )
         mTextObject.setText( text );
      }
   public String getText()  {  return mText;  }

   /*
    * NB: ONLY to be called from a constructor, after initializing
    * mTextObject and mText!
    */
   private void addToWindow( final DvWindow w,
                             final float x, final float y )
      {
      mTextObject = new TextEditor( false );
      final String     text        = mText;
      final TextEditor text_object = mTextObject;

//      GameTaskQueueManager.getManager()
//                          .update( new Callable< Object >()
//         {
//         public Object call() throws Exception
//            {
            // tried to add TextArea here but get OpenGLException
//          TextArea ta = new TextArea( false );
            // FIXME Can addWidget go after the setup?
            w.addWidget( text_object );
            text_object.setText( text );
            text_object.setX( (int) x );
            text_object.setY( (int) y );
            text_object.setSize( text_object.getAppearance()
                                            .getFont()
                                            .getWidth( text_object
                                                       .getText() ),
                                 text_object.getAppearance()
                                            .getFont()
                                            .getHeight() );
//            return null;
//            }
//         } );
      }

   private String     mText;
   private TextEditor mTextObject;
   }

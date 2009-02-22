/*
 * <copyleft>
 *
 * Copyright 2007-2009 Dan Reish
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or
 * combining it with Clojure (or a modified version of that program)
 * or clojure-contrib (or a modified version of that library),
 * containing parts covered by the terms of the Eclipse Public
 * License, the licensors of this Program grant you additional
 * permission to convey the resulting work. {Corresponding Source for
 * a non-source form of such a combination shall include the source
 * code for the parts of Clojure and clojure-contrib used as well as
 * that of the covered work.}
 *
 * </copyleft>
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

package org.distroverse.viewer.gui;

import java.awt.Font;
import java.util.concurrent.Callable;

import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.util.GameTaskQueueManager;
import com.jmex.font3d.Font3D;
import com.jmex.font3d.JmeText;
import com.jmex.font3d.Text3D;

public class TextDisplayBar extends Element
   {
   public TextDisplayBar( final Node parent )
      {
      super( parent );
      mText = "";
      addToParent( parent );
      }
   
   public TextDisplayBar( final Node parent, String init_text )
      {
      super( parent );
      mText = init_text;
      addToParent( parent );
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
   
   public void setTextObject( JmeText text_object )
      {  mTextObject = text_object;  }
   
   private void addToParent( final Node parent )
      {
      GameTaskQueueManager.getManager()
                          .update( new Callable< Object >()
         {
         public Object call() throws Exception
            {
            Font3D font 
               = new Font3D( new Font( "Arial", Font.PLAIN, 24 ),
                             0.001f, true, true, true );
            Text3D text = font.createText( getText(), 50.0f, 0 );
            setTextObject( text );
            text.setLocalScale( new Vector3f( 50.0f, 50.0f, 0.01f ) );
            parent.attachChild( text );
            return null;
            }
         } );
      }

   private String  mText;
   private JmeText mTextObject;
   }

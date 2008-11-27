/*
 * <copyleft>
 *
 * Copyright 2007-2008 Dan Reish
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
 * containing parts covered by the terms of the Common Public License,
 * the licensors of this Program grant you additional permission to
 * convey the resulting work. {Corresponding Source for a non-source
 * form of such a combination shall include the source code for the
 * parts of Clojure and clojure-contrib used as well as that of the
 * covered work.}
 *
 * </copyleft>
 */
package org.distroverse.viewer.gui;

import org.fenggui.Display;
import org.fenggui.event.Key;
import org.fenggui.event.mouse.MouseButton;
import org.lwjgl.input.Keyboard;

import com.jme.input.InputHandler;
import com.jme.input.MouseInput;
import com.jme.input.MouseInputListener;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;

/**
 * FengJMEInputHandler
 *
 * @author Joshua Keplinger
 * Minor changes by dreish.
 */
public class FengJMEInputHandler extends InputHandler
   {
   public  Display        mDisp;
   private KeyInputAction mKeyAction;

   public  boolean        mKeyHandled;
   public  boolean        mMouseHandled;

   public FengJMEInputHandler( Display disp )
      {
      mDisp = disp;

      mKeyAction = new KeyAction();
      addAction( mKeyAction, DEVICE_KEYBOARD, BUTTON_ALL, AXIS_NONE,
                 false );

      MouseInput.get().addListener( new MouseListener() );
      }

   @Override
   public void update( float time )
      {
      mKeyHandled = false;
      mMouseHandled = false;
      super.update( time );
      }

   public boolean wasKeyHandled()
      {
      return mKeyHandled;
      }

   public boolean wasMouseHandled()
      {
      return mMouseHandled;
      }

   public class KeyAction extends KeyInputAction
      {
      public void performAction( InputActionEvent evt )
         {
         char character = evt.getTriggerCharacter();
         Key key = mapKeyEvent();
         if ( evt.getTriggerPressed() )
            {
            mKeyHandled = mDisp.fireKeyPressedEvent( character, key );
            // Bug workaround see note after code
            if ( key == Key.LETTER || key == Key.DIGIT )
               mKeyHandled = mDisp.fireKeyTypedEvent( character );
            }
         else
            mKeyHandled = mDisp.fireKeyReleasedEvent( character, key );
         }

      /**
       * Helper method that maps LWJGL key events to FengGUI.  Question:
       * How is this in any way reentrant?
       * @return The Key enumeration of the last key pressed.
       */
      private Key mapKeyEvent()
         {
         Key key_class;

         switch ( Keyboard.getEventKey() )
            {
            case Keyboard.KEY_BACK:
               key_class = Key.BACKSPACE;
               break;
            case Keyboard.KEY_RETURN:
               key_class = Key.ENTER;
               break;
            case Keyboard.KEY_DELETE:
               key_class = Key.DELETE;
               break;
            case Keyboard.KEY_UP:
               key_class = Key.UP;
               break;
            case Keyboard.KEY_RIGHT:
               key_class = Key.RIGHT;
               break;
            case Keyboard.KEY_LEFT:
               key_class = Key.LEFT;
               break;
            case Keyboard.KEY_DOWN:
               key_class = Key.DOWN;
               break;
            case Keyboard.KEY_SCROLL:
               key_class = Key.SHIFT;
               break;
            case Keyboard.KEY_LMENU:
               key_class = Key.ALT;
               break;
            case Keyboard.KEY_RMENU:
               key_class = Key.ALT;
               break;
            case Keyboard.KEY_LCONTROL:
               key_class = Key.CTRL;
               break;
            case Keyboard.KEY_RSHIFT:
               key_class = Key.SHIFT;
               break;
            case Keyboard.KEY_LSHIFT:
               key_class = Key.SHIFT;
               break;
            case Keyboard.KEY_RCONTROL:
               key_class = Key.CTRL;
               break;
            case Keyboard.KEY_INSERT:
               key_class = Key.INSERT;
               break;
            case Keyboard.KEY_F12:
               key_class = Key.F12;
               break;
            case Keyboard.KEY_F11:
               key_class = Key.F11;
               break;
            case Keyboard.KEY_F10:
               key_class = Key.F10;
               break;
            case Keyboard.KEY_F9:
               key_class = Key.F9;
               break;
            case Keyboard.KEY_F8:
               key_class = Key.F8;
               break;
            case Keyboard.KEY_F7:
               key_class = Key.F7;
               break;
            case Keyboard.KEY_F6:
               key_class = Key.F6;
               break;
            case Keyboard.KEY_F5:
               key_class = Key.F5;
               break;
            case Keyboard.KEY_F4:
               key_class = Key.F4;
               break;
            case Keyboard.KEY_F3:
               key_class = Key.F3;
               break;
            case Keyboard.KEY_F2:
               key_class = Key.F2;
               break;
            case Keyboard.KEY_F1:
               key_class = Key.F1;
               break;
            default:
               char ec = Keyboard.getEventCharacter();
               if ( ec >= '0' && ec <= '9' )
                  key_class = Key.DIGIT;
               else if ( (ec >= 'A' && ec <= 'Z')
                         || (ec >= 'a' && ec <= 'z') )
                  key_class = Key.LETTER;
               else
                  key_class = null;
               break;
            }

         return key_class;
         }

      }

   public class MouseListener implements MouseInputListener
      {

      private boolean down;
      private int     lastButton;

      public void onButton( int button, boolean pressed, int x, int y )
         {
         down = pressed;
         lastButton = button;
         if ( pressed )
            mMouseHandled = mDisp.fireMousePressedEvent(
                              x, y, getMouseButton( button ), 1 );
         else
            mMouseHandled = mDisp.fireMouseReleasedEvent(
                              x, y, getMouseButton( button ), 1 );
         }

      public void onMove( int xDelta, int yDelta, int newX, int newY )
         {
         // If the button is down, the mouse is being dragged
         if ( down )
            mMouseHandled = mDisp.fireMouseDraggedEvent(
                             newX, newY, getMouseButton( lastButton ) );
         else
            mMouseHandled = mDisp.fireMouseMovedEvent( newX, newY );
         }

      public void onWheel( int wheelDelta, int x, int y )
         {
         // wheelDelta is positive if the mouse wheel rolls up
         if ( wheelDelta > 0 )
            mMouseHandled = mDisp.fireMouseWheel( x, y, true,
                                                wheelDelta );
         else
            mMouseHandled = mDisp.fireMouseWheel( x, y, false,
                                                wheelDelta );

         /* note (johannes): wheeling code not tested on jME, please
          * report problems on www.fenggui.org/forum/
          */
         }

      /**
       * Helper method that maps the mouse button to the equivalent
       * FengGUI MouseButton enumeration.
       * @param button The button pressed or released.
       * @return The FengGUI MouseButton enumeration matching the
       * button.
       */
      private MouseButton getMouseButton( int button )
         {
         switch ( button )
            {
            case 0:
               return MouseButton.LEFT;
            case 1:
               return MouseButton.RIGHT;
            case 2:
               return MouseButton.MIDDLE;
            default:
               return MouseButton.LEFT;
            }
         }
      }
   }

package org.distroverse.dvtp;

public class KeyDown extends Keystroke
   {
   public KeyDown()
      {  super();  }

   public KeyDown( int kn )
      {  super( kn );  }

   @Override
   public int getClassNumber()
      {  return 20;  }
   }

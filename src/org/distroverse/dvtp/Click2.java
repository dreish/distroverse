package org.distroverse.dvtp;

/**
 * A mouseclick with the secondary mouse button, or a two-fingered
 * touch.  Typically calls a context-dependent menu, so "force" may not
 * be relevant in most cases, but it's available.
 * @author dreish
 */
public class Click2 extends Click
   {
   public Click2()
      {  super();  }
   
   public Click2( Vec dir, Flo force )
      {  super( dir, force );  }

   @Override
   public int getClassNumber()
      {  return 23;  }

   @Override
   public String prettyPrint()
      {
      // XXX don't do this
      return super.prettyPrint().replaceFirst( " ", "2 " );
      }
   }

package org.distroverse.viewer;

import java.io.IOException;

/**
 * Signals that a running proxy has done something erroneous, such as
 * telling the client to display a URL that it does not have access to.
 * @author dreish
 */
public class ProxyErrorException extends IOException
   {
   private static final long serialVersionUID = 6465783313329842296L;

   public ProxyErrorException()
      {
      super();
      }

   public ProxyErrorException( String s )
      {
      super( s );
      }
   }

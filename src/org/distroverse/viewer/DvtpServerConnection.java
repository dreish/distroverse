package org.distroverse.viewer;

import java.net.URI;

public class DvtpServerConnection
   {
   /**
    * Construct a new connection to a DVTP server, getting the hostname
    * from a URL.
    * @param url
    */
   public DvtpServerConnection( URI u )
      {
      String hostname = u.getHost();
      }

   }

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
/**
 * 
 */
package org.distroverse.distroplane.lib;

import org.distroverse.core.net.NetInQueueWatcher;

/**
 * A DvtpListener's serve() method opens a server socket, listens for
 * incoming connections, receives DVTP commands and sends responses as
 * defined by a DvtpServer.
 * @author dreish
 */
public interface DvtpListener
   {
   /**
    * Waits for incoming connections and handles them.  Does not return
    * unless the server aborts for some reason.
    */
   public abstract void serve();

   public DvtpServer getServer();

   public void setServer( DvtpServer server );

   public void setWatcher( NetInQueueWatcher< Object > watcher_thread );

   public void setGreeting( Object greeting );

//   public void addSocket( SocketChannel client ) throws IOException,
//                                          ClosedChannelException;
   }

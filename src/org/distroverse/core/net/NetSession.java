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
package org.distroverse.core.net;

/**
 * Bundles an input queue and an output queue of some type, and holds
 * arbitrary state information about the session in the form of a
 * generic attachment.
 * @author dreish
 * @param <T>
 */
public class NetSession< T >
   {
   public NetSession( NetInQueue < T > niqs,
                      NetOutQueue< T > noqs )
      {
      super();
      niqs.setSession( this );
      noqs.setSession( this );
      mNetInQueue  = niqs;
      mNetOutQueue = noqs;
      mProxyMode = false;
      }

   public NetInQueue< T > getNetInQueue()
      {  return mNetInQueue;  }
   public NetOutQueue< T > getNetOutQueue()
      {  return mNetOutQueue;  }
   public void setProxyMode()
      {  mProxyMode = true;  }
   public boolean inProxyMode()
      {  return mProxyMode;  }
   public < AT > AT setAttachment( Class< AT > attachment_class,
                                   AT attachment )
      {
      mAttachmentClass = attachment_class;
      mAttachment      = attachment;
      return attachment;
      }

   /**
    * Returns an object of class attachment_class if this NetSession has
    * such an object attached to it.  Otherwise, if this NetSession has
    * nothing attached to it at all, or has an object of a class other
    * than attachment_class attached to it, this method returns null.
    * Never throws an exception.
    * @param <AT> - Type of attachment_class; inferred
    * @param attachment_class - Class to look for
    * @return - The object attached to this NetSession
    */
   @SuppressWarnings("unchecked")
   public < AT > AT getAttachmentOrNull( Class< AT > attachment_class )
      {
      // This is type-checking enough:
      if ( mAttachment != null
           &&  attachment_class.isAssignableFrom( mAttachmentClass ) )
         return (AT) mAttachment;
      return null;
      }

   /**
    * Old-skool attachment getter, for people who are set in their ways.
    * You should probably use getAttachmentOrNull().
    * @return -
    */
   public Object getAttachment()
      {  return mAttachment;  }

   /**
    * Close the socket associated with this connection
    */
   public void close()
      {
      // FIXME Auto-generated method stub

      }

   private NetInQueue< T >  mNetInQueue;
   private NetOutQueue< T > mNetOutQueue;
   private Object           mAttachment;
   private Class< ? >       mAttachmentClass;
   private boolean          mProxyMode;
   }

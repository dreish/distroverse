package org.distroverse.viewer;

/**
 * This is the beloved browser cache.  Remote files are often downloaded
 * into the cache and then used locally.
 * TODO implement cache expiration
 * TODO store a URL-to-filename map in a file and use shorter filenames
 * @author dreish
 */
public abstract class ResourceCache
   {
   /**
    * Returns a local file URL for the requested resource, downloading
    * the resource if necessary, and counting as a hit for the purposes
    * of expiring the resource from the cache if not.  Theoretically, if
    * the cache is expiring very rapidly, it is possible files could be
    * deleted from the cache before they are used.
    * @param url - remote URL
    * @return local file:// URL
    */
   public static final String internalizeResourceUrl( String url )
      {
      checkInit();
      synchronized ( mCanDelete )
         {  mCanDelete = false;  }
      
      String ret = localizeUrl( url );
      if ( fileUrlExists( ret ) )
         registerUse( ret );
      else
         downloadUrl( url, ret );
      mCanDelete = true;

      return ret;
      }
   
   /**
    * Returns true if the given remote URL is locally cached, and counts
    * as a hit for the purposes of expiring the resource from the cache.
    * @param url - remote URL
    * @return is it cached?
    */
   public static final boolean checkResourceUrl( String url )
      {
      checkInit();
      synchronized ( mCanDelete )
         {  mCanDelete = false;  }
   
      String local_url = localizeUrl( url );
      boolean ret = false;
      if ( fileUrlExists( local_url ) )
         {
         registerUse( local_url );
         ret = true;
         }
      mCanDelete = true;
      
      return ret;
      }

   private static void checkInit()
      {
      if ( ! mInitialized )
         {
         mCanDelete = true;
         mInitialized = true;
         }
      }

   private static String localizeUrl( String url )
      {
      // FIXME This hardcoded directory obviously bogus and stupid
      // FIXME This set of escaped chars is not nearly portable
      return "/Users/dreish/.dv/cache/"
             + url.replaceAll( "_", "__" )
                  .replaceAll( "/", "_%" )
                  .replaceAll( ":", "_." );
      }

   private static boolean fileUrlExists( String ret )
      {
      // XXX Auto-generated method stub
      return false;
      }

   private static void registerUse( String ret )
      {
      // XXX Auto-generated method stub
      
      }

   private static void downloadUrl( String url, String ret )
      {
      // XXX Auto-generated method stub
      
      }

   private static boolean mInitialized = false;
   private static Boolean mCanDelete;
   }

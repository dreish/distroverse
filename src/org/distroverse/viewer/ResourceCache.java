package org.distroverse.viewer;

public class ResourceCache
   {
   /**
    * Singleton: only the static methods are allowed to construct.
    */
   private ResourceCache()
      {
      // TODO Anything here?
      }
   
   /**
    * Returns a local file URL for the requested resource, downloading
    * the resource if necessary.
    * @param url - remote URL
    * @return local file:// URL
    */
   public static String internalizeResourceUrl( String url )
      {
      checkSingleton();
      return mImpl.iInternalizeResourceUrl( url );
      }
   
   /**
    * Returns true if the given remote URL is locally cached.
    * @param url - remote URL
    * @return is it cached?
    */
   public static boolean checkResourceUrl( String url )
      {
      checkSingleton();
      return mImpl.iCheckResourceUrl( url );
      }

   private String iInternalizeResourceUrl( String url )
      {
      // TODO Auto-generated method stub
      return null;
      }

   private boolean iCheckResourceUrl( String url )
      {
      // TODO Auto-generated method stub
      return false;
      }

   private static void checkSingleton()
      {
      if ( mImpl == null )
         mImpl = new ResourceCache();
      }

   private static ResourceCache mImpl;
   }

/* <copyleft>
 *
 * Copyright 2008 Dan Reish
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
 * combining it with clojure-contrib (or a modified version of that
 * library), containing parts covered by the terms of the Common
 * Public License, the licensors of this Program grant you additional
 * permission to convey the resulting work. {Corresponding Source for
 * a non-source form of such a combination shall include the source
 * code for the parts of clojure-contrib used as well as that of the
 * covered work.}
 *
 * </copyleft>
 */
package org.distroverse.core;

/**
 * @author dreish
 * An immutable self-shrinking linear feedback shift register PRNG.
 * This has better statistical properties than a linear congruential
 * PRNG, and requires orders of magnitude less state than a Mersenne
 * twister.  It has a period of roughly 2^62.  The exact period is not
 * known.
 * 
 * For anyone who has trouble reading Wikipedia articles, THIS IS STILL
 * NOT SUITABLE FOR CRYPTOGRAPHY because it is relatively vulnerable to
 * a clock attack and the feedback polynomial is not secret.
 * 
 * This implementation is immutable.  State is changed by returning a
 * new object.
 * 
 * I am hardcoding taps for a maximum-cycle 64-bit PRNG.  The cycle
 * length is roughly 1/4 that of a non-shrinking LFSR.
 */
public final class PRNGFeedback
   {
   /**
    * Initializes a new {@link PRNGFeedback} generator.  Note that only
    * the low 63 bits of the seed are used.  The high (sign) bit is
    * ignored.
    * @param seed
    */
   public PRNGFeedback( long seed )
      {
      long register = seed | (1 << 63);

      /* From looking at streams generated with some small seeds, the
       * first 104 to 156 output bits appear to have too many zeros.
       */ 
      for ( int i = 0; i < 156; ++i )
         {
         register = nextRegister( register );
         while ( (register & 1) == 0 )
            {
            register = nextRegister( register );
            register = nextRegister( register );
            }
         register = nextRegister( register );
         }

      mRegister = register;
      mCollectedBits = 0;
      mNumCollectedBits = 0;
      }

   /**
    * NB: This produces a DIFFERENT stream from the above constructor,
    * since it does not advance the sequence.
    * @param seed
    * @param collected_bits
    * @param n_bits
    */
   private PRNGFeedback( long seed, long collected_bits, int n_bits )
      {
      mRegister = seed;
      mCollectedBits = collected_bits;
      mNumCollectedBits = n_bits;
      }
   
   public PRNGFeedback advance( int n_bits )
      {
      long collected_bits = 0;
      long register       = mRegister;
      
      for ( int i = 0; i < n_bits; ++i )
         {
         register = nextRegister( register );
         while ( (register & 1) == 0 )
            {
            register = nextRegister( register );
            register = nextRegister( register );
            }
         register = nextRegister( register );
         collected_bits |= ((register & 1) << i); 
         }
      
      return new PRNGFeedback( register, collected_bits, n_bits );
      }
   
   public int getNumCollectedBits()
      {  return mNumCollectedBits;  }
   public long getCollectedBits()
      {  return mCollectedBits;  }
   public long getRegister()
      {  return mRegister;  }
   
   public static long nextRegister( long register )
      {
      long bit = (   (register & TAP_0)
                  ^ ((register & TAP_1) >> 1)
                  ^ ((register & TAP_3) >> 3)
                  ^ ((register & TAP_4) >> 4));
      return (register >>> 1) | (bit << 63);
      }
   
   private static final long TAP_0 = 1L;
   private static final long TAP_1 = 1L << 1;
   private static final long TAP_3 = 1L << 3;
   private static final long TAP_4 = 1L << 4;
   
   private final long mRegister;
   private final long mCollectedBits;
   private final int  mNumCollectedBits;
   }

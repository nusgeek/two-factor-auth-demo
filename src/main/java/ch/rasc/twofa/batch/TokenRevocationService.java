/*
 *                        SSG Public License Notice
 *
 *   This software is the intellectual property of SSG. The program
 *   may be used only in accordance with the terms of the license agreement you
 *   entered into with SSG.
 *
 *   2019 SkillsFuture Singapore (SSG). All rights reserved.
 *   1 Marina Boulevard
 *   #18-01 One Marina Boulevard
 *   Singapore 018989
 */

package ch.rasc.twofa.batch;

/**
 * Token revocation service
 *
 * @author XingJun
 */
public interface TokenRevocationService
{
    /**
     * Revoke tokens of a consent by sending request to auth server.
     *
     * @param consentId
     * @return TokenRevocationResult
     */
    TokenRevocationResult revokeTokensByConsentId( String consentId, String clientId, String clientSecret );
    
    /**
     * Token revocation result model.
     */
    class TokenRevocationResult
    {
        private Result result;
        private String prev;
        private String next;
        
        /**
         * Construct a new instance.
         */
        public TokenRevocationResult()
        {
            this.result = null;
            this.prev = null;
            this.next = null;
        }
        
        /**
         * Get prev string of tokens.
         *
         * @return String
         */
        public String getPrev()
        {
            return prev != null ? prev : "";
        }
        
        /**
         * Set prev string of tokens.
         *
         * @param prev
         */
        public void setPrev( String prev )
        {
            this.prev = prev != null ? prev : "";
        }
        
        /**
         * Get next string of tokens.
         *
         * @return String
         */
        public String getNext()
        {
            return next != null ? next : "";
        }
        
        /**
         * Set next string of tokens.
         *
         * @param next
         */
        public void setNext( String next )
        {
            this.next = next != null ? next : "";
        }
        
        /**
         * Get result type.
         *
         * @return Result
         */
        public Result getResult()
        {
            return result;
        }
        
        /**
         * Set result type.
         *
         * @param result
         */
        public void setResult( Result result )
        {
            this.result = result;
        }
        
        /**
         * Check if result type is "SUCCESS".
         *
         * @return Boolean
         */
        public boolean hasError()
        {
            return result != Result.SUCCESS;
        }
        
        /**
         * Enum type of result.
         */
        public enum Result
        {
            SUCCESS,
            TOKEN_REVOKE_FAIL
        }
    }
}

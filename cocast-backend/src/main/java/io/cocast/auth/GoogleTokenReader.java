package io.cocast.auth;

import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

public class GoogleTokenReader {

    private static final String GOOGLE_VALIDATION_ACCESS_POINT = "https://www.googleapis.com/oauth2/v1/tokeninfo";

    public GoogleTokenData readToken(String tokenString) throws AuthenticationException {

        GoogleTokenData payload = new GoogleTokenData();

        Client client = ClientBuilder.newClient().register(JacksonFeature.class);

        try {
            payload = client.target(GOOGLE_VALIDATION_ACCESS_POINT).queryParam("access_token", tokenString)
                    .request().get(GoogleTokenData.class);
        } catch (BadRequestException exc) {
            throw new AuthenticationException(403, "Invalid Google Access Token. Expired?", exc);
        }

        return payload;
    }

    public static class GoogleTokenData {

        private String issued_to;
        private String audience;
        private String user_id;
        private String scope;
        private Integer expires_in;
        private String email;
        private Boolean verified_email;
        private String access_type;

        GoogleTokenData() {
        }

        public String getIssued_to() {
            return issued_to;
        }

        public void setIssued_to(String issuedTo) {
            this.issued_to = issuedTo;
        }

        public String getAudience() {
            return audience;
        }

        public void setAudience(String audience) {
            this.audience = audience;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }

        public Integer getExpires_in() {
            return expires_in;
        }

        public void setExpires_in(Integer expires_in) {
            this.expires_in = expires_in;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Boolean getVerified_email() {
            return verified_email;
        }

        public void setVerified_email(Boolean verified_email) {
            this.verified_email = verified_email;
        }

        public String getAccess_type() {
            return access_type;
        }

        public void setAccess_type(String accessType) {
            this.access_type = accessType;
        }
    }
}


package io.cocast.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.cocast.auth.AuthenticationException;
import io.cocast.auth.SecurityClaims;
import io.cocast.auth.SecurityContext;
import io.cocast.config.BasicBackendModule;
import io.cocast.util.DateUtils;
import org.junit.Before;

/**
 * Basic class for all unit tests
 */
public class BaseTest {

    private Injector injector;

    @Before
    public void setUpParent() throws AuthenticationException {
        this.injector = Guice.createInjector(new BasicBackendModule());
        setSecurityContext();
    }

    protected <T> T getInstance(Class<T> type) {
        return this.injector.getInstance(type);
    }

    protected void setSecurityContext() throws AuthenticationException {
        SecurityContext.set(new SecurityContext(this.createMockSecurityClaims()));
    }

    protected SecurityClaims createMockSecurityClaims() {
        SecurityClaims claims = new SecurityClaims("co-cast");
        claims.setEmail("viveiros@ciandt.com");
        claims.setName("Daniel Viveiros");
        claims.setSubject("google:118239183782204424177");
        claims.setIssuedAt(DateUtils.now());
        claims.setExpirationTime(DateUtils.eternity());

        return claims;
    }
}

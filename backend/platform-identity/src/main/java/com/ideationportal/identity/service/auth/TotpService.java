package com.ideationportal.identity.service.auth;

import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import org.springframework.stereotype.Component;

/**
 * TOTP (RFC 6238) MFA support (BR-U1-07/09). Secrets are generated here and stored encrypted
 * by the caller; never logged.
 */
@Component
public class TotpService {

    private final SecretGenerator secretGenerator = new DefaultSecretGenerator();
    private final CodeVerifier codeVerifier =
            new DefaultCodeVerifier(new DefaultCodeGenerator(), new SystemTimeProvider());

    public String generateSecret() {
        return secretGenerator.generate();
    }

    public boolean verify(String secret, String code) {
        if (secret == null || code == null) {
            return false;
        }
        return codeVerifier.isValidCode(secret, code);
    }
}

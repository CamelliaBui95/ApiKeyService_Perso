package fr.btn.utils;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import java.util.Base64;

public class Argon2 {
    private static final int SALT_LENGTH = 16;
    private static final int KEY_LENGTH = 10;
    private static final int ITERATIONS = 10;
    private static final int MEMORY = 2 ^ 24;
    private static final int PARALLELISM = 1;
    private static final String SUFFIX = "$argon2id$v=19$m=26,t=10,p=1$";

    private Argon2() {

    }

    public static String getHashedData(String rawPassword) {
        return getEncode(rawPassword).substring(SUFFIX.length());
    }

    private static String getEncode(String data) {
        Argon2PasswordEncoder argon2 = new Argon2PasswordEncoder(SALT_LENGTH,KEY_LENGTH,PARALLELISM,MEMORY,ITERATIONS);
        String hashedData = argon2.encode(data);

        return Base64.getUrlEncoder().encodeToString(hashedData.getBytes());
    }

    public static boolean validate(String rawData, String hashedData) {
        byte[] encodedBytes = Base64.getDecoder().decode(hashedData);
        hashedData = new String(encodedBytes);
        hashedData = SUFFIX + hashedData;

        return new Argon2PasswordEncoder(SALT_LENGTH,KEY_LENGTH,PARALLELISM,MEMORY,ITERATIONS).matches(rawData, hashedData);
    }
}

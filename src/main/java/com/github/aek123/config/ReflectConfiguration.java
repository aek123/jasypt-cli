package com.github.aek123.config;

import org.jasypt.commons.CommonUtils;
import org.jasypt.digest.StandardStringDigester;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import org.jasypt.registry.AlgorithmRegistry;

import java.util.Set;

public class ReflectConfiguration {

    private static final String password = "some-random-password";

    public static void main(String[] args) {
        String text = "hello world";
        Set<String> algorithms = AlgorithmRegistry.getAllPBEAlgorithms();
        for (String algorithm : algorithms) {
            String enc = enc(text, algorithm);
            System.out.println(enc);
            String dec = dec(enc, algorithm);
            System.out.println(dec);
            assert dec.equals(text);
        }
        System.err.println("*************************************");
        Set<String> digestAlgorithms = AlgorithmRegistry.getAllDigestAlgorithms();
        for (String algorithm : digestAlgorithms) {
            StandardStringDigester digester = new StandardStringDigester();
            digester.setAlgorithm(algorithm);
            String digest = digester.digest(password);
            System.out.println(digest);
        }
    }

    private static String enc(String text, String algorithm) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(password);
        encryptor.setAlgorithm(algorithm);
        encryptor.setIvGenerator(new RandomIvGenerator());
        encryptor.setStringOutputType(CommonUtils.STRING_OUTPUT_TYPE_BASE64);
        return encryptor.encrypt(text);
    }

    private static String dec(String text, String algorithm) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(password);
        encryptor.setAlgorithm(algorithm);
        encryptor.setStringOutputType(CommonUtils.STRING_OUTPUT_TYPE_BASE64);
        encryptor.setIvGenerator(new RandomIvGenerator());
        return encryptor.decrypt(text);
    }
}

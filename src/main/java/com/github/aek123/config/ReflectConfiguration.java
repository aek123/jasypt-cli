package com.github.aek123.config;

import org.jasypt.commons.CommonUtils;
import org.jasypt.digest.StandardStringDigester;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.IvGenerator;
import org.jasypt.registry.AlgorithmRegistry;

import java.util.Set;

public class ReflectConfiguration {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        String text = "hello world";
        String password = "some-random-password";
        IvGenerator ivGenerator = (IvGenerator) Class.forName("org.jasypt.iv.RandomIvGenerator")
                                                     .getConstructor()
                                                     .newInstance();
        Set<String> algorithms = AlgorithmRegistry.getAllPBEAlgorithms();
        for (String algorithm : algorithms) {
            String enc = enc(text, password, algorithm, ivGenerator);
            System.out.println(enc);
            String dec = dec(enc, password, algorithm, ivGenerator);
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

    private static String enc(String text, String password, String algorithm, IvGenerator ivGenerator) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(password);
        encryptor.setAlgorithm(algorithm);
        encryptor.setIvGenerator(ivGenerator);
        encryptor.setStringOutputType(CommonUtils.STRING_OUTPUT_TYPE_BASE64);
        return encryptor.encrypt(text);
    }

    private static String dec(String text, String password, String algorithm, IvGenerator ivGenerator) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(password);
        encryptor.setAlgorithm(algorithm);
        encryptor.setStringOutputType(CommonUtils.STRING_OUTPUT_TYPE_BASE64);
        encryptor.setIvGenerator(ivGenerator);
        return encryptor.decrypt(text);
    }
}

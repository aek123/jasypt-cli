package com.github.aek123.config;

import com.github.aek123.EJasyptCli;
import org.jasypt.commons.CommonUtils;
import org.jasypt.digest.StandardStringDigester;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.IvGenerator;
import org.jasypt.registry.AlgorithmRegistry;
import org.jasypt.salt.SaltGenerator;

import java.util.Set;

public class ReflectConfiguration {

    public static void main(String[] args) throws Exception {
        String text = "hello world";
        String password = "some-random-password";
        IvGenerator ivGenerator = (IvGenerator) Class.forName("org.jasypt.iv.RandomIvGenerator")
                                                     .getConstructor()
                                                     .newInstance();
        SaltGenerator saltGenerator = (SaltGenerator) Class.forName("org.jasypt.salt.RandomSaltGenerator")
                                                           .getConstructor()
                                                           .newInstance();
        runPBEAlgorithms(text, password, ivGenerator, saltGenerator);
        System.err.println("*************************************");
        runDigestAlgorithms(password);
        System.err.println("*************************************");
        EJasyptCli.createCommandLine().usage(System.out);
    }

    @SuppressWarnings("unchecked")
    private static void runDigestAlgorithms(String password) {
        Set<String> digestAlgorithms = AlgorithmRegistry.getAllDigestAlgorithms();
        for (String algorithm : digestAlgorithms) {
            StandardStringDigester digester = new StandardStringDigester();
            digester.setAlgorithm(algorithm);
            String digest = digester.digest(password);
            System.out.println(digest);
        }
    }

    @SuppressWarnings("unchecked")
    private static void runPBEAlgorithms(String text, String password, IvGenerator ivGenerator, SaltGenerator saltGenerator) {
        Set<String> algorithms = AlgorithmRegistry.getAllPBEAlgorithms();
        for (String algorithm : algorithms) {
            String enc = enc(text, password, algorithm, ivGenerator, saltGenerator);
            System.out.println(enc);
            String dec = dec(enc, password, algorithm, ivGenerator, saltGenerator);
            System.out.println(dec);
            assert dec.equals(text);
        }
    }

    private static String enc(String text,
                              String password,
                              String algorithm,
                              IvGenerator ivGenerator,
                              SaltGenerator saltGenerator) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(password);
        encryptor.setAlgorithm(algorithm);
        encryptor.setIvGenerator(ivGenerator);
        encryptor.setStringOutputType(CommonUtils.STRING_OUTPUT_TYPE_BASE64);
        encryptor.setSaltGenerator(saltGenerator);
        return encryptor.encrypt(text);
    }

    private static String dec(String text,
                              String password,
                              String algorithm,
                              IvGenerator ivGenerator,
                              SaltGenerator saltGenerator) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(password);
        encryptor.setAlgorithm(algorithm);
        encryptor.setStringOutputType(CommonUtils.STRING_OUTPUT_TYPE_BASE64);
        encryptor.setIvGenerator(ivGenerator);
        encryptor.setSaltGenerator(saltGenerator);
        return encryptor.decrypt(text);
    }
}

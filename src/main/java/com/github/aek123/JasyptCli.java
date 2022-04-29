package com.github.aek123;

import org.jasypt.intf.cli.AlgorithmRegistryCLI;
import org.jasypt.intf.cli.JasyptPBEStringDecryptionCLI;
import org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI;
import org.jasypt.intf.cli.JasyptStringDigestCLI;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JasyptCli {
    public static void main(String[] args) {
        if (args.length == 0 || CliFunction.findBy(args[0]) == null) {
            List<String> codes = Arrays.stream(CliFunction.values())
                                       .map(CliFunction::getCode)
                                       .toList();
            System.out.printf("You have to specify one of %s function to work as first argument%n", codes);
            return;
        }
        CliFunction function = CliFunction.findBy(args[0]);
        args[0] = args[0] + ".sh";
        switch (function) {
            case ENCRYPT -> JasyptPBEStringEncryptionCLI.main(args);
            case DECRYPT -> JasyptPBEStringDecryptionCLI.main(args);
            case DIGEST -> JasyptStringDigestCLI.main(args);
            case LIST_ALGORITHMS -> AlgorithmRegistryCLI.main(args);
        }
    }

    public enum CliFunction {
        ENCRYPT("encrypt"),
        DECRYPT("decrypt"),
        DIGEST("digest"),
        LIST_ALGORITHMS("listAlgorithms");

        private static final Map<String, CliFunction> MAP = Arrays.stream(values())
                                                                  .collect(Collectors.toMap(CliFunction::getCode, Function.identity()));

        private final String code;

        CliFunction(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public static CliFunction findBy(String code) {
            return MAP.get(code);
        }
    }

}
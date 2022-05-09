package com.github.aek123;

import org.jasypt.registry.AlgorithmRegistry;
import picocli.CommandLine;

import java.util.Deque;
import java.util.LinkedList;

public class EJasyptCli {

    private static final String SALT_GENERATOR = "org.jasypt.salt.RandomSaltGenerator";
    private static final String IV_GENERATOR = "org.jasypt.iv.RandomIvGenerator";

    public static void main(String[] args) {
        CommandLine commandLine = createCommandLine();
        commandLine.setExecutionStrategy(EJasyptCli::run);
        int exitCode = commandLine.execute(args);
        System.exit(exitCode);
    }

    public static CommandLine createCommandLine() {
        CommandLine.Model.CommandSpec spec = CommandLine.Model.CommandSpec.create();
        spec.mixinStandardHelpOptions(true);
        CommandLine.Model.UsageMessageSpec messageSpec = new CommandLine.Model.UsageMessageSpec();
        messageSpec.description("Example usage: ",
                                "\tjasypt-cli 'my super secret text' enc -p myPassword",
                                "\tjasypt-cli 'M7Puz+8YaBTW0U8j2csRXa4b4lag+1VL7J6pP2ni4rgaDHwIH2fidQ==' dec -p myPassword",
                                "\tjasypt-cli 'my super secret text' dig -a SHA3-512"
        );
        spec.usageMessage(messageSpec);
        spec.addSubcommand("enc", getEncryptionSpec());
        spec.addSubcommand("dec", getDecryptionSpec());
        spec.addSubcommand("dig", getDigestSpec());
        spec.addOption(CommandLine.Model.OptionSpec.builder("--hex")
                                                   .type(boolean.class)
                                                   .description("Hexadecimal output format, default is base64.")
                                                   .required(false)
                                                   .build());
        spec.addPositional(CommandLine.Model.PositionalParamSpec.builder()
                                                                .paramLabel("INPUT")
                                                                .type(String.class)
                                                                .description("Input for subcommands")
                                                                .required(true)
                                                                .build());

        return new CommandLine(spec);
    }

    private static CommandLine.Model.CommandSpec getEncryptionSpec() {
        CommandLine.Model.CommandSpec encSpec = CommandLine.Model.CommandSpec.create();
        encSpec.mixinStandardHelpOptions(true);
        encSpec.addOption(CommandLine.Model.OptionSpec.builder("-p", "--password")
                                                      .paramLabel("PASSWORD")
                                                      .description("Password of encryption")
                                                      .type(String.class)
                                                      .required(true)
                                                      .build());
        encSpec.addOption(CommandLine.Model.OptionSpec.builder("-a", "--algorithm")
                                                      .paramLabel("PBE ALGORITHM")
                                                      .description("One of %s algorithm to encryption".formatted(AlgorithmRegistry.getAllPBEAlgorithms()))
                                                      .type(String.class)
                                                      .build());
        encSpec.addOption(CommandLine.Model.OptionSpec.builder("-i", "--ivGenerator")
                                                      .paramLabel("IV GENERATOR")
                                                      .description("IvGenerator for encryption")
                                                      .defaultValue(IV_GENERATOR)
                                                      .showDefaultValue(CommandLine.Help.Visibility.ALWAYS)
                                                      .type(String.class)
                                                      .build());
        encSpec.addOption(CommandLine.Model.OptionSpec.builder("-s", "--salGenerator")
                                                      .paramLabel("SALT GENERATOR")
                                                      .description("SaltGenerator for encryption")
                                                      .defaultValue(SALT_GENERATOR)
                                                      .showDefaultValue(CommandLine.Help.Visibility.ALWAYS)
                                                      .type(String.class)
                                                      .build());
        return encSpec;
    }

    private static CommandLine.Model.CommandSpec getDecryptionSpec() {
        CommandLine.Model.CommandSpec decSpec = CommandLine.Model.CommandSpec.create();
        decSpec.mixinStandardHelpOptions(true);
        decSpec.addOption(CommandLine.Model.OptionSpec.builder("-p", "--password")
                                                      .paramLabel("PASSWORD")
                                                      .description("Password of decryption")
                                                      .type(String.class)
                                                      .required(true)
                                                      .build());
        decSpec.addOption(CommandLine.Model.OptionSpec.builder("-a", "--algorithm")
                                                      .paramLabel("PBE ALGORITHM")
                                                      .description("One of %s algorithm to decryption".formatted(AlgorithmRegistry.getAllPBEAlgorithms()))
                                                      .type(String.class)
                                                      .build());
        decSpec.addOption(CommandLine.Model.OptionSpec.builder("-i", "--ivGenerator")
                                                      .paramLabel("IV GENERATOR")
                                                      .description("IvGenerator for decryption")
                                                      .defaultValue(IV_GENERATOR)
                                                      .showDefaultValue(CommandLine.Help.Visibility.ALWAYS)
                                                      .type(String.class)
                                                      .build());
        decSpec.addOption(CommandLine.Model.OptionSpec.builder("-s", "--salGenerator")
                                                      .paramLabel("SALT GENERATOR")
                                                      .description("SaltGenerator for decryption")
                                                      .defaultValue(SALT_GENERATOR)
                                                      .showDefaultValue(CommandLine.Help.Visibility.ALWAYS)
                                                      .type(String.class)
                                                      .build());
        return decSpec;
    }

    private static CommandLine.Model.CommandSpec getDigestSpec() {
        CommandLine.Model.CommandSpec digSpec = CommandLine.Model.CommandSpec.create();
        digSpec.mixinStandardHelpOptions(true);
        digSpec.addOption(CommandLine.Model.OptionSpec.builder("-a", "--algorithm")
                                                      .paramLabel("DIGEST ALGORITHM")
                                                      .description("One of %s algorithm to digest".formatted(AlgorithmRegistry.getAllDigestAlgorithms()))
                                                      .type(String.class)
                                                      .build());
        digSpec.addOption(CommandLine.Model.OptionSpec.builder("-s", "--salGenerator")
                                                      .paramLabel("SALT GENERATOR")
                                                      .description("SaltGenerator for digest")
                                                      .defaultValue(SALT_GENERATOR)
                                                      .showDefaultValue(CommandLine.Help.Visibility.ALWAYS)
                                                      .type(String.class)
                                                      .build());
        return digSpec;
    }

    private static int run(CommandLine.ParseResult parseResult) {
        Integer helpExitCode = CommandLine.executeHelpRequest(parseResult);
        if (helpExitCode != null) {
            return helpExitCode;
        }
        String input = parseResult.matchedPositionalValue(0, null);
        if (!parseResult.hasSubcommand()) {
            System.err.printf("One of %s subcommand missing!%n", parseResult.commandSpec().subcommands().keySet());
            CommandLine.usage(parseResult.commandSpec(), System.out);
            return 1;
        }
        Deque<String> args = new LinkedList<>();
        if (parseResult.hasMatchedOption("hex")) {
            args.add("stringOutputType=hexadecimal");
        }
        CommandLine.ParseResult subResult = parseResult.subcommand();
        args.add("input=%s".formatted(input));
        return switch (subResult.commandSpec().name()) {
            case "enc" -> runEnc(subResult, args);
            case "dec" -> runDec(subResult, args);
            case "dig" -> runDig(subResult, args);
            default -> 1;
        };
    }

    private static int runEnc(CommandLine.ParseResult parseResult, Deque<String> args) {
        Integer helpExitCode = CommandLine.executeHelpRequest(parseResult);
        if (helpExitCode != null) {
            return helpExitCode;
        }
        args.addFirst("encrypt");
        String value = parseResult.matchedOptionValue("p", null);
        if (value != null) {
            args.add("password=%s".formatted(value));
        }
        value = parseResult.matchedOptionValue("a", null);
        if (value != null) {
            args.add("algorithm=%s".formatted(value));
        }
        value = parseResult.matchedOptionValue("i", IV_GENERATOR);
        args.add("ivGeneratorClassName=%s".formatted(value));
        value = parseResult.matchedOptionValue("s", SALT_GENERATOR);
        args.add("saltGeneratorClassName=%s".formatted(value));
        JasyptCli.main(args.toArray(String[]::new));
        return 0;
    }

    private static int runDec(CommandLine.ParseResult parseResult, Deque<String> args) {
        Integer helpExitCode = CommandLine.executeHelpRequest(parseResult);
        if (helpExitCode != null) {
            return helpExitCode;
        }
        args.addFirst("decrypt");
        String value = parseResult.matchedOptionValue("p", null);
        if (value != null) {
            args.add("password=%s".formatted(value));
        }
        value = parseResult.matchedOptionValue("a", null);
        if (value != null) {
            args.add("algorithm=%s".formatted(value));
        }
        value = parseResult.matchedOptionValue("i", IV_GENERATOR);
        args.add("ivGeneratorClassName=%s".formatted(value));
        value = parseResult.matchedOptionValue("s", SALT_GENERATOR);
        args.add("saltGeneratorClassName=%s".formatted(value));
        JasyptCli.main(args.toArray(String[]::new));
        return 0;
    }

    private static int runDig(CommandLine.ParseResult parseResult, Deque<String> args) {
        Integer helpExitCode = CommandLine.executeHelpRequest(parseResult);
        if (helpExitCode != null) {
            return helpExitCode;
        }
        args.addFirst("digest");
        String value = parseResult.matchedOptionValue("a", null);
        if (value != null) {
            args.add("algorithm=%s".formatted(value));
        }
        value = parseResult.matchedOptionValue("s", SALT_GENERATOR);
        args.add("saltGeneratorClassName=%s".formatted(value));
        JasyptCli.main(args.toArray(String[]::new));
        return 0;
    }

}

This project intended to use of jasypt-dist scripts with one native executable.

You can look the sources from:

    https://github.com/jasypt/jasypt/tree/jasypt-1.9.3/jasypt-dist/src/main/bin

Or you can download from:

    https://github.com/jasypt/jasypt/releases/tag/jasypt-1.9.3

I use GraalVM 22.1.0 java 17 version in ubuntu to create native image.

Once you select GraalVM you need to execute in terminal to enable to create native images:

    gu install native-image

In order to create standalone image you need to run ReflectionConfiguration with native image agent:

     -agentlib:native-image-agent=config-merge-dir=src/main/resources/META-INF/native-image

After you run ReflectionConfiguration class with given agent some json files will be generated under resource directory.

Then just create a fat jar with maven:

    maven clean package

And the last, in terminal run following code:

    native-image --no-fallback --initialize-at-build-time=sun.instrument.InstrumentationImpl -jar target/jasypt-cli-1.0-SNAPSHOT-jar-with-dependencies.jar jasypt-cli

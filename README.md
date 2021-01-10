# openHAB Add-ons - Sony binding

This repository is based on the offical [openHAB 3.0 addons repository](https://github.com/openhab/openhab-addons) 
and the sources for the awsome Sony binding for openHAB 2.5 as provided [here](https://github.com/tmrobert8/openhab-addons/releases/tag/2.5-1201-2).

It is currenty aimed at providing a personal and lightweight playground to learn how to implement openHAB 3 bindings 
with the specific task of migrating the Sony binding for openHAB 2.5 to openHAB 3.

Note this is not (yet) an attempt to contribute to the offical repositories.

## Development / Repository Organization

openHAB add-ons are [Java](https://en.wikipedia.org/wiki/Java_(programming_language)) `.jar` files.

The openHAB build system is based on [Maven](https://maven.apache.org/what-is-maven.html).
The official IDE (Integrated development environment) is Eclipse.

You find the following repository structure:

```
.
+-- bom       Maven buildsystem: Bill of materials
|   +-- openhab-addons  Lists all extensions for other repos to reference them
|   +-- ...             Other boms
|
+-- bundles   Official openHAB extensions
|   +-- org.openhab.binding.airquality
|   +-- org.openhab.binding.astro
|   +-- ...
|
+-- features  Part of the runtime dependency resolver ("Karaf features")
|
+-- itests    Integration tests. Those tests require parts of the framework to run.
|   +-- org.openhab.binding.astro.tests
|   +-- org.openhab.binding.avmfritz.tests
|   +-- ...
|
+-- src/etc   Auxilary buildsystem files: The license header for automatic checks for example
+-- tools     Static code analyser instructions
|
+-- CODEOWNERS  This file assigns people to directories so that they are informed if a pull-request
                would modify their add-ons.
```

### Command line build

To build all add-ons from the command-line, type in:

`mvn clean install`

To improve build times you can add the following options to the command:

| Option                        | Description                                         |
| ----------------------------- | --------------------------------------------------- |
| `-DskipChecks`                | Skip the static analysis (Checkstyle, FindBugs)     |
| `-DskipTests`                 | Skip the execution of tests                         |
| `-Dmaven.test.skip=true`      | Skip the compilation and execution of tests         |
| `-Dfeatures.verify.skip=true` | Skip the Karaf feature verification                 |
| `-Dspotless.check.skip=true`  | Skip the Spotless code style checks                 |
| `-o`                          | Work offline so Maven does not download any updates |
| `-T 1C`                       | Build in parallel, using 1 thread per core          |

For example you can skip checks and tests during development with:

`mvn clean install -DskipChecks -DskipTests`

Adding these options improves the build time but could hide problems in your code.
Parallel builds are also less easy to debug and the increased load may cause timing sensitive tests to fail.

To check if your code is following the [code style](https://www.openhab.org/docs/developer/guidelines.html#b-code-formatting-rules-style) run: `mvn spotless:check`
To reformat your code so it conforms to the code style you can run: `mvn spotless:apply`

When your add-on also has an integration test in the `itests` directory, you may need to update the runbundles in the `itest.bndrun` file when the Maven dependencies change.
Maven can resolve the integration test dependencies automatically by executing: `mvn clean install -DwithResolver -DskipChecks`

The build generates a `.jar` file per bundle in the respective bundle `/target` directory.

### How to develop via an Integrated Development Environment (IDE)

We have assembled some step-by-step guides for different IDEs on our developer documentation website:

https://www.openhab.org/docs/developer/#setup-the-development-environment

Happy coding!

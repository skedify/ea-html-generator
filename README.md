# Enterprise Architect HTML Report Generator

The Enteprise Architect HTML Report Generator provides a way to automate the generation of a HTML report from a package in an Enterprise Architect project.

## Building
> Make sure you have the Java 8 JDK installed on your system and the `JAVA_HOME` environment variable set correctly.

Run the following command:

`./mvnw clean install`

> If you get errors indicating that the `org.sparx:ea-api` artifact cannot be found, install the `org.sparx:ea-api` artifact in your local Maven repository.
See the `README` in the `eaapi` subfolder.

## Running
The JAR can be run using the following command:

`java -jar ea-html-generator-<x.y.z>.jar`

where `<x.y.z>` should be replaced by the version of the JAR.

### Options
|Option|Description|Default value|
|---|---|---|
|`--help`|Provides information about the usage of the application.||
|`--input`|Input file path or connection string for the Enterprise Architect project.|`default.eapx`|
|`--username`|Username for the Enterprise Architect project.||
|`--password`|Password for the Enterprise Architect project.||
|`--package`|The package for which a HTML report must be generated.|`Default`|
|`--output`|The output folder where the the HTML report files should be saved.|`./html-report-output`|
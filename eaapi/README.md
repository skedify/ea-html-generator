# Enterprise Architect API

To interact with the Enterprise Architect domain model and automation API, a JAR is made available together with a few DLLs that are used by the JAR.
In order to use this JAR as a dependency in a custom Maven project, it is useful to have the JAR available as a Maven artifact. To achieve this, we have created a POM file that allows to install the JAR in a Maven repository.

## How to install the JAR in a Maven repository

> Make sure you have the Java 8 JDK installed on your system and the `JAVA_HOME` environment variable set correctly.

Run the following command:

`../mvnw install:install-file -Dfile=eaapi.jar -DpomFile=pom.xml`

## How to use the Enterprise Architect API in a Maven project

Add the following to the `pom.xml` of your Maven project:

```xml
<dependencies>
  <dependency>
    <groupId>org.sparx</groupId>
    <artifactId>ea-api</artifactId>
    <version>15.2</version>
  </dependency>
</dependencies>
```
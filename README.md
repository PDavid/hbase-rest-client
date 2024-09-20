# About

Small Java application which calls the rest version REST API endpoint of the HBase REST server component, then parses the response using the `hbase-rest` library.

The application requests the following representations:

- protobuf
- XML
- JSON

# How to run

Make sure HBase rest server is running. Localhost and port 8080 is assumed.

Set the following JVM option:
`--add-opens java.base/java.lang=ALL-UNNAMED` (required for JAXB unmarshalling)

Then run the application:
`./gradlew run`
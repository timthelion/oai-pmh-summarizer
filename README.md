Collate publications from OAI_PMH source
-----------------------------------------

Compile:

```
mvn clean compile assembly:single
```

Run:

```
java -jar target/collate-publications-1.0-SNAPSHOT-jar-with-dependencies.jar -e https://pub.uni-bielefeld.de/oai
```

Note that the runtime may be very slow due to the need to download hundreds of xml files.


Test

```
mvn test
```

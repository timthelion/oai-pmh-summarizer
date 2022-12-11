Collate publications from OAI_PMH source
-----------------------------------------

Compile:

```
mvn clean compile assembly:single
```

Run:

```
java -jar target/collate-publications-1.0-SNAPSHOT-jar-with-dependencies.jar -e https://pub.uni-bielefeld.de/oai?verb=ListRecords&metadataPrefix=oai_datacite
```

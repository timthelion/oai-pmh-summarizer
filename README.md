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

Example output

```
$ java -jar target/collate-publications-1.0-SNAPSHOT-jar-with-dependencies.jar -e https://pub.uni-bielefeld.de/oai
... <snip> ...
Publications by year
+---------------+-----+
|publicationYear|count|
+---------------+-----+
|           1983|  380|
|           1972|  107|
|           2007| 2686|
|           1979|  295|
|           1988|  522|
|           2014| 3459|
|           1986|  479|
|           1969|   67|
|           1967|   45|
|           1964|   10|
|           2012| 3484|
|           1991|  671|
|           1975|  172|
|           2016| 3254|
|           1960|    6|
|           1994|  987|
|           1987|  511|
|           1999| 1254|
|           1997| 1135|
|           1963|   10|
|           2009| 3031|
|           2010| 3419|
|           2006| 2454|
|           1970|   80|
|           1998| 1206|
|           2013| 3452|
|           1968|   41|
|           1976|  241|
|           1984|  376|
|           1977|  226|
|           2004| 2076|
|           2003| 1914|
|           2002| 1700|
|           1982|  345|
|           2011| 3427|
|           1966|   25|
|           1973|  149|
|           1980|  366|
|           1971|   91|
|           1978|  282|
|           1995|  913|
|           2005| 2270|
|           1974|  170|
|           1981|  317|
|           1996| 1007|
|           1992|  738|
|           2008| 2978|
|           1965|   16|
|           1961|   11|
|           1993|  756|
|           1989|  535|
|           2001| 1680|
|           2000| 1554|
|           1985|  435|
|           1990|  653|
|           1958|    3|
|           2021| 3614|
|           2018| 3432|
|           1959|    1|
|           1957|    4|
|           1962|   11|
|           2020| 3362|
|           2019| 3557|
|           2015| 3378|
|           2017| 3260|
|           2022| 2754|
|           2023|   80|
+---------------+-----+

Publications by type
+--------------------+-----+
|          recordType|count|
+--------------------+-----+
|   bi_postdoc_thesis|   14|
|     bi_dissertation| 2415|
|   conference_editor|  174|
|              report|  924|
|        dissertation| 3585|
|     journal_article|38436|
|        book_chapter|15313|
| conference_abstract| 2892|
|         book_editor| 1985|
|            preprint|  515|
|          conference| 6948|
|  bi_bachelor_thesis|   20|
|      journal_editor|  408|
|              review| 1709|
|    bi_master_thesis|   93|
|       working_paper| 1894|
|                book| 2366|
|encyclopedia_article|  842|
|   newspaper_article|  596|
| translation_chapter|   58|
|              patent|   95|
|         translation|   29|
|       research_data|  411|
|           blog_post|  191|
|            software|   11|
+--------------------+-----+

```

Test

```
mvn test
```

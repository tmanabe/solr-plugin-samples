# solr_demo_plugin

フォーク元をSolr 9に対応させる検証のためのブランチです。

## LICENSE

Some code in this repository were derived and modified from [Apache Solr 9.2.1](https://github.com/apache/solr/tree/branch_9_2) under the [Apache License 2.0](https://github.com/apache/solr/blob/branch_9_2/LICENSE.txt). Here is the list of correspondence between them:

| This Repository                                                                                                        | Apache Solr 9.2.1                                                                       |
|------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------|
| io.github.tmanabe.demo.DemoMergeStrategy#unmarshalSortValues                                                           | org.apache.solr.handler.component.QueryComponent#unmarshalSortValues                    |
| io.github.tmanabe.demo.DemoMergeStrategy#populateNextCursorMarkFromMergedShards                                        | org.apache.solr.handler.component.QueryComponent#populateNextCursorMarkFromMergedShards |
| io.github.tmanabe.demo.DemoMergeStrategy#merge                                                                         | org.apache.solr.handler.component.QueryComponent#mergeIds                               |
| io.github.tmanabe.demo.DemoMergeStrategy$ScoreAndDoc                                                                   | org.apache.solr.handler.component.QueryComponent$ScoreAndDoc                            |
| io.github.tmanabe.demo.DemoMergeStrategy#handleMergeFields                                                             | org.apache.solr.handler.component.QueryComponent#doFieldSortValues                      |
| io.github.tmanabe.demo.DemoRankQuery#getTopDocsCollector<br>io.github.tmanabe.demo2.Demo2RankQuery#getTopDocsCollector | org.apache.solr.search.SolrIndexSearcher#buildTopDocsCollector |

This repository itself is published under the [MIT License](./LICENSE).

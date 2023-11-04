# solr_demo_plugin

フォーク元をSolr 9に対応させる検証のためのブランチです。

## LICENSE

Some code in this repository were derived and modified from [Apache Solr 9.2.1](https://github.com/apache/solr/tree/branch_9_2) under the [Apache License 2.0](https://github.com/apache/solr/blob/branch_9_2/LICENSE.txt). Here is the list of correspondence between them:

| This Repository                                                                | Apache Solr 9.2.1                                                                       |
|--------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------|
| jp.co.yahoo.solr.demo.DemoMergeStrategy#unmarshalSortValues                    | org.apache.solr.handler.component.QueryComponent#unmarshalSortValues                    |
| jp.co.yahoo.solr.demo.DemoMergeStrategy#populateNextCursorMarkFromMergedShards | org.apache.solr.handler.component.QueryComponent#populateNextCursorMarkFromMergedShards |
| jp.co.yahoo.solr.demo.DemoMergeStrategy#merge                                  | org.apache.solr.handler.component.QueryComponent#mergeIds                               |
| jp.co.yahoo.solr.demo.DemoMergeStrategy$ScoreAndDoc                            | org.apache.solr.handler.component.QueryComponent$ScoreAndDoc                            |
| jp.co.yahoo.solr.demo.DemoMergeStrategy#handleMergeFields                      | org.apache.solr.handler.component.QueryComponent#doFieldSortValues                      |
| jp.co.yahoo.solr.demo.DemoRankQuery#getTopDocsCollector<br>jp.co.yahoo.solr.demo.Demo2RankQuery#getTopDocsCollector | org.apache.solr.search.SolrIndexSearcher#buildTopDocsCollector |

This repository itself is published under the [MIT License](./LICENSE).

package io.github.tmanabe.demo;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.FieldDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryVisitor;
import org.apache.lucene.search.ScoreMode;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.Weight;
import org.apache.solr.handler.component.MergeStrategy;
import org.apache.solr.search.CursorMark;
import org.apache.solr.search.QueryCommand;
import org.apache.solr.search.QueryUtils;
import org.apache.solr.search.RankQuery;

import java.io.IOException;
import java.util.Objects;

public class DemoRankQuery extends RankQuery {
  private Query wrappedQuery;  // The original query wrapped in this query
  private final DemoMergeStrategy demoMergeStrategy;
  public DemoContext demoContext;

  /**
   * Constructor for demo rank query.
   *
   * @param demoMergeStrategy related merge strategy
   * @param demoContext related context object
   */
  public DemoRankQuery(DemoMergeStrategy demoMergeStrategy, DemoContext demoContext) {
    this.demoMergeStrategy = demoMergeStrategy;
    this.demoContext = demoContext;
  }

  public Sort weightSort(Sort sort, IndexSearcher searcher) throws IOException {
    return (sort != null) ? sort.rewrite(searcher) : null;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public TopDocsCollector getTopDocsCollector(int len, QueryCommand cmd, IndexSearcher searcher) throws IOException {
    TopDocsCollector wrappedTopDocsCollector;

    // cf. SolrIndexSearcher#buildTopDocsCollector
    int minNumFound = cmd.getMinExactCount();
    if (null == cmd.getSort()) {
      assert null == cmd.getCursorMark() : "have cursor but no sort";
      wrappedTopDocsCollector = TopScoreDocCollector.create(len, minNumFound);
    } else {
      // we have a sort
      final Sort weightedSort = weightSort(cmd.getSort(), searcher);
      final CursorMark cursor = cmd.getCursorMark();

      final FieldDoc searchAfter = (null != cursor ? cursor.getSearchAfterFieldDoc() : null);
      wrappedTopDocsCollector = TopFieldCollector.create(weightedSort, len, searchAfter, minNumFound);
    }

    return new DemoTopDocsCollector(searcher, wrappedTopDocsCollector, demoContext);
  }

  @Override
  public MergeStrategy getMergeStrategy() {
    return demoMergeStrategy;
  }

  @Override
  public RankQuery wrap(Query query) {
    if (query != null) {
      // NOTE: workaround for negative query
      this.wrappedQuery = QueryUtils.makeQueryable(query);
    }
    return this;
  }

  @Override
  public Weight createWeight(IndexSearcher searcher, ScoreMode scoreMode, float boost) throws IOException {
    return wrappedQuery.createWeight(searcher, scoreMode, boost);
  }

  @Override
  public Query rewrite(IndexReader reader) throws IOException {
    Query q = wrappedQuery.rewrite(reader);
    if (q == wrappedQuery) {
      return this;
    } else {
      return new DemoRankQuery(demoMergeStrategy, demoContext).wrap(q);
    }
  }

  @Override
  public void visit(QueryVisitor queryVisitor) {
    queryVisitor.visitLeaf(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;

    DemoRankQuery that = (DemoRankQuery) obj;
    return Objects.equals(wrappedQuery, that.wrappedQuery) && Objects.equals(demoMergeStrategy, that.demoMergeStrategy)
           && Objects.equals(demoContext, that.demoContext);
  }

  @Override
  public int hashCode() {
    return Objects.hash(wrappedQuery, demoMergeStrategy, demoContext);
  }
}

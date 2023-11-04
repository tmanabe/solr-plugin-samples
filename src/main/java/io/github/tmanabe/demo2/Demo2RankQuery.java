package io.github.tmanabe.demo2;

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

public class Demo2RankQuery extends RankQuery {
  private Query wrappedQuery;  // The original query wrapped in this query
  private final Demo2Context demo2Context;

  /**
   * Constructor for demo2 rank query.
   *
   * @param demo2Context related context object
   */
  public Demo2RankQuery(Demo2Context demo2Context) {
    this.demo2Context = demo2Context;
  }

  public Sort weightSort(Sort sort, IndexSearcher searcher) throws IOException {
    return (sort != null) ? sort.rewrite(searcher) : null;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public TopDocsCollector getTopDocsCollector(int len, QueryCommand cmd, IndexSearcher searcher) throws IOException {
    // cf. SolrIndexSearcher#buildTopDocsCollector
    int minNumFound = cmd.getMinExactCount();
    if (null == cmd.getSort()) {
      assert null == cmd.getCursorMark() : "have cursor but no sort";
      return TopScoreDocCollector.create(len, minNumFound);
    } else {
      // we have a sort
      final Sort weightedSort = weightSort(cmd.getSort(), searcher);
      final CursorMark cursor = cmd.getCursorMark();

      final FieldDoc searchAfter = (null != cursor ? cursor.getSearchAfterFieldDoc() : null);
      return TopFieldCollector.create(weightedSort, len, searchAfter, minNumFound);
    }
  }

  @Override
  public MergeStrategy getMergeStrategy() {
    return null;
  }

  public Demo2Context getDemo2Context() {
    return demo2Context;
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
    return new Demo2Weight(this, wrappedQuery.createWeight(searcher, scoreMode, boost));
  }

  @Override
  public Query rewrite(IndexReader reader) throws IOException {
    Query q = wrappedQuery.rewrite(reader);
    if (q == wrappedQuery) {
      return this;
    } else {
      return new Demo2RankQuery(demo2Context).wrap(q);
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

    Demo2RankQuery that = (Demo2RankQuery) obj;
    return Objects.equals(wrappedQuery, that.wrappedQuery) && Objects.equals(demo2Context, that.demo2Context);
  }

  @Override
  public int hashCode() {
    return Objects.hash(wrappedQuery, demo2Context);
  }
}

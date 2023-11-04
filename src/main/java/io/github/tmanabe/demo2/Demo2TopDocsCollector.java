package io.github.tmanabe.demo2;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.LeafCollector;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.ScoreMode;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.solr.common.SolrException;

import java.io.IOException;

public class Demo2TopDocsCollector extends TopDocsCollector<ScoreDoc> {
    private final IndexSearcher indexSearcher;
    @SuppressWarnings("rawtypes")
    private final TopDocsCollector wrappedTopDocsCollector;
    private final Demo2RankQuery demo2RankQuery;

    @SuppressWarnings("rawtypes")
    protected Demo2TopDocsCollector(IndexSearcher indexSearcher, TopDocsCollector topDocsCollector,
                                    Demo2RankQuery demo2RankQuery) {
        super(null);
        this.indexSearcher = indexSearcher;
        this.wrappedTopDocsCollector = topDocsCollector;
        this.demo2RankQuery = demo2RankQuery;
    }

    @Override
    public LeafCollector getLeafCollector(LeafReaderContext context) throws IOException {
        return wrappedTopDocsCollector.getLeafCollector(context);
    }

    @Override
    public ScoreMode scoreMode() {
        return wrappedTopDocsCollector.scoreMode();
    }

    @Override
    public int getTotalHits() {
        return wrappedTopDocsCollector.getTotalHits();
    }

    @Override
    public TopDocs topDocs(int start, int rows) {
        TopDocs results = wrappedTopDocsCollector.topDocs();
        try {
            TopFieldCollector.populateScores(results.scoreDocs, indexSearcher, demo2RankQuery);
        } catch (IOException e) {
            throw new SolrException(SolrException.ErrorCode.SERVICE_UNAVAILABLE, "failed to populate scores", e);
        }
        return results;
    }
}

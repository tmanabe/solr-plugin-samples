package io.github.tmanabe.demo2;

import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class Demo2Scorer extends Scorer {
  private final Scorer wrappedScorer;
  private final BinaryDocValues binaryDocValues;
  private final float[] queryVector;

  private float cachedScore;

  public Demo2Scorer(Demo2Weight demo2Weight, LeafReaderContext leafReaderContext, Scorer wrappedScorer) throws IOException {
    super(demo2Weight);
    this.wrappedScorer = wrappedScorer;
    Demo2Context demo2Context = demo2Weight.getDemo2Context();
    this.binaryDocValues = leafReaderContext.reader().getBinaryDocValues(demo2Context.fieldName);
    this.queryVector = demo2Context.queryVector;

    cachedScore = 0.0f;
  }

  @Override
  public int docID() {
    return wrappedScorer.docID();
  }

  @Override
  public float score() throws IOException {
    if (null == binaryDocValues || docID() < binaryDocValues.docID()) {
      return 0.0f;
    }
    if (binaryDocValues.docID() == docID()) {
      return cachedScore;
    }

    binaryDocValues.advance(docID());
    if (binaryDocValues.docID() == docID()) {
      BytesRef bytesRef = binaryDocValues.binaryValue();
      FloatBuffer floatBuffer = ByteBuffer.wrap(bytesRef.bytes, bytesRef.offset, bytesRef.length).asFloatBuffer();
      if (floatBuffer.limit() == queryVector.length) {
        cachedScore = 2.0f;
        for (int i = 0; i < floatBuffer.limit(); ++i) {
          cachedScore += floatBuffer.get(i) * queryVector[i];
        }
      } else {
        cachedScore = 0.0f;
      }
    } else {
      cachedScore = 0.0f;
    }
    return cachedScore;
  }

  @Override
  public DocIdSetIterator iterator() {
    return wrappedScorer.iterator();
  }

  @Override
  public float getMaxScore(int i) throws IOException {
    return wrappedScorer.getMaxScore(i);
  }
}

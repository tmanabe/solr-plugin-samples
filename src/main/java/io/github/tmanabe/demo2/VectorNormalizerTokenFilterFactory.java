package io.github.tmanabe.demo2;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.TokenFilterFactory;

import java.util.Map;

public class VectorNormalizerTokenFilterFactory extends TokenFilterFactory {
  public VectorNormalizerTokenFilterFactory(Map<String, String> args) {
    super(args);
  }

  @Override
  public VectorNormalizerTokenFilter create(TokenStream input) {
    return new VectorNormalizerTokenFilter(input);
  }
}

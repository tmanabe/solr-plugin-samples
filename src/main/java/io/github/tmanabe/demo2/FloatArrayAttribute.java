package io.github.tmanabe.demo2;

import org.apache.lucene.util.Attribute;

public interface FloatArrayAttribute extends Attribute {
  float[] getFloatArray();

  void setFloatArray(float[] floatArray);
}

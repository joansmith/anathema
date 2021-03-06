package net.sf.anathema.hero.sheet.pdf.encoder.boxes;

import net.sf.anathema.framework.environment.dependencies.DoNotInstantiateAutomatically;
import net.sf.anathema.hero.sheet.pdf.content.BasicContent;
import net.sf.anathema.framework.environment.Resources;
import net.sf.anathema.lib.util.SimpleIdentifier;

@DoNotInstantiateAutomatically
public class NullEncoderFactory extends SimpleIdentifier implements EncoderFactory {
  public static final NullBoxContentEncoder NULL_ENCODER = new NullBoxContentEncoder("Unknown");

  public NullEncoderFactory(String id) {
    super(id);
  }

  @Override
  public ContentEncoder create(Resources resources, BasicContent content) {
    return NULL_ENCODER;
  }

  @Override
  public boolean supports(BasicContent content) {
    return true;
  }

  @Override
  public float getPreferredHeight(EncodingMetrics metrics, float width) {
    return 30;
  }
}

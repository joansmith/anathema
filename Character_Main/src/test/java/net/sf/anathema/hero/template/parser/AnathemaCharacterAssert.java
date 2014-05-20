package net.sf.anathema.hero.template.parser;

import net.sf.anathema.character.main.traits.ITraitTemplate;
import net.sf.anathema.character.main.traits.limitation.EssenceBasedLimitation;
import net.sf.anathema.character.main.traits.limitation.StaticTraitLimitation;
import org.junit.Assert;

public class AnathemaCharacterAssert {

  public static void assertEssenceLimitedTraitTemplate(int startValue, int zeroLevelValue, int minimalValue, ITraitTemplate traitTemplate) {
    assertBasicValues(startValue, zeroLevelValue, minimalValue, traitTemplate);
    Assert.assertTrue(traitTemplate.getLimitation() instanceof EssenceBasedLimitation);
  }

  public static void assertStaticSimpleTraitTemplate(int startValue, int zeroLevelValue, int minimalValue, int maximalValue,
                                                     ITraitTemplate traitTemplate) {
    assertBasicValues(startValue, zeroLevelValue, minimalValue, traitTemplate);
    Assert.assertEquals(maximalValue, ((StaticTraitLimitation) traitTemplate.getLimitation()).getStaticLimit());
  }

  private static void assertBasicValues(int startValue, int zeroLevelValue, int minimalValue, ITraitTemplate traitTemplate) {
    Assert.assertEquals(startValue, traitTemplate.getStartValue());
    Assert.assertEquals(minimalValue, traitTemplate.getMinimumValue(null));
  }
}
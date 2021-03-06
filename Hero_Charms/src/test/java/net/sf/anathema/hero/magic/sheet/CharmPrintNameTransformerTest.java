package net.sf.anathema.hero.magic.sheet;

import net.sf.anathema.hero.dummy.DummyCharm;
import net.sf.anathema.character.magic.charm.Charm;
import net.sf.anathema.hero.dummy.DummyGenericTrait;
import net.sf.anathema.hero.traits.model.ValuedTraitType;
import net.sf.anathema.hero.traits.model.types.AbilityType;
import net.sf.anathema.hero.charms.sheet.content.CharmPrintNameTransformer;
import net.sf.anathema.framework.environment.Resources;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CharmPrintNameTransformerTest {
  Resources resources = mock(Resources.class);
  DummyCharm charm = new DummyCharm("Abyssal.SecondExcellency.Archery", new Charm[0],
          new ValuedTraitType[]{new DummyGenericTrait(AbilityType.Archery, 5)});

  @Before
  public void setUp() throws Exception {
    charm.setGeneric(true);
  }

  @Test
  public void internationalizesGenericCharms() throws Exception {
    String expected = "Hit";
    when(resources.getString("Archery")).thenReturn("Archery");
    when(resources.getString("Abyssal.SecondExcellency", "Archery")).thenReturn(expected);
    String result = new CharmPrintNameTransformer(resources).apply(charm);
    assertThat(result, is(expected));
  }


}

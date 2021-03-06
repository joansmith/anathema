package net.sf.anathema.hero.charms.model.special;

import net.sf.anathema.character.magic.charm.Charm;
import net.sf.anathema.hero.charms.model.learn.ILearningCharmGroup;

public interface ISpecialCharmManager {

  CharmSpecialsModel getSpecialCharmConfiguration(Charm charm);

  void registerSpecialCharmConfiguration(ISpecialCharm specialCharm, Charm charm, ILearningCharmGroup group);
}
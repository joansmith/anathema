package net.sf.anathema.hero.charms.model.context;

import net.sf.anathema.character.magic.charm.Charm;
import net.sf.anathema.hero.charms.model.learn.ICharmLearnStrategy;
import net.sf.anathema.hero.charms.model.IBasicLearnCharmGroup;

public class CreationCharmLearnStrategy implements ICharmLearnStrategy {

  @Override
  public boolean isUnlearnable(IBasicLearnCharmGroup group, Charm charm) {
    return group.isLearned(charm);
  }

  @Override
  public boolean isLearned(IBasicLearnCharmGroup group, Charm charm) {
    return group.isLearned(charm, false);
  }

  @Override
  public void toggleLearned(IBasicLearnCharmGroup group, Charm charm) {
    group.toggleLearnedOnCreation(charm);
  }
}
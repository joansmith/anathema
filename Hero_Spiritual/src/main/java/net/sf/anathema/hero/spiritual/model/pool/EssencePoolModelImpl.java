package net.sf.anathema.hero.spiritual.model.pool;

import net.sf.anathema.hero.framework.HeroEnvironment;
import net.sf.anathema.hero.model.Hero;
import net.sf.anathema.hero.model.HeroModel;
import net.sf.anathema.hero.model.change.ChangeAnnouncer;
import net.sf.anathema.hero.spiritual.template.EssencePoolTemplate;
import net.sf.anathema.hero.traits.model.TraitMap;
import net.sf.anathema.hero.traits.model.TraitModelFetcher;
import net.sf.anathema.lib.control.ChangeListener;
import net.sf.anathema.lib.util.IdentifiedInteger;
import net.sf.anathema.lib.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class EssencePoolModelImpl implements EssencePoolModel, HeroModel {

  private final AggregatedOverdrivePool overdrivePool = new AggregatedOverdrivePool();
  private EssencePoolStrategy poolStrategy = null;
  private List<IEssencePoolModifier> essencePoolModifiers = new ArrayList<>();
  private EssencePoolTemplate template;

  public EssencePoolModelImpl(EssencePoolTemplate template) {
    this.template = template;
  }

  @Override
  public Identifier getId() {
    return ID;
  }

  @Override
  public void initialize(HeroEnvironment environment, Hero hero) {
    if (!isEssenceUser()) {
      return;
    }
    TraitMap traitMap = TraitModelFetcher.fetch(hero);
    EssencePoolConfiguration essencePoolConfiguration = new EssencePoolConfiguration(template);
    poolStrategy = new EssencePoolStrategyImpl(hero, essencePoolConfiguration, traitMap, overdrivePool);
  }

  @Override
  public void initializeListening(ChangeAnnouncer announcer) {
    // nothing to do
  }

  @Override
  public void addOverdrivePool(OverdrivePool pool) {
    overdrivePool.addOverdrivePool(pool);
  }

  @Override
  public String getPersonalPool() {
    if (!isEssenceUser()) {
      return null;
    }
    if (!hasAdditionalPools()) {
      return String.valueOf(poolStrategy.getStandardPersonalPool());
    }
    return poolStrategy.getStandardPersonalPool() + " (" + poolStrategy.getExtendedPersonalPool() + ")";
  }

  @Override
  public int getPersonalPoolValue() {
    return poolStrategy.getFullPersonalPool();
  }

  @Override
  public String getPeripheralPool() {
    if (!isEssenceUser()) {
      return null;
    }
    if (!hasAdditionalPools()) {
      return String.valueOf(poolStrategy.getStandardPeripheralPool());
    }
    return poolStrategy.getStandardPeripheralPool() + " (" + poolStrategy.getExtendedPeripheralPool() + ")";
  }

  @Override
  public int getPeripheralPoolValue() {
    return poolStrategy.getFullPeripheralPool();
  }

  @Override
  public int getOverdrivePoolValue() {
    return poolStrategy.getOverdrivePool();
  }

  @Override
  public IdentifiedInteger[] getComplexPools() {
    return poolStrategy.getComplexPools();
  }

  @Override
  public String getAttunedPool() {
    return "" + poolStrategy.getAttunementExpenditures();
  }

  @Override
  public int getAttunedPoolValue() {
    return poolStrategy.getAttunementExpenditures();
  }

  private boolean hasAdditionalPools() {
    return false;
  }

  @Override
  public boolean isEssenceUser() {
    return template.isEssenceUser;
  }

  @Override
  public boolean hasPeripheralPool() {
    return isEssenceUser() && (poolStrategy.getExtendedPeripheralPool() != 0 || poolStrategy.getUnmodifiedPeripheralPool() != 0);
  }

  @Override
  public void addPoolChangeListener(ChangeListener listener) {
    poolStrategy.addPoolChangeListener(listener);
  }

  @Override
  public Iterable<IEssencePoolModifier> getEssencePoolModifiers() {
    return essencePoolModifiers;
  }

  @Override
  public void addEssencePoolModifier(IEssencePoolModifier modifier) {
    essencePoolModifiers.add(modifier);
  }
}
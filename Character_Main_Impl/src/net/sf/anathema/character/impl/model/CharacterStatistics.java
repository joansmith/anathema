package net.sf.anathema.character.impl.model;

import net.disy.commons.core.util.Ensure;
import net.sf.anathema.character.generic.framework.ICharacterGenerics;
import net.sf.anathema.character.generic.framework.additionaltemplate.model.ICharacterModelContext;
import net.sf.anathema.character.generic.impl.magic.SpellException;
import net.sf.anathema.character.generic.magic.IMagicStats;
import net.sf.anathema.character.generic.rules.IEditionVisitor;
import net.sf.anathema.character.generic.rules.IExaltedEdition;
import net.sf.anathema.character.generic.rules.IExaltedRuleSet;
import net.sf.anathema.character.generic.template.ICharacterTemplate;
import net.sf.anathema.character.generic.traits.IGenericTrait;
import net.sf.anathema.character.generic.traits.ITraitType;
import net.sf.anathema.character.impl.generic.GenericCharacter;
import net.sf.anathema.character.impl.model.advance.ExperiencePointConfiguration;
import net.sf.anathema.character.impl.model.charm.CharmConfiguration;
import net.sf.anathema.character.impl.model.charm.ComboConfiguration;
import net.sf.anathema.character.impl.model.concept.CharacterConcept;
import net.sf.anathema.character.impl.model.concept.Motivation;
import net.sf.anathema.character.impl.model.concept.Nature;
import net.sf.anathema.character.impl.model.context.CharacterModelContext;
import net.sf.anathema.character.impl.model.statistics.ExtendedConfiguration;
import net.sf.anathema.character.impl.model.traits.CoreTraitConfiguration;
import net.sf.anathema.character.impl.model.traits.essence.EssencePoolConfiguration;
import net.sf.anathema.character.impl.model.traits.listening.CharacterTraitListening;
import net.sf.anathema.character.model.ICharacterStatistics;
import net.sf.anathema.character.model.ISpellConfiguration;
import net.sf.anathema.character.model.advance.IExperiencePointConfiguration;
import net.sf.anathema.character.model.advance.IExperiencePointConfigurationListener;
import net.sf.anathema.character.model.advance.IExperiencePointEntry;
import net.sf.anathema.character.model.charm.ICharmConfiguration;
import net.sf.anathema.character.model.charm.IComboConfiguration;
import net.sf.anathema.character.model.concept.ICharacterConcept;
import net.sf.anathema.character.model.concept.IMotivation;
import net.sf.anathema.character.model.concept.INature;
import net.sf.anathema.character.model.concept.IWillpowerRegainingConcept;
import net.sf.anathema.character.model.concept.IWillpowerRegainingConceptVisitor;
import net.sf.anathema.character.model.health.IHealthConfiguration;
import net.sf.anathema.character.model.traits.ICoreTraitConfiguration;
import net.sf.anathema.character.model.traits.essence.IEssencePoolConfiguration;
import net.sf.anathema.lib.control.change.IChangeListener;
import net.sf.anathema.lib.control.objectvalue.IObjectValueChangedListener;

public class CharacterStatistics implements ICharacterStatistics {

  private final CharacterModelContext context = new CharacterModelContext(new GenericCharacter(this, null));
  private final ICharacterTemplate characterTemplate;
  private final ICharacterConcept concept;
  private final IEssencePoolConfiguration essencePool;
  private final CharmConfiguration charms;
  private final IComboConfiguration combos;
  private final ISpellConfiguration spells;
  private final IHealthConfiguration health;
  private final IExperiencePointConfiguration experiencePoints = new ExperiencePointConfiguration();
  private final IExaltedRuleSet rules;
  private boolean experienced = false;
  private final IObjectValueChangedListener<String> motivationChangeListener = new IObjectValueChangedListener<String>() {
    @Override
    public void valueChanged(String newValue) {
      context.getCharacterListening().fireCharacterChanged();
    }
  };
  private final IChangeListener natureChangeListener = new IChangeListener() {
    @Override
    public void changeOccurred() {
      context.getCharacterListening().fireCharacterChanged();
    }
  };
  private final IChangeListener casteChangeListener = new IChangeListener() {
    @Override
    public void changeOccurred() {
      context.getCharacterListening().fireCasteChanged();
    }
  };
  private final IChangeListener ageChangeListener = new IChangeListener() {
    @Override
    public void changeOccurred() {
      context.getCharacterListening().fireCharacterChanged();
    }
  };
  private final ExtendedConfiguration extendedConfiguration = new ExtendedConfiguration(context);
  private final ICoreTraitConfiguration traitConfiguration;
  private IMagicStats[] genericStats = new IMagicStats[0];

  public CharacterStatistics(final ICharacterTemplate template, ICharacterGenerics generics, IExaltedRuleSet rules)
          throws SpellException {
    Ensure.ensureArgumentNotNull(template);
    Ensure.ensureArgumentNotNull(generics);
    Ensure.ensureArgumentNotNull(rules);
    this.rules = rules;
    this.characterTemplate = template;
    this.concept = initConcept();
    this.traitConfiguration = new CoreTraitConfiguration(template, context, generics.getBackgroundRegistry());
    new CharacterTraitListening(traitConfiguration, context.getCharacterListening()).initListening();
    this.health = new HealthConfiguration(getTraitArray(template.getToughnessControllingTraitTypes()), traitConfiguration, template.getBaseHealthProviders());
    this.charms = new CharmConfiguration(health, context, generics.getTemplateRegistry(), generics.getCharmProvider());
    initCharmListening(charms);
    this.essencePool = new EssencePoolConfiguration(template.getEssenceTemplate(),
            template.getAdditionalRules(),
            context);
    charms.initListening();
    this.combos = new ComboConfiguration(charms, context.getComboLearnStrategy(), rules.getEdition(), experiencePoints, this);
    combos.addComboConfigurationListener(new CharacterChangeComboListener(context.getCharacterListening()));
    this.spells = new SpellConfiguration(charms, context.getSpellLearnStrategy(), template, rules.getEdition());
    this.spells.addChangeListener(new IChangeListener() {
      @Override
      public void changeOccurred() {
        context.getCharacterListening().fireCharacterChanged();
      }
    });
    initExperienceListening();
    extendedConfiguration.addAdditionalModelChangeListener(new IChangeListener() {
      @Override
      public void changeOccurred() {
        context.getCharacterListening().fireCharacterChanged();
      }
    });
    if (characterTemplate.isNpcOnly()){
      setExperienced(true);
    }
  }

  private IGenericTrait[] getTraitArray(ITraitType[] types) {
    IGenericTrait[] traits = new IGenericTrait[types.length];
    for (int i = 0; i != types.length; i++)
      traits[i] = traitConfiguration.getTrait(types[i]);
    return traits;
  }

  private void initExperienceListening() {
    experiencePoints.addExperiencePointConfigurationListener(new IExperiencePointConfigurationListener() {

      @Override
      public void entryAdded(IExperiencePointEntry entry) {
        context.getCharacterListening().fireCharacterChanged();
      }

      @Override
      public void entryChanged(IExperiencePointEntry entry) {
        context.getCharacterListening().fireCharacterChanged();
      }

      @Override
      public void entryRemoved(IExperiencePointEntry entry) {
        context.getCharacterListening().fireCharacterChanged();
      }
    });
  }

  private CharacterConcept initConcept() {
    final IWillpowerRegainingConcept[] willpowerConcept = new IWillpowerRegainingConcept[1];
    rules.getEdition().accept(new IEditionVisitor() {
      @Override
      public void visitFirstEdition(IExaltedEdition visitedEdition) {
        willpowerConcept[0] = new Nature();
      }

      @Override
      public void visitSecondEdition(IExaltedEdition visitedEdition) {
        willpowerConcept[0] = new Motivation(experiencePoints);
      }
    });
    CharacterConcept characterConcept = new CharacterConcept(willpowerConcept[0]);
    characterConcept.getCaste().addChangeListener(casteChangeListener);
    characterConcept.getAge().addChangeListener(ageChangeListener);
    characterConcept.getWillpowerRegainingConcept().accept(new IWillpowerRegainingConceptVisitor() {
      @Override
      public void accept(INature nature) {
        nature.getDescription().addChangeListener(natureChangeListener);
      }

      @Override
      public void accept(IMotivation motivation) {
        motivation.getDescription().addTextChangedListener(motivationChangeListener);
      }
    });
    return characterConcept;
  }

  private void initCharmListening(ICharmConfiguration charmConfiguration) {
    charmConfiguration.addCharmLearnListener(new CharacterChangeCharmListener(context.getCharacterListening()));
  }

  @Override
  public ICharacterConcept getCharacterConcept() {
    return concept;
  }

  @Override
  public IEssencePoolConfiguration getEssencePool() {
    return essencePool;
  }

  @Override
  public ICharmConfiguration getCharms() {
    return charms;
  }

  @Override
  public IHealthConfiguration getHealth() {
    return health;
  }

  @Override
  public IComboConfiguration getCombos() {
    return combos;
  }

  @Override
  public ISpellConfiguration getSpells() {
    return spells;
  }

  @Override
  public boolean isExperienced() {
    return experienced;
  }

  @Override
  public void setExperienced(boolean experienced) {
    if (this.experienced){
      return;
    }
    this.experienced = experienced;
    context.setExperienced(experienced);
    context.getCharacterListening().fireExperiencedChanged(experienced);
  }

  @Override
  public IExperiencePointConfiguration getExperiencePoints() {
    return experiencePoints;
  }

  @Override
  public ICharacterTemplate getCharacterTemplate() {
    return characterTemplate;
  }

  @Override
  public IExaltedRuleSet getRules() {
    return rules;
  }

  @Override
  public ExtendedConfiguration getExtendedConfiguration() {
    return extendedConfiguration;
  }

  @Override
  public ICoreTraitConfiguration getTraitConfiguration() {
    return traitConfiguration;
  }

  @Override
  public ICharacterModelContext getCharacterContext() {
    return context;
  }

  public void setGenericCharmStats(IMagicStats[] genericStats) {
    this.genericStats = genericStats;
  }

  @Override
  public IMagicStats[] getGenericCharmStats() {
    return genericStats;
  }
}
package net.sf.anathema.character.generic.impl;

import net.sf.anathema.character.generic.character.IGenericCharacter;
import net.sf.anathema.character.generic.character.IGenericTraitCollection;
import net.sf.anathema.character.generic.equipment.ICharacterStatsModifiers;
import net.sf.anathema.character.generic.traits.ITraitType;
import net.sf.anathema.character.generic.traits.types.AbilityType;
import net.sf.anathema.character.generic.traits.types.AttributeType;
import net.sf.anathema.character.generic.traits.types.OtherTraitType;
import net.sf.anathema.character.generic.traits.INamedGenericTrait;
import net.sf.anathema.character.generic.type.ICharacterType;

public class CharacterUtilities {

  public static int getDodgeMdv(IGenericTraitCollection traitCollection, ICharacterStatsModifiers equipment) {
    int baseValue = getRoundDownDv(traitCollection, OtherTraitType.Willpower, AbilityType.Integrity, OtherTraitType.Essence);
    baseValue += equipment.getMDDVMod();
    return Math.max(baseValue, 0);
  }

  public static int getJoinBattle(IGenericTraitCollection traitCollection, ICharacterStatsModifiers equipment) {
    int baseValue = getTotalValue(traitCollection, AttributeType.Wits, AbilityType.Awareness);
    baseValue += equipment.getJoinBattleMod();
    return Math.max(baseValue, 1);
  }

  public static int getJoinDebate(IGenericTraitCollection traitCollection, ICharacterStatsModifiers equipment) {
    int baseValue = getTotalValue(traitCollection, AttributeType.Wits, AbilityType.Awareness);
    baseValue += equipment.getJoinDebateMod();
    return Math.max(baseValue, 1);
  }

  public static int getKnockdownThreshold(IGenericTraitCollection traitCollection) {
    int baseValue = getTotalValue(traitCollection, AttributeType.Stamina, AbilityType.Resistance);
    return Math.max(baseValue, 0);

  }

  public static int getKnockdownPool(IGenericCharacter character) {
    return getKnockdownPool(character.getTraitCollection());
  }

  public static int getKnockdownPool(IGenericTraitCollection traitCollection) {
    int attribute = getMaxValue(traitCollection, AttributeType.Dexterity, AttributeType.Stamina);
    int ability = getMaxValue(traitCollection, AbilityType.Athletics, AbilityType.Resistance);
    int pool = attribute + ability;
    return Math.max(pool, 0);
  }

  public static int getStunningThreshold(IGenericTraitCollection traitCollection) {
    int baseValue = getTotalValue(traitCollection, AttributeType.Stamina);
    return Math.max(baseValue, 0);
  }

  public static int getStunningPool(IGenericTraitCollection traitCollection) {
    int baseValue = getTotalValue(traitCollection, AttributeType.Stamina, AbilityType.Resistance);
    return Math.max(baseValue, 0);
  }

  private static int getMaxValue(IGenericTraitCollection traitCollection, ITraitType second, ITraitType first) {
    return Math.max(traitCollection.getTrait(first).getCurrentValue(),
            traitCollection.getTrait(second).getCurrentValue());
  }

// these two functions should be private, but need to limit what's calling them first
  public static int getRoundDownDv(IGenericTraitCollection traitCollection, ITraitType... types) {
    return getTotalValue(traitCollection, types) / 2;
  }
  public static int getRoundUpDv(IGenericTraitCollection traitCollection, ITraitType... types) {
    return (int) Math.ceil(( getTotalValue(traitCollection, types)) * 0.5);
  }

  public static int getTotalValue(IGenericTraitCollection traitCollection, ITraitType... types) {
    int sum = 0;
    for (ITraitType type : types) {
      sum += traitCollection.getTrait(type).getCurrentValue();
    }
    return sum;
  }

  public static int getDodgeDv(ICharacterType characterType,
                               IGenericTraitCollection traitCollection,
                               ICharacterStatsModifiers equipment) {
    
    // pass in an empty list of dodge specialties, and it will assume no dodge specialties, for a pool modifier of 0
    return getDodgeDvWithSpecialty( characterType, traitCollection, equipment, new INamedGenericTrait[0] );
  }

  public static int getDodgeDvWithSpecialty(ICharacterType characterType,
                                            IGenericTraitCollection traitCollection,
                                            ICharacterStatsModifiers equipment,
                                            INamedGenericTrait[] dodgeSpecialties) {
	int highestSpecialty = 0;
    for( INamedGenericTrait t : dodgeSpecialties)
    {
      highestSpecialty = highestSpecialty < t.getCurrentValue() ? t.getCurrentValue() : highestSpecialty;
    }
    
    // if essence >=2, add essence to the dodge pool
    int dvPool = 0;
    if(traitCollection.getTrait(OtherTraitType.Essence).getCurrentValue() > 1) {
      dvPool = getTotalValue( traitCollection, AttributeType.Dexterity, AbilityType.Dodge, OtherTraitType.Essence );
    } else
    {
      dvPool = getTotalValue( traitCollection, AttributeType.Dexterity, AbilityType.Dodge);
    }
    
    dvPool += highestSpecialty; // + equipment.getDDVPoolMod()
    
    // if essence user, round up, else round down
    int dv = 0;
    if(characterType.isEssenceUser()) {
      dv = (int) Math.ceil(dvPool * 0.5);
    } else {
      dv = dvPool / 2;
    }
    
    dv += equipment.getDDVMod() + equipment.getMobilityPenalty();
    
    return Math.max(dv, 0);
  }
  
}
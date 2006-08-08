package net.sf.anathema.character.impl.model.traits.listening;

import java.util.Arrays;

import net.sf.anathema.character.library.trait.IModifiableTrait;
import net.sf.anathema.lib.control.intvalue.IIntValueChangedListener;

public class WillpowerListening {

  public void initListening(final IModifiableTrait willpower, final IModifiableTrait[] virtues) {
    for (IModifiableTrait virtue : virtues) {
      virtue.addCreationPointListener(new IIntValueChangedListener() {
        public void valueChanged(int newValue) {
          updateWillpowerCreationRange(willpower, virtues);
        }
      });
    }
    updateWillpowerCreationRange(willpower, virtues);
  }
  
  private void updateWillpowerCreationRange(final IModifiableTrait willpower, final IModifiableTrait[] virtues) {
    int newInitialValue = Math.min(calculateAbsoluteMinimalValue(virtues), willpower.getMaximalValue());
    int newUpperValue = Math.min(calculateUpperValue(virtues), willpower.getMaximalValue());
    willpower.setModifiedCreationRange(newInitialValue, newUpperValue);
  }

  private int calculateAbsoluteMinimalValue(final IModifiableTrait[] virtues) {
    int[] virtueCreationValues = orderVirtueCreationValuesAscending(virtues);
    return virtueCreationValues[virtueCreationValues.length - 1]
        + virtueCreationValues[virtueCreationValues.length - 2];
  }

  private int[] orderVirtueCreationValuesAscending(final IModifiableTrait[] virtues) {
    int[] virtueValues = new int[virtues.length];
    for (int index = 0; index < virtueValues.length; index++) {
      virtueValues[index] = virtues[index].getCreationValue();
    }
    Arrays.sort(virtueValues);
    return virtueValues;
  }

  private int calculateUpperValue(final IModifiableTrait[] virtues) {
    int[] virtueValues = orderVirtueCreationValuesAscending(virtues);
    return Math.max(virtueValues[virtueValues.length - 1] + virtueValues[virtueValues.length - 2], 8);
  }
}
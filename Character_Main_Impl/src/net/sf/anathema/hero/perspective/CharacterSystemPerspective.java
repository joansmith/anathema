package net.sf.anathema.hero.perspective;

import net.sf.anathema.character.main.perspective.CharacterGridPresenter;
import net.sf.anathema.character.main.perspective.CharacterStackBridge;
import net.sf.anathema.character.main.perspective.CharacterStackPresenter;
import net.sf.anathema.character.main.perspective.ShowOnSelect;
import net.sf.anathema.framework.IApplicationModel;
import net.sf.anathema.framework.view.perspective.Container;
import net.sf.anathema.framework.view.perspective.Perspective;
import net.sf.anathema.framework.view.perspective.PerspectiveAutoCollector;
import net.sf.anathema.framework.view.perspective.PerspectiveToggle;
import net.sf.anathema.initialization.reflections.Weight;
import net.sf.anathema.lib.file.RelativePath;
import net.sf.anathema.lib.resources.Resources;
import net.sf.anathema.swing.hero.perspective.CharacterStackSwingBridge;
import net.sf.anathema.swing.hero.perspective.CharacterSystemView;
import net.sf.anathema.swing.hero.perspective.CharacterViewFactory;

@PerspectiveAutoCollector
@Weight(weight = 1)
public class CharacterSystemPerspective implements Perspective {

  @Override
  public void configureToggle(PerspectiveToggle toggle) {
    toggle.setIcon(new RelativePath("icons/King-icon.png"));
    toggle.setTooltip("CharacterSystem.Perspective.Name");
  }

  @Override
  public void initContent(Container container, IApplicationModel applicationModel, Resources resources) {
    CharacterSystemModel systemModel = new CharacterSystemModel(applicationModel);
    CharacterSystemView view = new CharacterSystemView();
    CharacterViewFactory viewFactory = new CharacterViewFactory(resources, applicationModel);
    CharacterStackBridge bridge = new CharacterStackSwingBridge(viewFactory, view.getStackView());
    CharacterStackPresenter stackPresenter = new CharacterStackPresenter(bridge, systemModel);
    ShowOnSelect showOnSelect = new ShowOnSelect(stackPresenter);
    CharacterGridPresenter gridPresenter = new CharacterGridPresenter(systemModel, view.getGridView(), showOnSelect, resources);
    gridPresenter.initPresentation();
    new InteractionPresenter(systemModel, view.getInteractionView(), resources, view.getGridView(), showOnSelect).initPresentation();
    container.setSwingContent(view.getComponent());
  }
}
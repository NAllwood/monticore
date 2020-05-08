/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java;

import de.monticore.ast.ASTNode;
import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd.facade.CDConstructorFacade;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.facade.CDParameterFacade;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.types.MCTypeFacade;

public abstract class AbstractDecorator {

  /**
   * Do not use for creation of new Decorators
   * Decide if your new Decorator is a Creator or a Transformer, to overwrite the correct decorate method
   * Only a class to sum up general Decorator functionality
   **/

  protected final GlobalExtensionManagement glex;

  private boolean templatesEnabled;

  private final MCTypeFacade mcTypeFacade;

  private final CDAttributeFacade cdAttributeFacade;

  private final CDConstructorFacade cdConstructorFacade;

  private final CDMethodFacade cdMethodFacade;

  private final CDParameterFacade cdParameterFacade;

  private final DecorationHelper decorationHelper;

  public AbstractDecorator() {
    this(null);
  }

  public AbstractDecorator(final GlobalExtensionManagement glex) {
    this(glex,
        MCTypeFacade.getInstance(),
        CDAttributeFacade.getInstance(),
        CDConstructorFacade.getInstance(),
        CDMethodFacade.getInstance(),
        CDParameterFacade.getInstance(),
        DecorationHelper.getInstance()
    );
  }

  public AbstractDecorator(final GlobalExtensionManagement glex,
                           final MCTypeFacade mcTypeFacade,
                           final CDAttributeFacade cdAttributeFacade,
                           final CDConstructorFacade cdConstructorFacade,
                           final CDMethodFacade cdMethodFacade,
                           final CDParameterFacade cdParameterFacade,
                           final DecorationHelper decorationHelper) {
    this.glex = glex;
    this.templatesEnabled = true;
    this.mcTypeFacade = mcTypeFacade;
    this.cdAttributeFacade = cdAttributeFacade;
    this.cdConstructorFacade = cdConstructorFacade;
    this.cdMethodFacade = cdMethodFacade;
    this.cdParameterFacade = cdParameterFacade;
    this.decorationHelper = decorationHelper;
  }

  public void enableTemplates() {
    this.templatesEnabled = true;
  }

  public void disableTemplates() {
    this.templatesEnabled = false;
  }

  private boolean templatesEnabled() {
    return this.templatesEnabled;
  }

  protected void replaceTemplate(String template, ASTNode node, HookPoint hookPoint) {
    if (this.templatesEnabled()) {
      this.glex.replaceTemplate(template, node, hookPoint);
    }
  }

  protected MCTypeFacade getMCTypeFacade() {
    return this.mcTypeFacade;
  }

  protected CDAttributeFacade getCDAttributeFacade() {
    return this.cdAttributeFacade;
  }

  protected CDConstructorFacade getCDConstructorFacade() {
    return this.cdConstructorFacade;
  }

  protected CDMethodFacade getCDMethodFacade() {
    return this.cdMethodFacade;
  }

  protected CDParameterFacade getCDParameterFacade() {
    return this.cdParameterFacade;
  }

  public DecorationHelper getDecorationHelper() {
    return decorationHelper;
  }
}

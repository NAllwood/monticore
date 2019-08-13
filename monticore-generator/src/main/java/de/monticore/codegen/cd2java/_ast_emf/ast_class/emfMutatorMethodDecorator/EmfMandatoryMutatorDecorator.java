package de.monticore.codegen.cd2java._ast_emf.ast_class.emfMutatorMethodDecorator;

import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.mutator.MandatoryMutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.se_rwth.commons.StringTransformations;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast_emf.EmfConstants.PACKAGE_SUFFIX;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class EmfMandatoryMutatorDecorator extends MandatoryMutatorDecorator {
  private final ASTService astService;

  private String className;

  public EmfMandatoryMutatorDecorator(GlobalExtensionManagement glex, ASTService astService) {
    super(glex);
    this.astService = astService;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  @Override
  protected ASTCDMethod createSetter(final ASTCDAttribute attribute) {
    String packageName = astService.getCDName() + PACKAGE_SUFFIX;
    //todo find better util than the DecorationHelper
    String methodName = String.format(SET, StringTransformations.capitalize(DecorationHelper.getNativeAttributeName(attribute.getName())));
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC, methodName, this.getCDParameterFacade().createParameters(attribute));
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast_emf.ast_class.Set",
        packageName, className, attribute));
    return method;
  }
}

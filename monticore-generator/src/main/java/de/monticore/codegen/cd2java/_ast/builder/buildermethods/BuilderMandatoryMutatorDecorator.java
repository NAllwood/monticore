/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.builder.buildermethods;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.mutator.MandatoryMutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;
import org.apache.commons.lang3.StringUtils;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class BuilderMandatoryMutatorDecorator extends MandatoryMutatorDecorator {
  private final ASTMCType builderType;

  public BuilderMandatoryMutatorDecorator(final GlobalExtensionManagement glex,
                                          final ASTMCType builderType) {
    super(glex);
    this.builderType = builderType;
  }

  @Override
  protected ASTCDMethod createSetter(final ASTCDAttribute ast) {
    String name = String.format(SET, StringUtils.capitalize(DecorationHelper.getNativeAttributeName(ast.getName())));
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(builderType).build();
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC, returnType, name, this.getCDParameterFacade().createParameters(ast));
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast.builder.Set", ast));
    return method;
  }
}

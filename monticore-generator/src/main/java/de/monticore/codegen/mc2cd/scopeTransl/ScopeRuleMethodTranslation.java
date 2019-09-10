package de.monticore.codegen.mc2cd.scopeTransl;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTMethod;
import de.monticore.grammar.grammar._ast.ASTMethodParameter;
import de.monticore.grammar.grammar._ast.ASTScopeRule;
import de.monticore.grammar.grammar_withconcepts._ast.ASTAction;
import de.monticore.statements.mccommonstatements._ast.ASTBlockStatement;
import de.monticore.types.FullGenericTypesPrinter;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

public class ScopeRuleMethodTranslation implements UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  private GlobalExtensionManagement glex;

  /**
   * Constructor for
   * de.monticore.codegen.mc2cd.transl.MethodTranslation
   *
   * @param glex
   */
  public ScopeRuleMethodTranslation(GlobalExtensionManagement glex) {
    this.glex = glex;
  }

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    for (Link<ASTScopeRule, ASTCDClass> link : rootLink.getLinks(ASTScopeRule.class,
        ASTCDClass.class)) {
      for (ASTMethod method : link.source().getMethodList()) {
        link.target().getCDMethodList().add(translateASTMethodToASTCDMethod(method));
      }
    }

    return rootLink;
  }

  private ASTCDMethod createSimpleCDMethod(ASTMethod method) {
    ASTCDMethod cdMethod = CD4AnalysisNodeFactory.createASTCDMethod();
    cdMethod.setModifier(TransformationHelper.createPublicModifier());
    cdMethod.setName(method.getName());
    String dotSeparatedName = FullGenericTypesPrinter.printReturnType(method.getMCReturnType());
    cdMethod.setMCReturnType(TransformationHelper.createReturnType(dotSeparatedName));
    for (ASTMethodParameter param : method.getMethodParameterList()) {
      String typeName = FullGenericTypesPrinter.printType(param.getType());
      cdMethod.getCDParameterList().add(TransformationHelper.createParameter(typeName, param.getName()));
    }
    return cdMethod;
  }

  private void addMethodBodyStereotype(ASTModifier modifier, StringBuilder code){
    // to save the body in the cd
    // todo think of better version
    TransformationHelper.addStereotypeValue(modifier,
        MC2CDStereotypes.METHOD_BODY.toString(),
        code.toString());
  }

  private ASTCDMethod translateASTMethodToASTCDMethod(ASTMethod method) {
    ASTCDMethod cdMethod = createSimpleCDMethod(method);
    if (method.getBody() instanceof ASTAction) {
      StringBuilder code = new StringBuilder();
      for (ASTBlockStatement action : ((ASTAction) method.getBody()).getBlockStatementList()) {
        code.append(GeneratorHelper.getMcPrettyPrinter().prettyprint(action));
      }
      addMethodBodyStereotype(cdMethod.getModifier(), code);
    }
    return cdMethod;
  }
}

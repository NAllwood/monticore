package de.monticore.codegen.cd2java._symboltable.symboltablecreator;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4code._ast.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.DEQUE_TYPE;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;

public class SymbolTableCreatorDelegatorDecorator extends AbstractCreator<ASTCDCompilationUnit, Optional<ASTCDClass>> {

  protected final SymbolTableService symbolTableService;

  protected final VisitorService visitorService;

  public SymbolTableCreatorDelegatorDecorator(final GlobalExtensionManagement glex,
                                              final SymbolTableService symbolTableService,
                                              final VisitorService visitorService) {
    super(glex);
    this.symbolTableService = symbolTableService;
    this.visitorService = visitorService;
  }

  @Override
  public Optional<ASTCDClass> decorate(ASTCDCompilationUnit input) {
    Optional<String> startProd = symbolTableService.getStartProdASTFullName(input.getCDDefinition());
    if (startProd.isPresent()) {
      String astFullName = startProd.get();
      String symbolTableCreatorDelegatorName = symbolTableService.getSymbolTableCreatorDelegatorSimpleName();
      String symbolTableCreatorName = symbolTableService.getSymbolTableCreatorSimpleName();
      String scopeInterface = symbolTableService.getScopeInterfaceFullName();
      String globalScopeName = symbolTableService.getGlobalScopeInterfaceFullName();
      String simpleName = symbolTableService.getCDName();
      String artifactScopeName = symbolTableService.getArtifactScopeFullName();
      String delegatorVisitorName = visitorService.getDelegatorVisitorFullName();
      String dequeType = String.format(DEQUE_TYPE, scopeInterface);

      ASTCDClass symTabCreatorDelegator = CD4CodeMill.cDClassBuilder()
          .setName(symbolTableCreatorDelegatorName)
          .setModifier(PUBLIC.build())
          .setSuperclass(getCDTypeFacade().createQualifiedType(delegatorVisitorName))
          .addCDConstructor(createConstructor(symbolTableCreatorDelegatorName, globalScopeName, symbolTableCreatorName, simpleName))
          .addCDAttribute(createScopeStackAttribute(dequeType))
          .addCDAttribute(createSymbolTableCreatorAttribute(symbolTableCreatorName))
          .addCDAttribute(createGlobalScopeAttribute(globalScopeName))
          .addCDMethod(createCreateFromASTMethod(astFullName, artifactScopeName))
          .build();
      return Optional.ofNullable(symTabCreatorDelegator);
    }
    return Optional.empty();
  }

  protected ASTCDConstructor createConstructor(String symTabCreatorDelegator, String globalScope,
                                               String symbolTableCreator, String simpleName) {
    List<CDDefinitionSymbol> superCDsTransitive = symbolTableService.getSuperCDsTransitive();
    Map<String, String> superSymTabCreator = new HashMap<>();
    for (CDDefinitionSymbol cdDefinitionSymbol : superCDsTransitive) {
      if (cdDefinitionSymbol.getAstNode().isPresent() && symbolTableService.hasStartProd(cdDefinitionSymbol.getAstNode().get())) {
        superSymTabCreator.put(cdDefinitionSymbol.getName(), symbolTableService.getSuperSTCForSubSTCSimpleName(cdDefinitionSymbol));
      }
    }
    ASTCDParameter globalScopeParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(globalScope), "globalScope");
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), symTabCreatorDelegator, globalScopeParam);
    this.replaceTemplate(EMPTY_BODY, constructor, new TemplateHookPoint("_symboltable.symboltablecreatordelegator.Constructor",
        superSymTabCreator, symbolTableCreator, simpleName));
    return constructor;
  }

  protected ASTCDAttribute createScopeStackAttribute(String dequeType) {
    ASTCDAttribute scopeStack = getCDAttributeFacade().createAttribute(PROTECTED, dequeType, "scopeStack");
    this.replaceTemplate(VALUE, scopeStack, new StringHookPoint("= new java.util.ArrayDeque<>()"));
    return scopeStack;
  }

  protected ASTCDAttribute createSymbolTableCreatorAttribute(String symbolTableCreator) {
    return getCDAttributeFacade().createAttribute(PROTECTED_FINAL, symbolTableCreator, "symbolTable");
  }

  protected ASTCDAttribute createGlobalScopeAttribute(String globalScope) {
    return getCDAttributeFacade().createAttribute(PROTECTED, globalScope, "globalScope");
  }

  protected ASTCDMethod createCreateFromASTMethod(String startProd, String artifactScope) {
    ASTCDParameter startProdParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(startProd), "rootNode");
    ASTCDMethod createFromAST = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createQualifiedType(artifactScope),
        "createFromAST", startProdParam);
    this.replaceTemplate(EMPTY_BODY, createFromAST, new TemplateHookPoint("_symboltable.symboltablecreatordelegator.CreateFromAST",
        artifactScope));
    return createFromAST;
  }
}

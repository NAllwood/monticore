/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.language;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._parser.ParserService;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mccollectiontypes._ast.ASTMCSetType;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.CoreTemplates.ANNOTATIONS;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;

/**
 * created language class from a grammar
 */
public class LanguageDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  protected SymbolTableService symbolTableService;

  protected ParserService parserService;

  protected AccessorDecorator accessorDecorator;

  /**
   * flag added to define if the Language class was overwritten with the TOP mechanism
   * if top mechanism was used, must use setter to set flag true, before the decoration
   */
  protected boolean isLanguageTop = false;

  protected static final String TEMPLATE_PATH = "_symboltable.language.";

  public LanguageDecorator(final GlobalExtensionManagement glex,
                           final SymbolTableService symbolTableService,
                           final ParserService parserService,
                           final AccessorDecorator accessorDecorator) {
    super(glex);
    this.symbolTableService = symbolTableService;
    this.parserService = parserService;
    this.accessorDecorator = accessorDecorator;
  }

  @Override
  public ASTCDClass decorate(ASTCDCompilationUnit input) {
    String languageClassName = symbolTableService.getLanguageClassSimpleName();
    String modelLoaderClassName = symbolTableService.getModelLoaderClassSimpleName();
    ASTMCObjectType iModelingLanguage = getMCTypeFacade().createQualifiedType(I_MODELING_LANGUAGE);
    String symTabMillFullName = symbolTableService.getMillFullName();

    List<ASTCDType> symbolDefiningProds = symbolTableService.getSymbolDefiningProds(input.getCDDefinition());
    symbolDefiningProds.addAll(symbolTableService.getSymbolDefiningSuperProds());

    ASTCDAttribute modelLoaderAttribute = createModelLoaderAttribute(modelLoaderClassName);
    List<ASTCDMethod> modelLoaderMethods = accessorDecorator.decorate(modelLoaderAttribute);
    ASTCDAttribute nameAttribute = createNameAttribute();
    List<ASTCDMethod> nameMethods = accessorDecorator.decorate(nameAttribute);
    ASTCDAttribute fileExtensionAttribute = createFileExtensionAttribute();
    List<ASTCDMethod> fileExtensionMethods = accessorDecorator.decorate(fileExtensionAttribute);

    ASTModifier modifier = PUBLIC_ABSTRACT.build();
    symbolTableService.addDeprecatedStereotype(modifier, Optional.empty());

    ASTCDClass languageClass = CD4AnalysisMill.cDClassBuilder()
        .setName(languageClassName)
        .setModifier(modifier)
        .addInterface(iModelingLanguage)
        .addCDConstructor(createConstructor(languageClassName))
        .addCDAttribute(modelLoaderAttribute)
        .addCDAttribute(nameAttribute)
        .addCDAttribute(fileExtensionAttribute)
        .addAllCDMethods(modelLoaderMethods)
        .addAllCDMethods(nameMethods)
        .addAllCDMethods(fileExtensionMethods)
        .addCDMethod(createGetSymbolTableCreatorMethod(symTabMillFullName))
        .addCDMethod(createProvideModelLoaderMethod(modelLoaderClassName, symTabMillFullName))
        .addAllCDMethods(createCalculateModelNameMethods(symbolDefiningProds))
        .build();
    Optional<ASTCDMethod> getParserMethod = createGetParserMethod(input.getCDDefinition());
    getParserMethod.ifPresent(languageClass::addCDMethod);

    return languageClass;
  }

  protected ASTCDConstructor createConstructor(String languageClassName) {
    ASTCDParameter langName = getCDParameterFacade().createParameter(String.class, "langName");
    ASTCDParameter fileEnding = getCDParameterFacade().createParameter(String.class, "fileEnding");

    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), languageClassName, langName, fileEnding);
    this.replaceTemplate(EMPTY_BODY, constructor, new TemplateHookPoint(TEMPLATE_PATH + "ConstructorLanguage"));
    return constructor;
  }

  protected ASTCDAttribute createModelLoaderAttribute(String modelLoaderName) {
    return getCDAttributeFacade().createAttribute(PRIVATE, modelLoaderName, MODEL_LOADER_VAR);
  }

  protected ASTCDAttribute createNameAttribute() {
    return getCDAttributeFacade().createAttribute(PRIVATE, String.class, NAME_VAR);
  }

  protected ASTCDAttribute createFileExtensionAttribute() {
    return getCDAttributeFacade().createAttribute(PRIVATE, String.class, "fileExtension");
  }

  protected Optional<ASTCDMethod> createGetParserMethod(ASTCDDefinition astcdDefinition) {
    if (!(astcdDefinition.isPresentModifier() && symbolTableService.hasComponentStereotype(astcdDefinition.getModifier()))) {
      String parserClass = parserService.getParserClassFullName();
      ASTCDMethod getParserMethod = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createQualifiedType(parserClass), "getParser");
      this.replaceTemplate(EMPTY_BODY, getParserMethod, new StringHookPoint("return new " + parserClass + "();"));
      return Optional.ofNullable(getParserMethod);
    }
    return Optional.empty();
  }

  protected ASTCDMethod createGetSymbolTableCreatorMethod(String symTabMillFullName) {
    String symbolTableCreatorDelegatorName = symbolTableService.getSymbolTableCreatorDelegatorSimpleName();
    String globalScopeFullName = symbolTableService.getGlobalScopeFullName();
    ASTCDParameter enclosingScope = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(globalScopeFullName), ENCLOSING_SCOPE_VAR);

    ASTCDMethod getSymbolTableCreatorMethod = getCDMethodFacade().createMethod(PUBLIC,
        getMCTypeFacade().createQualifiedType(symbolTableCreatorDelegatorName), "getSymbolTableCreator", enclosingScope);
    this.replaceTemplate(EMPTY_BODY, getSymbolTableCreatorMethod, new StringHookPoint(" return " + symTabMillFullName + "." +
        StringTransformations.uncapitalize(symbolTableCreatorDelegatorName) + "Builder().setGlobalScope(" + ENCLOSING_SCOPE_VAR + ").build();"));
    return getSymbolTableCreatorMethod;
  }

  protected ASTCDMethod createProvideModelLoaderMethod(String modelLoaderName, String symTabMillFullName) {
    ASTCDMethod provideModelLoaderMethod = getCDMethodFacade().createMethod(PROTECTED,
        getMCTypeFacade().createQualifiedType(modelLoaderName), "provideModelLoader");
    if (isLanguageTop()) {
      provideModelLoaderMethod.getModifier().setAbstract(true);
    } else {
      this.replaceTemplate(EMPTY_BODY, provideModelLoaderMethod, new StringHookPoint("return " + symTabMillFullName + "." +
          StringTransformations.uncapitalize(modelLoaderName) + "Builder().setModelingLanguage(this).build();"));    }
    return provideModelLoaderMethod;
  }

  protected List<ASTCDMethod> createCalculateModelNameMethods(List<ASTCDType> symbolProds) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDType symbolProd : symbolProds) {
      String simpleName = symbolTableService.removeASTPrefix(symbolProd);
      ASTMCSetType setTypeOfString = getMCTypeFacade().createSetTypeOf(String.class);
      ASTCDParameter nameParam = getCDParameterFacade().createParameter(String.class, NAME_VAR);
      ASTCDMethod method = getCDMethodFacade().createMethod(PROTECTED, setTypeOfString,
          String.format("calculateModelNamesFor%s", simpleName), nameParam);
      this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "CalculateModelNamesFor"));
      methodList.add(method);
    }
    return methodList;
  }

  public boolean isLanguageTop() {
    return isLanguageTop;
  }

  public void setLanguageTop(boolean languageTop) {
    isLanguageTop = languageTop;
  }
}

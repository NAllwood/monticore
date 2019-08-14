/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.mill;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.ast.AstGeneratorHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.CollectionTypesPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILDER_SUFFIX;
import static de.monticore.codegen.cd2java._ast.mill.MillConstants.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;

public class MillDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  protected final AbstractService<?> service;

  public MillDecorator(final GlobalExtensionManagement glex, final AbstractService service) {
    super(glex);
    this.service = service;
  }

  public ASTCDClass decorate(final ASTCDCompilationUnit compilationUnit) {
    //filter out all classes that are abstract and remove AST prefix
    List<ASTCDClass> classList = compilationUnit.getCDDefinition().deepClone().getCDClassList()
        .stream()
        .filter(ASTCDClassTOP::isPresentModifier)
        .filter(x -> !x.getModifier().isAbstract())
        .collect(Collectors.toList());

    String millClassName = compilationUnit.getCDDefinition().getName() + MILL_SUFFIX;
    ASTMCType millType = this.getCDTypeFacade().createTypeByDefinition(millClassName);

    List<CDDefinitionSymbol> superSymbolList = service.getSuperCDs();

    ASTCDConstructor constructor = this.getCDConstructorFacade().createConstructor(PROTECTED, millClassName);

    ASTCDAttribute millAttribute = this.getCDAttributeFacade().createAttribute(PROTECTED_STATIC, millType, MILL_INFIX);

    //add mill attribute for each class
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    for (String attributeName : getAttributeNameList(classList)) {
      attributeList.add(this.getCDAttributeFacade().createAttribute(PROTECTED_STATIC, millType, MILL_INFIX + attributeName));
    }

    //add all standard methods
    ASTCDMethod getMillMethod = addGetMillMethods(millType);

    ASTCDMethod initMeMethod = addInitMeMethod(millType, classList);

    ASTCDMethod initMethod = addInitMethod(millType, superSymbolList);

    ASTCDMethod resetMethod = addResetMethod(classList, superSymbolList);

    List<ASTCDMethod> builderMethodsList = addBuilderMethods(classList);

    //add builder methods for each class
    List<ASTCDMethod> superMethodsList = addSuperBuilderMethods(superSymbolList, classList);

    return CD4AnalysisMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(millClassName)
        .addCDAttribute(millAttribute)
        .addAllCDAttributes(attributeList)
        .addCDConstructor(constructor)
        .addCDMethod(getMillMethod)
        .addCDMethod(initMeMethod)
        .addCDMethod(initMethod)
        .addCDMethod(resetMethod)
        .addAllCDMethods(builderMethodsList)
        .addAllCDMethods(superMethodsList)
        .build();
  }

  protected List<String> getAttributeNameList(List<ASTCDClass> astcdClasses) {
    List<String> attributeNames = new ArrayList<>();
    for (ASTCDClass astcdClass : astcdClasses) {
      attributeNames.add(astcdClass.getName());
    }
    return attributeNames;
  }

  protected ASTCDMethod addGetMillMethods(ASTMCType millType) {
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(millType).build();
    ASTCDMethod getMillMethod = this.getCDMethodFacade().createMethod(PROTECTED_STATIC, returnType, GET_MILL);
    this.replaceTemplate(EMPTY_BODY, getMillMethod, new TemplateHookPoint("_ast.mill.GetMillMethod", CollectionTypesPrinter.printType(millType)));
    return getMillMethod;
  }

  protected ASTCDMethod addInitMeMethod(ASTMCType millType, List<ASTCDClass> astcdClassList) {
    ASTCDParameter astcdParameter = getCDParameterFacade().createParameter(millType, "a");
    ASTCDMethod initMeMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, INIT_ME, astcdParameter);
    this.replaceTemplate(EMPTY_BODY, initMeMethod, new TemplateHookPoint("_ast.mill.InitMeMethod", getAttributeNameList(astcdClassList)));
    return initMeMethod;
  }

  protected ASTCDMethod addInitMethod(ASTMCType millType, List<CDDefinitionSymbol> superSymbolList) {
    ASTCDMethod initMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, INIT);
    this.replaceTemplate(EMPTY_BODY, initMethod, new TemplateHookPoint("_ast.mill.InitMethod", CollectionTypesPrinter.printType(millType), superSymbolList));
    return initMethod;
  }

  protected ASTCDMethod addResetMethod(List<ASTCDClass> astcdClassList, List<CDDefinitionSymbol> superSymbolList) {
    ASTCDMethod resetMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, RESET);
    this.replaceTemplate(EMPTY_BODY, resetMethod, new TemplateHookPoint("_ast.mill.ResetMethod", getAttributeNameList(astcdClassList), superSymbolList));
    return resetMethod;
  }

  protected List<ASTCDMethod> addBuilderMethods(List<ASTCDClass> astcdClassList) {
    List<ASTCDMethod> builderMethodsList = new ArrayList<>();

    for (ASTCDClass astcdClass : astcdClassList) {
      String astName = astcdClass.getName();
      ASTMCReturnType builderType = MCBasicTypesMill.mCReturnTypeBuilder().
              setMCType(this.getCDTypeFacade().createQualifiedType(astName + BUILDER_SUFFIX)).build();
      String methodName = StringTransformations.uncapitalize(astName.replaceFirst("AST", "")) + BUILDER_SUFFIX;

      // add public static Method for Builder
      ASTCDMethod builderMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, builderType, methodName);
      builderMethodsList.add(builderMethod);
      this.replaceTemplate(EMPTY_BODY, builderMethod, new TemplateHookPoint("_ast.mill.BuilderMethod", astName, methodName));

      // add protected Method for Builder
      ASTCDMethod protectedMethod = this.getCDMethodFacade().createMethod(PROTECTED, builderType, "_" + methodName);
      builderMethodsList.add(protectedMethod);
      this.replaceTemplate(EMPTY_BODY, protectedMethod, new TemplateHookPoint("_ast.mill.ProtectedBuilderMethod", CollectionTypesPrinter.printReturnType(builderType)));
    }

    return builderMethodsList;
  }

  protected List<ASTCDMethod> addSuperBuilderMethods(List<CDDefinitionSymbol> superSymbolList, List<ASTCDClass> classList) {
    List<ASTCDMethod> superMethods = new ArrayList<>();
    //get super symbols
    for (CDDefinitionSymbol superSymbol : superSymbolList) {
      Optional<ASTCDDefinition> astNode = superSymbol.getAstNode();
      if (astNode.isPresent()) {
        //get super cddefinition
        ASTCDDefinition superDefinition = astNode.get().deepClone();
        //filter out all abstract classes
        List<ASTCDClass> copiedList = superDefinition.getCDClassList()
            .stream()
            .filter(ASTCDClassTOP::isPresentModifier)
            .filter(x -> !x.getModifier().isAbstract())
            .collect(Collectors.toList());

        for (ASTCDClass superClass : copiedList) {
          if (!service.isClassOverwritten(superClass, classList)) {
            String packageName = superSymbol.getFullName().toLowerCase() + AstGeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT;
            ASTMCReturnType superAstType = MCBasicTypesMill.mCReturnTypeBuilder().
                    setMCType(this.getCDTypeFacade().createQualifiedType(packageName + superClass.getName() + BUILDER_SUFFIX)).build();
            String methodName = StringTransformations.uncapitalize(superClass.getName().replaceFirst("AST", "")) + BUILDER_SUFFIX;

            //add builder method
            ASTCDMethod createDelegateMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, superAstType, methodName);
            if (!service.isMethodAlreadyDefined(createDelegateMethod.getName(), superMethods)) {
              this.replaceTemplate(EMPTY_BODY, createDelegateMethod, new TemplateHookPoint("_ast.mill.BuilderDelegatorMethod", packageName + superSymbol.getName(), methodName));
              superMethods.add(createDelegateMethod);
            }
          }
        }
      }
    }
    return superMethods;
  }

}

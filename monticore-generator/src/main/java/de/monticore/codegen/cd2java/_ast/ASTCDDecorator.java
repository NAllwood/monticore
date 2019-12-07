/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java._ast.ast_class.ASTConstants;
import de.monticore.codegen.cd2java._ast.ast_class.ASTFullDecorator;
import de.monticore.codegen.cd2java._ast.ast_interface.ASTLanguageInterfaceDecorator;
import de.monticore.codegen.cd2java._ast.ast_interface.FullASTInterfaceDecorator;
import de.monticore.codegen.cd2java._ast.builder.ASTBuilderDecorator;
import de.monticore.codegen.cd2java._ast.constants.ASTConstantsDecorator;
import de.monticore.codegen.cd2java._ast.enums.EnumDecorator;
import de.monticore.codegen.cd2java._ast.factory.NodeFactoryDecorator;
import de.monticore.codegen.cd2java._ast.mill.MillDecorator;
import de.monticore.codegen.cd2java._ast.mill.MillForSuperDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.*;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PREFIX;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.NODE_SUFFIX;

/**
 * combines all decorators to create all classes, interfaces and enums for the _ast package
 * returns them all combined in ASTCDCompilationUnit
 */
public class ASTCDDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDCompilationUnit> {

  protected final ASTFullDecorator astFullDecorator;

  protected final ASTLanguageInterfaceDecorator astLanguageInterfaceDecorator;

  protected final ASTBuilderDecorator astBuilderDecorator;

  protected final NodeFactoryDecorator nodeFactoryDecorator;

  protected final MillDecorator millDecorator;

  protected final MillForSuperDecorator millForSuperDecorator;

  protected final ASTConstantsDecorator astConstantsDecorator;

  protected final EnumDecorator enumDecorator;

  protected final FullASTInterfaceDecorator astInterfaceDecorator;

  public ASTCDDecorator(final GlobalExtensionManagement glex,
                        final ASTFullDecorator astFullDecorator,
                        final ASTLanguageInterfaceDecorator astLanguageInterfaceDecorator,
                        final ASTBuilderDecorator astBuilderDecorator,
                        final NodeFactoryDecorator nodeFactoryDecorator,
                        final MillDecorator millDecorator,
                        final MillForSuperDecorator millForSuperDecorator,
                        final ASTConstantsDecorator astConstantsDecorator,
                        final EnumDecorator enumDecorator,
                        final FullASTInterfaceDecorator astInterfaceDecorator) {
    super(glex);
    this.astFullDecorator = astFullDecorator;
    this.astLanguageInterfaceDecorator = astLanguageInterfaceDecorator;
    this.astBuilderDecorator = astBuilderDecorator;
    this.nodeFactoryDecorator = nodeFactoryDecorator;
    this.millDecorator = millDecorator;
    this.millForSuperDecorator = millForSuperDecorator;
    this.astConstantsDecorator = astConstantsDecorator;
    this.enumDecorator = enumDecorator;
    this.astInterfaceDecorator = astInterfaceDecorator;
  }

  @Override
  public ASTCDCompilationUnit decorate(final ASTCDCompilationUnit ast) {
    List<String> astPackage = new ArrayList<>(ast.getPackageList());
    astPackage.addAll(Arrays.asList(ast.getCDDefinition().getName().toLowerCase(), ASTConstants.AST_PACKAGE));

    ASTCDDefinition astCD = CD4AnalysisMill.cDDefinitionBuilder()
        .setName(ast.getCDDefinition().getName())
        .addAllCDClasss(createASTClasses(ast))
        .addAllCDClasss(createASTBuilderClasses(ast))
        .addCDClass(createNodeFactoryClass(ast))
        .addCDClass(createMillClass(ast))
        .addAllCDClasss(createMillForSuperClasses(ast))
        .addCDClass(createASTConstantsClass(ast))
        .addAllCDInterfaces(createASTInterfaces(ast))
        .addCDInterface(createLanguageInterface(ast))
        .addAllCDEnums(createEnums(ast))
        .build();

    // change the package and add deprecated annotations to all classes, interfaces, enums
    for (ASTCDClass cdClass : astCD.getCDClassList()) {
      this.replaceTemplate(PACKAGE, cdClass, createPackageHookPoint(astPackage));
      this.replaceTemplate(ANNOTATIONS, cdClass, createAnnotationsHookPoint(cdClass.getModifierOpt()));
    }

    for (ASTCDInterface cdInterface : astCD.getCDInterfaceList()) {
      this.replaceTemplate(CoreTemplates.PACKAGE, cdInterface, createPackageHookPoint(astPackage));
      this.replaceTemplate(ANNOTATIONS, cdInterface, createAnnotationsHookPoint(cdInterface.getModifierOpt()));
    }

    for (ASTCDEnum cdEnum : astCD.getCDEnumList()) {
      this.replaceTemplate(CoreTemplates.PACKAGE, cdEnum, createPackageHookPoint(astPackage));
      this.replaceTemplate(ANNOTATIONS, cdEnum, createAnnotationsHookPoint(cdEnum.getModifierOpt()));
    }

    return CD4AnalysisMill.cDCompilationUnitBuilder()
        .setPackageList(astPackage)
        .setCDDefinition(astCD)
        .build();
  }

  protected List<ASTCDClass> createASTClasses(final ASTCDCompilationUnit ast) {
    List<ASTCDClass> astcdClassList = new ArrayList<>();
    for (ASTCDClass astcdClass : ast.getCDDefinition().getCDClassList()) {
      ASTCDClass changedClass = CD4AnalysisMill.cDClassBuilder()
          .setName(astcdClass.getName())
          .setModifier(astcdClass.getModifier().deepClone())
          .build();
      astFullDecorator.decorate(astcdClass, changedClass);
      astcdClassList.add(changedClass);
    }
    return astcdClassList;
  }


  protected ASTCDInterface createLanguageInterface(final ASTCDCompilationUnit ast) {
    return astLanguageInterfaceDecorator.decorate(ast);
  }

  protected List<ASTCDClass> createASTBuilderClasses(final ASTCDCompilationUnit ast) {
    return ast.getCDDefinition().getCDClassList().stream()
        .map(astBuilderDecorator::decorate)
        .collect(Collectors.toList());
  }

  protected ASTCDClass createNodeFactoryClass(final ASTCDCompilationUnit ast) {
    return nodeFactoryDecorator.decorate(ast);
  }

  protected ASTCDClass createMillClass(final ASTCDCompilationUnit ast) {
    return millDecorator.decorate(ast);
  }

  protected List<ASTCDClass> createMillForSuperClasses(final ASTCDCompilationUnit ast) {
    return millForSuperDecorator.decorate(ast);
  }

  protected ASTCDClass createASTConstantsClass(final ASTCDCompilationUnit ast) {
    return astConstantsDecorator.decorate(ast);
  }

  protected List<ASTCDInterface> createASTInterfaces(final ASTCDCompilationUnit ast) {
    List<ASTCDInterface> astcdInterfaceList = new ArrayList<>();
    for (ASTCDInterface astcdInterface : ast.getCDDefinition().getCDInterfaceList()) {
      // do not create normal ast interface for language interface, is seperately created by ASTLanguageInterfaceDecorator
      if (!astcdInterface.getName().equals(AST_PREFIX + ast.getCDDefinition().getName() + NODE_SUFFIX)) {
        ASTCDInterface changedInterface = CD4AnalysisMill.cDInterfaceBuilder().setName(astcdInterface.getName())
            .setModifier(astcdInterface.getModifier().deepClone())
            .build();
        ASTCDInterface decoratedASTClass = astInterfaceDecorator.decorate(astcdInterface, changedInterface);
        astcdInterfaceList.add(decoratedASTClass);
      }
    }
    return astcdInterfaceList;
  }

  protected List<ASTCDEnum> createEnums(final ASTCDCompilationUnit ast) {
    return ast.getCDDefinition().getCDEnumList().stream()
        .map(enumDecorator::decorate)
        .collect(Collectors.toList());
  }
}

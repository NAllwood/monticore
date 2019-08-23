/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor.visitor_interface;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.SYMBOL_FULL_NAME;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.SYMBOL_TABLE_PACKGE;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.*;

public class SymbolVisitorDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {

  private final VisitorInterfaceDecorator visitorInterfaceDecorator;

  private final VisitorService visitorService;

  public SymbolVisitorDecorator(final GlobalExtensionManagement glex, final VisitorInterfaceDecorator visitorInterfaceDecorator, final VisitorService visitorService) {
    super(glex);
    this.visitorInterfaceDecorator = visitorInterfaceDecorator;
    this.visitorService = visitorService;
  }

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit input) {
    ASTCDCompilationUnit compilationUnit = input.deepClone();

    //set classname to correct Name with path
    String astPath = compilationUnit.getCDDefinition().getName().toLowerCase() + "." + SYMBOL_TABLE_PACKGE + ".";
    for (ASTCDClass astcdClass : compilationUnit.getCDDefinition().getCDClassList()) {
      astcdClass.setName(astPath + astcdClass.getName());
    }

    for (ASTCDInterface astcdInterface : compilationUnit.getCDDefinition().getCDInterfaceList()) {
      astcdInterface.setName(astPath + astcdInterface.getName());
    }

    for (ASTCDEnum astcdEnum : compilationUnit.getCDDefinition().getCDEnumList()) {
      astcdEnum.setName(astPath + astcdEnum.getName());
    }
    visitorInterfaceDecorator.disableTemplates();
    ASTCDInterface astcdInterface = visitorInterfaceDecorator.decorate(compilationUnit);

    astcdInterface.getCDMethodList().stream().filter(m -> TRAVERSE.equals(m.getName())).forEach(m ->
        this.replaceTemplate("_visitor.Traverse", m, new TemplateHookPoint(EMPTY_BODY, astcdInterface)));
    astcdInterface.getCDMethodList().stream().filter(m -> HANDLE.equals(m.getName())).forEach(m ->
        this.replaceTemplate(EMPTY_BODY, m, new TemplateHookPoint("_visitor.Handle", true)));

    astcdInterface.addCDMethod(addVisitASTNodeMethods());
    astcdInterface.addCDMethod(addEndVisitASTNodeMethods());
    return astcdInterface;
  }

  protected ASTCDMethod addVisitASTNodeMethods() {
    ASTMCType astNodeType = getCDTypeFacade().createTypeByDefinition(SYMBOL_FULL_NAME);
    return visitorService.getVisitorMethod(VISIT, astNodeType);
  }

  protected ASTCDMethod addEndVisitASTNodeMethods() {
    ASTMCType astNodeType = getCDTypeFacade().createTypeByDefinition(SYMBOL_FULL_NAME);
    return visitorService.getVisitorMethod(END_VISIT, astNodeType);
  }

}

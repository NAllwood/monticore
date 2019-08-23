/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_class;

import de.monticore.ast.ASTCNode;
import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.cd4analysis._ast.ASTCDParameter;
import de.monticore.codegen.cd2java.AbstractTransformer;
import de.monticore.codegen.cd2java._ast.factory.NodeFactoryService;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.CollectionTypesPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;


public class ASTDecorator extends AbstractTransformer<ASTCDClass> {

  private static final String VISITOR = "visitor";

  protected final ASTService astService;

  protected final VisitorService visitorService;

  protected final NodeFactoryService nodeFactoryService;

  protected final ASTSymbolDecorator symbolDecorator;

  protected final ASTScopeDecorator scopeDecorator;

  protected final MethodDecorator methodDecorator;

  protected final SymbolTableService symbolTableService;

  public ASTDecorator(final GlobalExtensionManagement glex,
                      final ASTService astService,
                      final VisitorService visitorService,
                      final NodeFactoryService nodeFactoryService,
                      final ASTSymbolDecorator symbolDecorator,
                      final ASTScopeDecorator scopeDecorator,
                      final MethodDecorator methodDecorator,
                      final SymbolTableService symbolTableService) {
    super(glex);
    this.astService = astService;
    this.visitorService = visitorService;
    this.nodeFactoryService = nodeFactoryService;
    this.symbolDecorator = symbolDecorator;
    this.scopeDecorator = scopeDecorator;
    this.methodDecorator = methodDecorator;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDClass decorate(final ASTCDClass originalClass, ASTCDClass changedClass) {
    changedClass.addInterface(this.astService.getASTBaseInterface());
    // have to use the changed one here because this one will get the TOP prefix
    // todo: find better way to determine if TOP class than by TOP prefix
    changedClass.addCDMethod(createAcceptMethod(changedClass));
    changedClass.addAllCDMethods(createAcceptSuperMethods(originalClass));
    changedClass.addCDMethod(getConstructMethod(originalClass));
    changedClass.addCDMethod(createGetChildrenMethod(originalClass));
    if (!originalClass.isPresentSuperclass()) {
      changedClass.setSuperclass(this.getCDTypeFacade().createQualifiedType(ASTCNode.class));
    }

    List<ASTCDAttribute> symbolAttributes = symbolDecorator.decorate(originalClass);
    addSymboltableMethods(symbolAttributes, changedClass);

    List<ASTCDAttribute> scopeAttributes = scopeDecorator.decorate(originalClass);
    addSymboltableMethods(scopeAttributes, changedClass);

    return changedClass;
  }

  protected void addSymboltableMethods(List<ASTCDAttribute> astcdAttributes, ASTCDClass clazz) {
    for (ASTCDAttribute attribute : astcdAttributes) {
      if (!astService.hasStereotype(attribute.getModifier(), MC2CDStereotypes.INHERITED)) {
        clazz.addCDAttribute(attribute);
        clazz.addAllCDMethods(methodDecorator.decorate(attribute));
      } else {
        String scopeInterfaceType = symbolTableService.getScopeInterfaceTypeName();

        methodDecorator.disableTemplates();
        List<ASTCDMethod> methods = methodDecorator.getMutatorDecorator().decorate(attribute);
        methods.forEach(m ->
            this.replaceTemplate(EMPTY_BODY, m, new TemplateHookPoint("_ast.ast_class.symboltable.InheritedSetEnclosingScope", m,
                CollectionTypesPrinter.printType(m.getCDParameter(0).getMCType()), scopeInterfaceType)));
        methodDecorator.enableTemplates();
        clazz.addAllCDMethods(methods);
      }
    }
  }


  protected ASTCDMethod createAcceptMethod(ASTCDClass astClass) {
    ASTCDParameter visitorParameter = this.getCDParameterFacade().createParameter(this.visitorService.getVisitorType(), VISITOR);
    ASTCDMethod acceptMethod = this.getCDMethodFacade().createMethod(PUBLIC, ASTConstants.ACCEPT_METHOD, visitorParameter);
    this.replaceTemplate(EMPTY_BODY, acceptMethod, new TemplateHookPoint("_ast.ast_class.Accept", astClass));
    return acceptMethod;
  }

  protected ASTCDMethod createGetChildrenMethod(ASTCDClass astClass) {
    ASTMCType astNodeType = getCDTypeFacade().createCollectionTypeOf(ASTConstants.AST_INTERFACE);
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(astNodeType).build();
    ASTCDMethod getChildrenMethod = this.getCDMethodFacade().createMethod(PUBLIC, returnType, ASTConstants.GET_CHILDREN_METHOD);
    this.replaceTemplate(EMPTY_BODY, getChildrenMethod, new TemplateHookPoint("_ast.ast_class.GetChildren", astClass));
    return getChildrenMethod;
  }

  protected List<ASTCDMethod> createAcceptSuperMethods(ASTCDClass astClass) {
    List<ASTCDMethod> result = new ArrayList<>();
    //accept methods for super visitors
    for (ASTMCType superVisitorType : this.visitorService.getAllVisitorTypesInHierarchy()) {
      ASTCDParameter superVisitorParameter = this.getCDParameterFacade().createParameter(superVisitorType, VISITOR);

      ASTCDMethod superAccept = this.getCDMethodFacade().createMethod(PUBLIC, ASTConstants.ACCEPT_METHOD, superVisitorParameter);
      String errorCode = DecorationHelper.getGeneratedErrorCode(astClass);
      this.replaceTemplate(EMPTY_BODY, superAccept, new TemplateHookPoint("_ast.ast_class.AcceptSuper",
          this.visitorService.getVisitorFullTypeName(), errorCode, astClass.getName(), CollectionTypesPrinter.printType(superVisitorType)));
      result.add(superAccept);
    }
    return result;
  }

  protected ASTCDMethod getConstructMethod(ASTCDClass astClass) {
    ASTCDMethod constructMethod;
    ASTMCType classType = this.getCDTypeFacade().createQualifiedType(astClass.getName());
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(classType).build();
    if (astClass.isPresentModifier() && astClass.getModifier().isAbstract()) {
      constructMethod = this.getCDMethodFacade().createMethod(PROTECTED_ABSTRACT, returnType, ASTConstants.CONSTRUCT_METHOD);
    } else {
      constructMethod = this.getCDMethodFacade().createMethod(PROTECTED, returnType, ASTConstants.CONSTRUCT_METHOD);
      this.replaceTemplate(EMPTY_BODY, constructMethod, new StringHookPoint(this.nodeFactoryService.getCreateInvocation(astClass)));
    }

    return constructMethod;
  }
}

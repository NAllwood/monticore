package de.monticore.codegen.cd2java._ast.ast_class;

import de.monticore.ast.ASTCNode;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java._ast.factory.NodeFactoryService;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISITOR_PREFIX;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;


public class ASTDecorator extends AbstractDecorator<ASTCDClass, ASTCDClass> {

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
  public ASTCDClass decorate(ASTCDClass clazz) {
    clazz.addInterface(this.astService.getASTBaseInterface());
    clazz.addCDMethod(createAcceptMethod(clazz));
    clazz.addAllCDMethods(createAcceptSuperMethods(clazz));
    clazz.addCDMethod(getConstructMethod(clazz));
    clazz.addCDMethod(createGetChildrenMethod(clazz));
    if (!clazz.isPresentSuperclass()) {
      clazz.setSuperclass(this.getCDTypeFacade().createSimpleReferenceType(ASTCNode.class));
    }

    List<ASTCDAttribute> symbolAttributes = symbolDecorator.decorate(clazz);
    addSymboltableMethods(symbolAttributes, clazz);

    List<ASTCDAttribute> scopeAttributes = scopeDecorator.decorate(clazz);
    addSymboltableMethods(scopeAttributes, clazz);

    return clazz;
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
                TypesPrinter.printType(m.getCDParameter(0).getType()), scopeInterfaceType)));
        methodDecorator.enableTemplates();
        clazz.addAllCDMethods(methods);
      }
    }
  }


  protected ASTCDMethod createAcceptMethod(ASTCDClass astClass) {
    ASTCDParameter visitorParameter = this.getCDParameterFacade().createParameter(this.visitorService.getVisitorType(), VISITOR_PREFIX);
    ASTCDMethod acceptMethod = this.getCDMethodFacade().createMethod(PUBLIC, ASTConstants.ACCEPT_METHOD, visitorParameter);
    this.replaceTemplate(EMPTY_BODY, acceptMethod, new TemplateHookPoint("_ast.ast_class.Accept", astClass));
    return acceptMethod;
  }

  protected ASTCDMethod createGetChildrenMethod(ASTCDClass astClass) {
    ASTType astNodeType = getCDTypeFacade().createCollectionTypeOf(ASTConstants.AST_INTERFACE);
    ASTCDMethod getChildrenMethod = this.getCDMethodFacade().createMethod(PUBLIC, astNodeType, ASTConstants.GET_CHILDREN_METHOD);
    this.replaceTemplate(EMPTY_BODY, getChildrenMethod, new TemplateHookPoint("_ast.ast_class.GetChildren", astClass));
    return getChildrenMethod;
  }

  protected List<ASTCDMethod> createAcceptSuperMethods(ASTCDClass astClass) {
    List<ASTCDMethod> result = new ArrayList<>();
    //accept methods for super visitors
    for (ASTType superVisitorType : this.visitorService.getAllVisitorTypesInHierarchy()) {
      ASTCDParameter superVisitorParameter = this.getCDParameterFacade().createParameter(superVisitorType, VISITOR_PREFIX);

      ASTCDMethod superAccept = this.getCDMethodFacade().createMethod(PUBLIC, ASTConstants.ACCEPT_METHOD, superVisitorParameter);
      String errorCode = DecorationHelper.getGeneratedErrorCode(astClass);
      this.replaceTemplate(EMPTY_BODY, superAccept, new TemplateHookPoint("_ast.ast_class.AcceptSuper",
          this.visitorService.getVisitorFullTypeName(), errorCode, astClass.getName(), TypesPrinter.printType(superVisitorType)));
      result.add(superAccept);
    }
    return result;
  }

  protected ASTCDMethod getConstructMethod(ASTCDClass astClass) {
    ASTCDMethod constructMethod;
    ASTType classType = this.getCDTypeFacade().createSimpleReferenceType(astClass.getName());
    if (astClass.isPresentModifier() && astClass.getModifier().isAbstract()) {
      constructMethod = this.getCDMethodFacade().createMethod(PROTECTED_ABSTRACT, classType, ASTConstants.CONSTRUCT_METHOD);
    } else {
      constructMethod = this.getCDMethodFacade().createMethod(PROTECTED, classType, ASTConstants.CONSTRUCT_METHOD);
      this.replaceTemplate(EMPTY_BODY, constructMethod, new StringHookPoint(this.nodeFactoryService.getCreateInvocation(astClass)));
    }

    return constructMethod;
  }
}

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_class.reference.referencedSymbol;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.cd4analysis._ast.ASTModifier;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.AbstractTransformer;
import de.monticore.codegen.cd2java._ast.ast_class.reference.referencedSymbol.referenedSymbolMethodDecorator.ReferencedSymbolAccessorDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;

public class ASTReferencedSymbolDecorator extends AbstractTransformer<ASTCDClass> {

  private static final String SYMBOL = "Symbol";

  public static final String IS_OPTIONAL = "isOptional";

  private final ReferencedSymbolAccessorDecorator accessorDecorator;

  private final SymbolTableService symbolTableService;

  public ASTReferencedSymbolDecorator(final GlobalExtensionManagement glex, final ReferencedSymbolAccessorDecorator accessorDecorator,
                                      final SymbolTableService symbolTableService) {
    super(glex);
    this.accessorDecorator = accessorDecorator;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDClass decorate(final ASTCDClass originalClass, ASTCDClass changedClass) {
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDAttribute astcdAttribute : originalClass.getCDAttributeList()) {
      if (symbolTableService.isReferencedSymbol(astcdAttribute)) {
        String referencedSymbolType = symbolTableService.getReferencedSymbolTypeName(astcdAttribute);
        //create referenced symbol attribute and methods
        ASTCDAttribute refSymbolAttribute = getRefSymbolAttribute(astcdAttribute, referencedSymbolType);
        attributeList.add(refSymbolAttribute);

        boolean wasAttributeOptional = wasAttributeOptional(astcdAttribute);
        methodList.addAll(getRefSymbolMethods(refSymbolAttribute, referencedSymbolType, wasAttributeOptional));
      }
    }
    changedClass.addAllCDMethods(methodList);
    changedClass.addAllCDAttributes(attributeList);
    return changedClass;
  }

  protected ASTCDAttribute getRefSymbolAttribute(ASTCDAttribute attribute, String referencedSymbol) {
    ASTModifier modifier = PROTECTED.build();
    //add referenced Symbol modifier that it can later be distinguished
    TransformationHelper.addStereotypeValue(modifier, MC2CDStereotypes.REFERENCED_SYMBOL_ATTRIBUTE.toString());

    if (GeneratorHelper.isListType(attribute.printType())) {
      //if the attribute is a list
      ASTMCType attributeType = getCDTypeFacade().createTypeByDefinition("Map< String, Optional<" + referencedSymbol + ">>");
      ASTCDAttribute symbolAttribute = this.getCDAttributeFacade().createAttribute(modifier, attributeType, attribute.getName() + SYMBOL);
      replaceTemplate(VALUE, symbolAttribute, new StringHookPoint("= new HashMap<>()"));
      return symbolAttribute;
    } else {
      //if the attribute is mandatory or optional
      ASTMCType attributeType = getCDTypeFacade().createOptionalTypeOf(referencedSymbol);
      ASTCDAttribute symbolAttribute = this.getCDAttributeFacade().createAttribute(modifier, attributeType, attribute.getName() + SYMBOL);
      replaceTemplate(VALUE, symbolAttribute, new StringHookPoint("= Optional.empty()"));
      return symbolAttribute;
    }
  }

  private List<ASTCDMethod> getRefSymbolMethods(ASTCDAttribute refSymbolAttribute, String referencedSymbol, boolean wasAttributeOptional) {
    ASTCDAttribute methodDecorationAttribute = refSymbolAttribute.deepClone();
    if (GeneratorHelper.isMapType(refSymbolAttribute.printType())) {
      //have to change type of attribute list instead of map
      //because the inner representation is a map but for users the List methods are only shown
      ASTMCType optionalType = getCDTypeFacade().createOptionalTypeOf(referencedSymbol);
      ASTMCType listType = getCDTypeFacade().createListTypeOf(optionalType);
      methodDecorationAttribute = getCDAttributeFacade().createAttribute(refSymbolAttribute.getModifier().deepClone(), listType, refSymbolAttribute.getName());
    } else if (wasAttributeOptional) {
      //add stereotype to attribute to later in the method generation know if the original attribute was optional or mandatory
      TransformationHelper.addStereotypeValue(methodDecorationAttribute.getModifier(), IS_OPTIONAL);
    }
    //to later easy symbol type
    TransformationHelper.addStereotypeValue(methodDecorationAttribute.getModifier(), MC2CDStereotypes.REFERENCED_SYMBOL.toString(), referencedSymbol);
    return accessorDecorator.decorate(methodDecorationAttribute);
  }

  private boolean wasAttributeOptional(ASTCDAttribute originalAttribute) {
    return DecorationHelper.isOptional(originalAttribute.getMCType());
  }

}

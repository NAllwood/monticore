package de.monticore.codegen.cd2java._ast.ast_class;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDType;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.SYMBOL_SUFFIX;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;

public class ASTSymbolDecorator extends AbstractDecorator<ASTCDType, List<ASTCDAttribute>> {


  private final SymbolTableService symbolTableService;

  public ASTSymbolDecorator(final GlobalExtensionManagement glex,
                            final SymbolTableService symbolTableService) {
    super(glex);
    this.symbolTableService = symbolTableService;
  }

  @Override
  public List<ASTCDAttribute> decorate(final ASTCDType clazz) {
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    if (clazz.getModifierOpt().isPresent() && symbolTableService.hasSymbolStereotype(clazz.getModifierOpt().get())) {
      ASTType symbolType = createSymbolType(clazz);

      String attributeName = StringUtils.uncapitalize(symbolTableService.getSimpleSymbolNameFromOptional(symbolType)) + SYMBOL_SUFFIX;

      attributeList.add(createSymbolAttribute(symbolType, attributeName));
      attributeList.add(createSymbol2Attribute(symbolType, attributeName));
    }
    return attributeList;
  }

  protected ASTType createSymbolType(ASTCDType clazz) {
    Optional<String> symbolTypeValue = symbolTableService.getSymbolTypeValue(clazz.getModifierOpt().get());
    if (symbolTypeValue.isPresent()) {
      // if symboltype was already defined in the grammar
      return getCDTypeFacade().createOptionalTypeOf(symbolTypeValue.get());
    } else {
      // use default type
      return this.getCDTypeFacade().createOptionalTypeOf(symbolTableService.getSymbolType(clazz));
    }
  }


  protected ASTCDAttribute createSymbolAttribute(ASTType symbolType, String attributeName) {
    //todo replace with symbol2 some day
    ASTCDAttribute attribute = this.getCDAttributeFacade().createAttribute(PROTECTED, symbolType, attributeName);
    this.replaceTemplate(VALUE, attribute, new StringHookPoint("= Optional.empty()"));
    return attribute;
  }

  protected ASTCDAttribute createSymbol2Attribute(ASTType symbolType, String attributeName) {
    //todo better name with the grammar name in the attributeName, like it was before
//    attributeName += "2";
    attributeName = "symbol2";
    ASTCDAttribute attribute = this.getCDAttributeFacade().createAttribute(PROTECTED, symbolType, attributeName);
    this.replaceTemplate(VALUE, attribute, new StringHookPoint("= Optional.empty()"));
    return attribute;
  }
}
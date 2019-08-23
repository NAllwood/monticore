/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.factories;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTModifier;
import de.monticore.cd.cd4analysis._ast.CD4AnalysisMill;
import de.monticore.cd.cd4code._parser.CD4CodeParser;
import de.monticore.codegen.cd2java.factories.exception.CDFactoryErrorCode;
import de.monticore.codegen.cd2java.factories.exception.CDFactoryException;
import de.monticore.types.MCCollectionTypesHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

public class CDAttributeFacade {

  private static CDAttributeFacade cdAttributeFacade;

  private final CD4CodeParser parser;

  private CDAttributeFacade() {
    this.parser = new CD4CodeParser();
  }

  public static CDAttributeFacade getInstance() {
    if (cdAttributeFacade == null) {
      cdAttributeFacade = new CDAttributeFacade();
    }
    return cdAttributeFacade;
  }

  public ASTCDAttribute createAttributeByDefinition(final String signature) {
    Optional<ASTCDAttribute> attribute;
    try {
      attribute = parser.parseCDAttribute(new StringReader(signature));
    } catch (IOException e) {
      throw new CDFactoryException(CDFactoryErrorCode.COULD_NOT_CREATE_ATTRIBUTE, signature, e);
    }
    if (!attribute.isPresent()) {
      throw new CDFactoryException(CDFactoryErrorCode.COULD_NOT_CREATE_ATTRIBUTE, signature);
    }
    return attribute.get();
  }

  public ASTCDAttribute createAttribute(final ASTModifier modifier, final ASTMCType type, final String name) {
    return CD4AnalysisMill.cDAttributeBuilder()
        .setModifier(modifier)
        .setMCType(type.deepClone())
        .setName(name)
        .build();
  }

  public ASTCDAttribute createAttribute(final ASTModifier modifier, final ASTMCType type) {
    return createAttribute(modifier, type, StringUtils.uncapitalize(MCCollectionTypesHelper.printType(type)));
  }

  public ASTCDAttribute createAttribute(final ASTModifier modifier, final String type, final String name) {
    return createAttribute(modifier, CDTypeFacade.getInstance().createQualifiedType(type), name);
  }

  public ASTCDAttribute createAttribute(final ASTModifier modifier, final String type) {
    return createAttribute(modifier, CDTypeFacade.getInstance().createQualifiedType(type), StringUtils.uncapitalize(type));
  }

  public ASTCDAttribute createAttribute(final ASTModifier modifier, final Class<?> type, final String name) {
    return createAttribute(modifier, CDTypeFacade.getInstance().createQualifiedType(type), name);
  }

  public ASTCDAttribute createAttribute(final ASTModifier modifier, final Class<?> type) {
    return createAttribute(modifier, CDTypeFacade.getInstance().createQualifiedType(type), StringUtils.uncapitalize(type.getSimpleName()));
  }


  public ASTCDAttribute createAttribute(final CDModifier modifier, final ASTMCType type, final String name) {
    return createAttribute(modifier.build(), type, name);
  }

  public ASTCDAttribute createAttribute(final CDModifier modifier, final ASTMCType type) {
    return createAttribute(modifier.build(), type);
  }

  public ASTCDAttribute createAttribute(final CDModifier modifier, final String type, final String name) {
    return createAttribute(modifier.build(), type, name);
  }

  public ASTCDAttribute createAttribute(final CDModifier modifier, final String type) {
    return createAttribute(modifier.build(), type);
  }

  public ASTCDAttribute createAttribute(final CDModifier modifier, final Class<?> type, final String name) {
    return createAttribute(modifier.build(), type, name);
  }

  public ASTCDAttribute createAttribute(final CDModifier modifier, final Class<?> type) {
    return createAttribute(modifier.build(), type);
  }
}

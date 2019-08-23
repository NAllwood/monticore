/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.factories;

import de.monticore.ast.ASTNode;
import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbolReference;
import de.monticore.cd.cd4analysis._symboltable.CDTypes;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.types.MCCollectionTypesHelper;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.se_rwth.commons.JavaNamesHelper;
import de.se_rwth.commons.StringTransformations;

import java.util.List;

import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PREFIX;

public class DecorationHelper extends MCCollectionTypesHelper {

  public static final String OPTIONAL = "Optional";

  public static final String GET_PREFIX_BOOLEAN = "is";

  public static final String GET_SUFFIX_OPTINAL = "Opt";

  public static final String GET_SUFFIX_LIST = "List";

  public static final String GET_PREFIX = "get";

  public static final String SET_PREFIX = "set";


  public static String getGeneratedErrorCode(ASTNode node) {
    // Use the string representation
    int hashCode = Math.abs(node.toString().hashCode());
    String errorCodeSuffix = String.valueOf(hashCode);
    return "x" + (hashCode < 1000 ? errorCodeSuffix : errorCodeSuffix
        .substring(errorCodeSuffix.length() - 3));
  }

  public boolean isAstNode(ASTCDAttribute attr) {
    if (attr.getModifier().isPresentStereotype()) {
      return attr.getModifier().getStereotype().getValueList().stream().anyMatch(v -> v.getName().equals(MC2CDStereotypes.AST_TYPE.toString()));
    }
    return false;
  }

  public boolean isSimpleAstNode(ASTCDAttribute attr) {
    return !DecorationHelper.isOptional(attr.getMCType()) && !DecorationHelper.isListType(attr.printType()) && isAstNode(attr);
  }

  public boolean isOptionalAstNode(ASTCDAttribute attr) {
    return DecorationHelper.isOptional(attr.getMCType()) && isAstNode(attr);
  }

  public boolean isListAstNode(ASTCDAttribute attribute) {
    return DecorationHelper.isListType(attribute.printType()) && isAstNode(attribute);
  }

  public boolean hasOnlyAstAttributes(ASTCDClass type) {
    for (ASTCDAttribute attr : type.getCDAttributeList()) {
      if (!isAstNode(attr)) {
        return false;
      }
    }
    return true;
  }

  public static String getAstClassNameForASTLists(ASTCDAttribute attr) {
    if (attr.getMCType() instanceof ASTMCBasicGenericType && ((ASTMCBasicGenericType) attr.getMCType()).sizeMCTypeArguments() == 1) {
      return MCCollectionTypesHelper.printType(((ASTMCBasicGenericType) attr.getMCType()).getMCTypeArgumentList().get(0));
    }
    return "";
  }

  public static String getNativeAttributeName(String attributeName) {
    if (!attributeName.startsWith(JavaNamesHelper.PREFIX_WHEN_WORD_IS_RESERVED)) {
      return attributeName;
    }
    return attributeName.substring(JavaNamesHelper.PREFIX_WHEN_WORD_IS_RESERVED.length());
  }

  public static boolean isListType(String type) {
    int index = type.indexOf('<');
    if (index != -1) {
      type = type.substring(0, index);
    }
    return "List".equals(type) || "java.util.List".equals(type)
        || "ArrayList".equals(type) || "java.util.ArrayList".equals(type);
  }


  public static boolean isString(String type) {
    return "String".equals(type) || "java.lang.String".equals(type);
  }


  public static String getPlainGetter(ASTCDAttribute ast) {
    String astType = printType(ast.getMCType());
    StringBuilder sb = new StringBuilder();
    if (CDTypes.isBoolean(astType)) {
      sb.append(GET_PREFIX_BOOLEAN);
    } else {
      sb.append(GET_PREFIX);
    }
    sb.append(StringTransformations.capitalize(getNativeAttributeName(ast.getName())));
    if (isOptional(ast.getMCType())) {
      sb.append(GET_SUFFIX_OPTINAL);
    } else if (isListType(astType)) {
      if (ast.getName().endsWith(TransformationHelper.LIST_SUFFIX)) {
        sb.replace(sb.length() - TransformationHelper.LIST_SUFFIX.length(),
            sb.length(), GET_SUFFIX_LIST);
      } else {
        sb.append(GET_SUFFIX_LIST);
      }
    }
    return sb.toString();
  }

  public boolean isAttributeOfTypeEnum(ASTCDAttribute attr) {
    if (!attr.isPresentSymbol()) {
      return false;
    }
    CDTypeSymbolReference attrType =  attr.getSymbol().getType();

    List<CDTypeSymbolReference> typeArgs = attrType.getActualTypeArguments();
    if (typeArgs.size() > 1) {
      return false;
    }

    String typeName = typeArgs.isEmpty()
            ? attrType.getName()
            : typeArgs.get(0).getName();
    if (!typeName.contains(".") && !typeName.startsWith(AST_PREFIX)) {
      return false;
    }

    List<String> listName = MCCollectionTypesHelper.createListFromDotSeparatedString(typeName);
    if (!listName.get(listName.size() - 1).startsWith(AST_PREFIX)) {
      return false;
    }

    if (typeArgs.isEmpty()) {
      return attrType.existsReferencedSymbol() && attrType.isEnum();
    }

    CDTypeSymbolReference typeArgument = (CDTypeSymbolReference) typeArgs
            .get(0);
    return typeArgument.existsReferencedSymbol() && typeArgument.isEnum();
  }


  public static String getPlainSetter(ASTCDAttribute ast) {
    StringBuilder sb = new StringBuilder(SET_PREFIX).append(
        StringTransformations.capitalize(getNativeAttributeName(ast.getName())));
    String astType = printType(ast.getMCType());
    if (isListType(astType))
      if (ast.getName().endsWith(TransformationHelper.LIST_SUFFIX)) {
        sb.replace(sb.length() - TransformationHelper.LIST_SUFFIX.length(),
            sb.length(), GET_SUFFIX_LIST);
      } else {
        sb.append(GET_SUFFIX_LIST);
      }
    return sb.toString();
  }


  public String getDefaultValue(ASTCDAttribute attribute) {
    if (isAstNode(attribute)) {
      return "null";
    }
    if (isOptional(attribute.getMCType())) {
      return "Optional.empty()";
    }
    String typeName = attribute.printType();
    switch (typeName) {
      case "boolean":
        return "false";
      case "int":
        return "0";
      case "short":
        return "(short) 0";
      case "long":
        return "0";
      case "float":
        return "0.0f";
      case "double":
        return "0.0";
      case "char":
        return "'\u0000'";
      default:
        return "null";
    }
  }

}

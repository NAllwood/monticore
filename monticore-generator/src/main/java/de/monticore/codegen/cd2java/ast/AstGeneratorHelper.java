/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java.ast;

import com.google.common.base.Joiner;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd.cd4analysis._visitor.CD4AnalysisVisitor;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.generating.GeneratorSetup;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.monticore.types.CollectionTypesPrinter;
import de.monticore.types.MCCollectionTypesHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

public class AstGeneratorHelper extends GeneratorHelper {

  private final MCGrammarSymbol grammarSymbol;
  
  public AstGeneratorHelper(ASTCDCompilationUnit topAst, CD4AnalysisGlobalScope symbolTable, Grammar_WithConceptsGlobalScope grammarScope) {
    super(topAst, symbolTable);
    String qualifiedGrammarName = topAst.getPackageList().isEmpty()
        ? this.cdDefinition.getName()
        : Joiner.on('.').join(Names.getQualifiedName(topAst.getPackageList()),
        this.cdDefinition.getName());

    grammarSymbol = grammarScope.resolveMCGrammar(
        qualifiedGrammarName).orElse(null);
  }

  public MCGrammarSymbol getGrammarSymbol() {
    return this.grammarSymbol;
  }

  public String getAstAttributeValue(ASTCDAttribute attribute, ASTCDType clazz) {
    return getAstAttributeValue(attribute);
  }
  
  public String getAstAttributeValue(ASTCDAttribute attribute) {
    if (attribute.isPresentValue()) {
      return attribute.printValue();
    }
    if (isOptional(attribute)) {
      return "Optional.empty()";
    }
    String typeName = CollectionTypesPrinter.printType(attribute.getMCType());
    if (isListType(typeName)) {
      return "new java.util.ArrayList<>()";
    }
    if (isMapType(typeName)) {
      return "new java.util.HashMap<>()";
    }
    return "";
  }
  
  public String getAstAttributeValueForBuilder(ASTCDAttribute attribute) {
    if (isOptional(attribute)) {
      return "Optional.empty()";
    }
    else if (isBoolean(attribute)) {
      return "false";
    }
    return getAstAttributeValue(attribute);
  }
  
  public static boolean isBuilderClass(ASTCDDefinition cdDefinition, ASTCDClass clazz) {
    if (!clazz.getName().endsWith(BUILDER)
         && !clazz.getName().endsWith(BUILDER + GeneratorSetup.GENERATED_CLASS_SUFFIX)) {
      return false;
    }
    String className = clazz.getName().substring(0, clazz.getName().indexOf(BUILDER));
    return cdDefinition.getCDClassList().stream()
        .filter(c -> className.equals(GeneratorHelper.getPlainName(c))).findAny()
        .isPresent();
  }

  public static boolean compareAstTypes(String qualifiedType, String type) {
    if (type.indexOf('.') != -1) {
      return qualifiedType.equals(type);
    }
    String simpleName = Names.getSimpleName(qualifiedType);
    if (simpleName.startsWith(AST_PREFIX)) {
      return simpleName.equals(type);
    }
    return false;
  }

  /**
   * @param qualifiedName
   * @return The lower case qualifiedName + AST_PACKAGE_SUFFIX
   */
  public static String getAstPackage(String qualifiedName) {
    Log.errorIfNull(qualifiedName);
    return Joiners.DOT.join(qualifiedName.toLowerCase(), AST_PACKAGE_SUFFIX_DOT);
  }
  
  /**
   * @param qualifiedCdName
   * @return The lower case qualifiedName + AST_PACKAGE_SUFFIX
   */
  public static String getAstPackageForCD(String qualifiedCdName) {
    Log.errorIfNull(qualifiedCdName);
    return Joiners.DOT.join(qualifiedCdName.toLowerCase(),
        Names.getSimpleName(qualifiedCdName).toLowerCase(), getAstPackageSuffix());
  }
  
  public static String getAstPackageSuffix() {
    return GeneratorHelper.AST_PACKAGE_SUFFIX;
  }
  
  public static String getSymbolTablePackageSuffix() {
    return GeneratorHelper.SYMBOLTABLE_PACKAGE_SUFFIX;
  }
  
  public static String getNameOfBuilderClass(ASTCDClass astClass) {
    String name = Names.getSimpleName(astClass.getName());
    if(astClass.getName().endsWith(GeneratorSetup.GENERATED_CLASS_SUFFIX)) {
      name = name.substring(0, name.indexOf(GeneratorSetup.GENERATED_CLASS_SUFFIX));
    }
    return name + BUILDER;
  }
  
  public static String getSuperClassForBuilder(ASTCDClass clazz) {
    if (!clazz.isPresentSuperclass()) {
      return "";
    }
    String superClassName = Names.getSimpleName(clazz.printSuperClass());
    return superClassName.endsWith(GeneratorSetup.GENERATED_CLASS_SUFFIX)
        ? superClassName.substring(0, superClassName.indexOf(GeneratorSetup.GENERATED_CLASS_SUFFIX))
        : superClassName;

  }
  
  public static boolean generateSetter(ASTCDClass clazz, ASTCDAttribute cdAttribute, String typeName) {
    if (GeneratorHelper.isInherited(cdAttribute)) {
      return false;
    }
    String methodName = GeneratorHelper.getPlainSetter(cdAttribute);
    if (clazz.getCDMethodList().stream()
        .filter(m -> methodName.equals(m.getName()) && m.getCDParameterList().size() == 1
            && compareAstTypes(typeName,
                MCCollectionTypesHelper.printSimpleRefType(m.getCDParameterList().get(0).getMCType())))
        .findAny()
        .isPresent()) {
      return false;
    }
    return true;
  }
  
  public static String getConstantClassName(MCGrammarSymbol grammarSymbol) {
    return grammarSymbol.getFullName().toLowerCase() +
        GeneratorHelper.AST_DOT_PACKAGE_SUFFIX + "."
        + getConstantClassSimpleName(grammarSymbol);
    
  }
  
  public static String getConstantClassSimpleName(MCGrammarSymbol grammarSymbol) {
    return "ASTConstants" + grammarSymbol.getName();
  }
  
  public static String getASTClassNameWithoutPrefix(ASTCDType type) {
    if (!GeneratorHelper.getPlainName(type).startsWith(GeneratorHelper.AST_PREFIX)) {
      return type.getName();
    }
    return GeneratorHelper.getPlainName(type).substring(GeneratorHelper.AST_PREFIX.length());
  }
  
  public static String getASTClassNameWithoutPrefix(String type) {
    return type.startsWith(GeneratorHelper.AST_PREFIX)
        ? type.substring(GeneratorHelper.AST_PREFIX.length())
        : type;
  }
  
  public static boolean isOriginalClassAbstract(ASTCDClass astType) {
    return hasStereotype(astType, "Abstract");
  }

  public static boolean isAbstract(ASTCDClass clazz) {
    return clazz.isPresentModifier() && clazz.getModifier().isAbstract();
  }
  
  public static boolean hasReturnTypeVoid(ASTCDMethod method) {
    return method.getMCReturnType().isPresentMCVoidType();
  }

  /**
   * Transforms all CD types to Java types using the given package suffix.
   */
  public void transformCdTypes2Java() {
    new Cd2JavaTypeConverter() {
      @Override
      public void visit(ASTMCQualifiedType node) {
        AstGeneratorHelper.this.transformTypeCd2Java(node, GeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT);
      }
    }.handle(topAst);
    
  }
  
  /**
   * Clones the top ast and transforms CD types defined in this- or in one of the super CDs to simple CD types
   * @return cloned transformed ast
   */
  public ASTCDCompilationUnit getASTCDForReporting() {
    ASTCDCompilationUnit ast = topAst.deepClone();
    
    new Cd2JavaTypeConverter() {
      @Override
      public void visit(ASTMCQualifiedType node) {
       AstGeneratorHelper.this.transformQualifiedToSimpleIfPossible(node, GeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT);
      }
    }.handle(ast);
    
    return ast;
  }
  
  public String printFullType(ASTMCType ast) {
    return CollectionTypesPrinter.printType(ast);
  }
  
  public class Cd2JavaTypeConverter implements CD4AnalysisVisitor {}

}

/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import de.monticore.ast.ASTNode;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.prettyprint.CDPrettyPrinter;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.monticore.io.paths.IterablePath;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCArrayType;
import de.monticore.types.mcfullgenerictypes._ast.MCFullGenericTypesMill;
import de.monticore.utils.ASTNodes;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

public final class TransformationHelper {

  public static final String DEFAULT_FILE_EXTENSION = ".java";

  public static final String AST_PREFIX = "AST";

  public static final String LIST_SUFFIX = "s";

  private TransformationHelper() {
    // noninstantiable
  }

  public static String getClassProdName(ASTClassProd classProd) {
    return classProd.getName();
  }

  public static String typeToString(ASTMCType type) {
    if (type instanceof ASTMCGenericType) {
      return ((ASTMCGenericType) type).printWithoutTypeArguments();
    } else if (type instanceof ASTMCArrayType) {
      return ((ASTMCArrayType) type).printTypeWithoutBrackets();
    }
    return type.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter());
  }

  public static String simpleName(ASTMCType type) {
    String name;
    if (type instanceof ASTMCGenericType) {
      name = ((ASTMCGenericType) type).printWithoutTypeArguments();
    } else if (type instanceof ASTMCArrayType) {
      name = ((ASTMCArrayType) type).printTypeWithoutBrackets();
    } else {
      name = type.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter());
    }
    return Names.getSimpleName(name);
  }

  /**
   * Pretty prints a CD AST to a String object.
   *
   * @param astNode the top node of the CD AST to be pretty printed
   */
  // TODO: should be placed somewhere in the UML/P CD project
  public static String prettyPrint(ASTCD4AnalysisNode astNode) {
    // set up objects
    CDPrettyPrinter prettyPrinter = new CDPrettyPrinter(
        new IndentPrinter());

    // run, check result and return
    String prettyPrintedAst = prettyPrinter.prettyprint(astNode);
    checkArgument(!isNullOrEmpty(prettyPrintedAst));
    return prettyPrintedAst;
  }

  public static Optional<String> getUsageName(ASTNode root,
                                              ASTNode successor) {
    List<ASTNode> intermediates = ASTNodes
        .getIntermediates(root, successor);
    for (ASTNode ancestor : Lists.reverse(intermediates)) {
      if (ancestor instanceof ASTConstantGroup) {
        return ((ASTConstantGroup) ancestor)
            .getUsageNameOpt();
      }
      if (ancestor instanceof ASTNonTerminal) {
        return ((ASTNonTerminal) ancestor)
            .getUsageNameOpt();
      }
      if (ancestor instanceof ASTNonTerminalSeparator) {
        return ((ASTNonTerminalSeparator) ancestor)
            .getUsageNameOpt();
      }
      if (ancestor instanceof ASTTerminal) {
        return ((ASTTerminal) ancestor).getUsageNameOpt();
      }
      if (ancestor instanceof ASTKeyTerminal) {
        return ((ASTKeyTerminal) ancestor).getUsageNameOpt();
      }
      if (ancestor instanceof ASTAdditionalAttribute) {
        return ((ASTAdditionalAttribute) ancestor).getNameOpt();
      }
    }
    return Optional.empty();
  }

  public static Optional<String> getName(ASTNode node) {
    if (node instanceof ASTNonTerminal) {
      return Optional.of(((ASTNonTerminal) node)
          .getName());
    }
    if (node instanceof ASTConstant) {
      return Optional.of(((ASTConstant) node)
          .getName());
    }
    if (node instanceof ASTNonTerminalSeparator) {
      return Optional.of(((ASTNonTerminalSeparator) node)
          .getName());
    }
    if (node instanceof ASTTerminal) {
      return Optional.of(((ASTTerminal) node).getName());
    }
    if (node instanceof ASTAdditionalAttribute) {
      return ((ASTAdditionalAttribute) node).getNameOpt();
    }
    return Optional.empty();
  }

  public static ASTCDParameter createParameter(String typeName,
                                               String parameterName) {
    ASTCDParameter parameter = CD4AnalysisNodeFactory
        .createASTCDParameter();
    parameter.setMCType(TransformationHelper.createType(typeName));
    parameter.setName(parameterName);
    return parameter;
  }

  public static ASTModifier createPublicModifier() {
    ASTModifier modifier = CD4AnalysisNodeFactory.createASTModifier();
    modifier.setPublic(true);
    return modifier;
  }

  public static ASTMCGenericType createType(
      String typeName, String generics) {
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTMCGenericType> optType = null;
    try {
      optType = parser.parse_StringMCGenericType(typeName + "<" + generics + ">");
    } catch (IOException e) {
      Log.error("0xA4036 Cannot create ASTType " + typeName + " during transformation from MC4 to CD4Analysis");
    }
    return optType.get();
  }

  public static ASTMCType createType(String typeName) {
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTMCType> optType = null;
    try {
      optType = parser.parse_StringMCType(typeName);
    } catch (IOException e) {
      Log.error("0xA4036 Cannot create ASTType " + typeName + " during transformation from MC4 to CD4Analysis");
    }
    return optType.get();
  }

  public static ASTMCReturnType createReturnType(String typeName) {
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTMCReturnType> optType = null;
    try {
      optType = parser.parse_StringMCReturnType(typeName);
    } catch (IOException e) {
      Log.error("0xA4036 Cannot create ASTType " + typeName + " during transformation from MC4 to CD4Analysis");
    }
    return optType.get();
  }

  public static ASTMCObjectType createObjectType(String typeName) {
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTMCObjectType> optType = null;
    try {
      optType = parser.parse_StringMCObjectType(typeName);
    } catch (IOException e) {
      Log.error("0xA4036 Cannot create ASTType " + typeName + " during transformation from MC4 to CD4Analysis");
    }
    return optType.get();
  }

  public static String getPackageName(ProdSymbol symbol) {
    // return grammar.getName().toLowerCase() + AST_DOT_PACKAGE_SUFFIX_DOT;
    return getGrammarName(symbol) + ".";
  }

  public static String getPackageName(
      ASTCDCompilationUnit cdCompilationUnit) {
    String packageName = Names
        .getQualifiedName(cdCompilationUnit.getPackageList());
    if (!packageName.isEmpty()) {
      packageName = packageName + ".";
    }
    return packageName + cdCompilationUnit.getCDDefinition().getName() + ".";
  }

  public static Set<String> getAllGrammarConstants(ASTMCGrammar grammar) {
    Set<String> constants = new HashSet<>();
    MCGrammarSymbol grammarSymbol = grammar.getSymbol();
    Preconditions.checkState(grammarSymbol != null);
    for (RuleComponentSymbol component : grammarSymbol.getProds().stream()
        .flatMap(p -> p.getProdComponents().stream()).collect(Collectors.toSet())) {
      if (component.isIsConstantGroup()) {
        for (String subComponent : component.getSubProdList()) {
          constants.add(subComponent);
        }
      }
    }
    for (ProdSymbol type : grammarSymbol.getProds()) {
      if (type.isIsEnum() && type.isPresentAstNode()
          && type.getAstNode() instanceof ASTEnumProd) {
        for (ASTConstant enumValue : ((ASTEnumProd) type.getAstNode()).getConstantList()) {
          String humanName = enumValue.isPresentHumanName()
              ? enumValue.getHumanName()
              : enumValue.getName();
          constants.add(humanName);
        }
      }
    }
    return constants;
  }


  /**
   * Checks if a handwritten class with the given qualifiedName (dot-separated)
   * exists on the target path
   *
   * @param qualifiedName name of the class to search for
   * @return true if a handwritten class with the qualifiedName exists
   */
  public static boolean existsHandwrittenClass(IterablePath targetPath,
                                               String qualifiedName) {
    Path handwrittenFile = Paths.get(Names
        .getPathFromPackage(qualifiedName)
        + DEFAULT_FILE_EXTENSION);
    Log.debug("Checking existence of handwritten class " + qualifiedName
        + " by searching for "
        + handwrittenFile.toString(), TransformationHelper.class.getName());
    Optional<Path> handwrittenFilePath = targetPath.getResolvedPath(handwrittenFile);
    boolean result = handwrittenFilePath.isPresent();
    if (result) {
      Reporting.reportUseHandwrittenCodeFile(handwrittenFilePath.get(),
          handwrittenFile);
    }
    Reporting.reportHWCExistenceCheck(targetPath,
        handwrittenFile, handwrittenFilePath);
    return result;
  }

  /**
   * Get the corresponding CD for MC grammar if exists
   *
   * @param ast
   * @return
   */
  public static Optional<ASTCDCompilationUnit> getCDforGrammar(CD4AnalysisGlobalScope globalScope,
                                                               ASTMCGrammar ast) {
    final String qualifiedCDName = Names.getQualifiedName(ast.getPackageList(), ast.getName());

    Optional<CDDefinitionSymbol> cdSymbol = globalScope.resolveCDDefinitionDown(
        qualifiedCDName);

    if (cdSymbol.isPresent() && cdSymbol.get().getEnclosingScope().isPresentAstNode()) {
      Log.debug("Got existed symbol table for " + cdSymbol.get().getFullName(),
          TransformationHelper.class.getName());
      return Optional
          .of((ASTCDCompilationUnit) cdSymbol.get().getEnclosingScope().getAstNode());
    }

    return Optional.empty();
  }

  public static String getQualifiedTypeNameAndMarkIfExternal(ASTMCType ruleReference,
                                                             ASTMCGrammar grammar, ASTCDClass cdClass) {

    Optional<ProdSymbol> typeSymbol = resolveAstRuleType(grammar, ruleReference);

    String qualifiedRuleName = getQualifiedAstName(
        typeSymbol, ruleReference);

    if (!typeSymbol.isPresent()) {
      addStereoType(cdClass,
          MC2CDStereotypes.EXTERNAL_TYPE.toString(), qualifiedRuleName);
    }

    return qualifiedRuleName;
  }

  // TODO GV: remove this if CDInterface and CDClass have a common type CDType
  public static String getQualifiedTypeNameAndMarkIfExternal(ASTMCType ruleReference,
                                                             ASTMCGrammar grammar, ASTCDInterface interf) {

    Optional<ProdSymbol> typeSymbol = resolveAstRuleType(grammar, ruleReference);

    String qualifiedRuleName = getQualifiedAstName(
        typeSymbol, ruleReference);

    if (!typeSymbol.isPresent()) {
      addStereoType(interf,
          MC2CDStereotypes.EXTERNAL_TYPE.toString(), qualifiedRuleName);
    }

    return qualifiedRuleName;
  }

  public static Optional<ProdSymbol> resolveAstRuleType(ASTMCGrammar node, ASTMCType type) {
    String simpleName = Names.getSimpleName(typeToString(type));
    if (!simpleName.startsWith(AST_PREFIX)) {
      return Optional.empty();
    }
    Optional<ProdSymbol> ruleSymbol = MCGrammarSymbolTableHelper.resolveRule(node,
            simpleName
                    .substring(AST_PREFIX.length()));
    if (ruleSymbol.isPresent() && istPartOfGrammar(ruleSymbol.get())) {
      return ruleSymbol;
    }
    return Optional.empty();
  }

  // TODO GV, PN: change it
  public static boolean istPartOfGrammar(ProdSymbol rule) {
    return rule.getEnclosingScope().getSpanningSymbolOpt().isPresent()
        && rule.getEnclosingScope().getSpanningSymbol() instanceof MCGrammarSymbol;
  }

  public static String getGrammarName(ProdSymbol rule) {
    return Names.getQualifier(rule.getFullName());
  }

  public static String getGrammarNameAsPackage(ProdSymbol rule) {
    return getGrammarName(rule) + ".";
  }

  public static String getQualifiedAstName(Optional<ProdSymbol> typeSymbol, ASTMCType type) {
    if (typeSymbol.isPresent()) {
      return Names.getQualifier(typeSymbol.get().getFullName()) + "." + AST_PREFIX + typeSymbol.get().getName();
    } else {
      return type.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter());
    }
  }

  public static void addStereoType(ASTCDType type, String stereotypeName,
                                   String stereotypeValue) {
    if (!type.getModifierOpt().isPresent()) {
      type.setModifier(CD4AnalysisNodeFactory.createASTModifier());
    }
    addStereotypeValue(type.getModifierOpt().get(),
        stereotypeName, stereotypeValue);
  }

  public static void addStereoType(ASTCDType type, String stereotypeName) {
    if (!type.getModifierOpt().isPresent()) {
      type.setModifier(CD4AnalysisNodeFactory.createASTModifier());
    }
    addStereotypeValue(type.getModifierOpt().get(),
        stereotypeName);
  }

  public static void addStereoType(ASTCDDefinition type, String stereotypeName) {
    if (!type.getModifierOpt().isPresent()) {
      type.setModifier(CD4AnalysisNodeFactory.createASTModifier());
    }
    addStereotypeValue(type.getModifierOpt().get(),
        stereotypeName);
  }

  public static void addStereoType(ASTCDAttribute attribute,
                                   String stereotypeName,
                                   String stereotypeValue) {
    if (!attribute.isPresentModifier()) {
      attribute.setModifier(CD4AnalysisNodeFactory.createASTModifier());
    }
    addStereotypeValue(attribute.getModifier(),
        stereotypeName, stereotypeValue);
  }

  public static void addStereoType(ASTCDDefinition type, String stereotypeName,
                                   String stereotypeValue) {
    if (!type.getModifierOpt().isPresent()) {
      type.setModifier(CD4AnalysisNodeFactory.createASTModifier());
    }
    addStereotypeValue(type.getModifierOpt().get(),
        stereotypeName, stereotypeValue);
  }

  public static void addStereotypeValue(ASTModifier astModifier,
                                        String stereotypeName,
                                        String stereotypeValue) {
    if (!astModifier.isPresentStereotype()) {
      astModifier.setStereotype(CD4AnalysisNodeFactory
          .createASTCDStereotype());
    }
    List<ASTCDStereoValue> stereoValueList = astModifier.getStereotype()
        .getValueList();
    ASTCDStereoValue stereoValue = CD4AnalysisNodeFactory
        .createASTCDStereoValue();
    stereoValue.setName(stereotypeName);
    stereoValue.setValue(stereotypeValue);
    stereoValueList.add(stereoValue);
  }

  public static void addStereotypeValue(ASTModifier astModifier,
                                        String stereotypeName) {
    if (!astModifier.isPresentStereotype()) {
      astModifier.setStereotype(CD4AnalysisNodeFactory
          .createASTCDStereotype());
    }
    List<ASTCDStereoValue> stereoValueList = astModifier.getStereotype()
        .getValueList();
    ASTCDStereoValue stereoValue = CD4AnalysisNodeFactory
        .createASTCDStereoValue();
    stereoValue.setName(stereotypeName);
    stereoValueList.add(stereoValue);
  }
}

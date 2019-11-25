/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.parser;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import de.monticore.ast.ASTNode;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.grammar.HelperGrammar;
import de.monticore.grammar.MCGrammarInfo;
import de.monticore.grammar.PredicatePair;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbolLoader;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar_withconcepts._ast.ASTAction;
import de.monticore.grammar.grammar_withconcepts._ast.ASTExpressionPredicate;
import de.monticore.grammar.grammar_withconcepts._ast.ASTGrammar_WithConceptsNode;
import de.monticore.grammar.grammar_withconcepts._ast.ASTJavaCode;
import de.monticore.grammar.prettyprint.Grammar_WithConceptsPrettyPrinter;
import de.monticore.javalight._ast.ASTClassMemberDeclaration;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mccommonstatements._ast.ASTBlockStatement;
import de.se_rwth.commons.JavaNamesHelper;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;

/**
 * This is a helper class for the parser generation
 */
public class ParserGeneratorHelper {

  public static final String MONTICOREANYTHING = "MONTICOREANYTHING";

  public static final String RIGHTASSOC = "<assoc=right>";

  public static final String ANTLR_CONCEPT = "antlr";

  private static Grammar_WithConceptsPrettyPrinter prettyPrinter;

  private ASTMCGrammar astGrammar;

  private String qualifiedGrammarName;

  private MCGrammarSymbol grammarSymbol;

  private MCGrammarInfo grammarInfo;

  private Map<ASTNode, String> tmpVariables = new HashMap<>();

  private int tmp_counter = 0;

  private boolean embeddedJavaCode;

  private boolean isJava;

  /**
   * Constructor for de.monticore.codegen.parser.ParserGeneratorHelper
   */
  public ParserGeneratorHelper(ASTMCGrammar ast, MCGrammarInfo grammarInfo) {
    Log.errorIfNull(ast);
    this.astGrammar = ast;
    this.qualifiedGrammarName = astGrammar.getPackageList().isEmpty()
            ? astGrammar.getName()
            : Joiner.on('.').join(Names.getQualifiedName(astGrammar.getPackageList()),
            astGrammar.getName());
    this.grammarInfo = grammarInfo;
    this.grammarSymbol = grammarInfo.getGrammarSymbol();
    checkState(qualifiedGrammarName.equals(grammarSymbol.getFullName()));
    this.embeddedJavaCode = true;
    this.isJava = true;
  }

  public ParserGeneratorHelper(ASTMCGrammar ast, MCGrammarInfo grammarInfo, boolean embeddedJavaCode, Languages lang) {
    Log.errorIfNull(ast);
    this.astGrammar = ast;
    this.qualifiedGrammarName = astGrammar.getPackageList().isEmpty()
            ? astGrammar.getName()
            : Joiner.on('.').join(Names.getQualifiedName(astGrammar.getPackageList()),
            astGrammar.getName());
    this.grammarInfo = grammarInfo;
    this.grammarSymbol = grammarInfo.getGrammarSymbol();
    checkState(qualifiedGrammarName.equals(grammarSymbol.getFullName()));
    this.embeddedJavaCode = embeddedJavaCode;
    this.isJava = Languages.JAVA.equals(lang);
  }

  /**
   * @return grammarSymbol
   */
  public MCGrammarSymbol getGrammarSymbol() {
    return this.grammarSymbol;
  }

  /**
   * @return the qualified grammar's name
   */
  public String getQualifiedGrammarName() {
    return qualifiedGrammarName;
  }

  /**
   * @return the name of the start rule
   */
  public String getStartRuleName() {
    if (grammarSymbol.getStartProd().isPresent()) {
      return grammarSymbol.getStartProd().get().getName();
    }
    for (MCGrammarSymbol g: grammarSymbol.getSuperGrammarSymbols()) {
      if (g.getStartProd().isPresent()) {
        return g.getStartProd().get().getName();
      }
    }
    return "";
  }

  /**
   * @return the qualified name of the top ast, i.e., the ast of the start rule.
   */
  public String getQualifiedStartRuleName() {
    if (grammarSymbol.getStartProd().isPresent()) {
      return MCGrammarSymbolTableHelper
              .getQualifiedName(grammarSymbol.getStartProd().get());
    }
    for (MCGrammarSymbol g: grammarSymbol.getSuperGrammarSymbols()) {
      if (g.getStartProd().isPresent()) {
        return MCGrammarSymbolTableHelper
                .getQualifiedName(g.getStartProd().get());
      }
    }
    return "";
  }

  /**
   * @return the package for the generated parser files
   */
  public String getParserPackage() {
    return getQualifiedGrammarName().toLowerCase() + "." + ParserGenerator.PARSER_PACKAGE;
  }

  /**
   * @return the name for a lexsymbol that should be used in an Antlr-File
   */
  public String getLexSymbolName(String constName) {
    Log.errorIfNull(constName);
    return grammarInfo.getLexNamer().getLexName(grammarSymbol, constName);
  }

  /**
   * Get all used LexSymbols, different form information in AST, as inherited
   * ones are integrated as well
   *
   * @return Keys of all lex symbols
   */
  public Set<String> getLexSymbolsWithInherited() {
    return grammarInfo.getLexNamer().getLexnames();
  }

  /**
   * checks if parser must be generated for this rule
   *
   * @param rule
   * @return
   */
  public boolean generateParserForRule(ProdSymbol rule) {
    boolean generateParserForRule = false;
    String ruleName = rule.getName();

    if (rule.isClass()) {
      if (!grammarInfo.isProdLeftRecursive(rule.getName())) {
        generateParserForRule = true;
      }
    }

    if (rule.isIsAbstract() || rule.isIsInterface()) {
      List<PredicatePair> subRules = grammarInfo.getSubRulesForParsing(ruleName);
      generateParserForRule = !subRules.isEmpty();
    }
    return generateParserForRule;
  }

  /**
   * Gets all interface rules which were not excluded from the generation
   *
   * @return List of interface rules
   */
  public List<ProdSymbol> getInterfaceRulesToGenerate() {
    List<ProdSymbol> interfaceRules = Lists.newArrayList();
    for (ProdSymbol ruleSymbol : grammarSymbol.getProdsWithInherited()
            .values()) {
      if (ruleSymbol.isIsAbstract() || ruleSymbol.isIsInterface()) {
        interfaceRules.add(ruleSymbol);
      }
    }
    return interfaceRules;
  }

  /**
   * Gets all non external idents
   *
   * @return List of ident types
   */
  public List<ProdSymbol> getIdentsToGenerate() {
    return embeddedJavaCode ? grammarSymbol.getProdsWithInherited().values().stream()
            .filter(r -> r.isIsLexerProd() && !r.isIsExternal()).collect(Collectors.toList()) : Collections.emptyList();
  }

  public static String getConvertFunction(ProdSymbol symbol) {
    if (symbol.isPresentAstNode() && symbol.isIsLexerProd()) {
      return HelperGrammar.createConvertFunction((ASTLexProd) symbol.getAstNode(), getPrettyPrinter());
    }
    return "";
  }

  /**
   * Gets parser rules
   *
   * @return List of ident types
   */
  public List<ASTProd> getParserRulesToGenerate() {
    // Iterate over all Rules
    List<ASTProd> l = Lists.newArrayList();
    for (ProdSymbol prod: grammarSymbol.getProdsWithInherited().values()) {
      if ((prod.isParserProd() || prod.isIsEnum()) && prod.isPresentAstNode()) {
        l.add(prod.getAstNode());
      }
    }
    return l;
  }

  public List<ASTLexProd> getLexerRulesToGenerate() {
    // Iterate over all LexRules
    List<ASTLexProd> prods = Lists.newArrayList();
    ProdSymbol mcanything = null;
    final Map<String, ProdSymbol> rules = new LinkedHashMap<>();

    // Don't use grammarSymbol.getRulesWithInherited because of changed order
    for (final ProdSymbol ruleSymbol : grammarSymbol.getProds()) {
      rules.put(ruleSymbol.getName(), ruleSymbol);
    }
    for (int i = grammarSymbol.getSuperGrammars().size() - 1; i >= 0; i--) {
      rules.putAll(grammarSymbol.getSuperGrammarSymbols().get(i).getProdsWithInherited());
    }

    for (Entry<String, ProdSymbol> ruleSymbol : rules.entrySet()) {
      if (ruleSymbol.getValue().isIsLexerProd()) {
        ProdSymbol lexProd = ruleSymbol.getValue();
        // MONTICOREANYTHING must be last rule
        if (lexProd.getName().equals(MONTICOREANYTHING)) {
          mcanything = lexProd;
        }
        else {
          prods.add((ASTLexProd) lexProd.getAstNode());
        }
      }
    }
    if (mcanything != null) {
      prods.add((ASTLexProd) mcanything.getAstNode());
    }
    return prods;
  }

  public String getConstantNameForConstant(ASTConstant x) {
    String name;
    if (x.isPresentHumanName()) {
      name = x.getHumanName();
    }
    else {
      name = grammarInfo.getLexNamer().getConstantName(x.getName());
    }

    return name.toUpperCase();
  }

  public String getTmpVarName(ASTNode a) {
    if (!tmpVariables.containsKey(a)) {
      tmpVariables.put(a, getNewTmpVar());
    }
    return tmpVariables.get(a);
  }

  private String getNewTmpVar() {
    return "tmp" + (Integer.valueOf(tmp_counter++)).toString();
  }

  public void resetTmpVarNames() {
    tmpVariables.clear();
    tmp_counter = 0;
  }

  // ----------------------------------------------------

  /**
   * The result is true iff ASTTerminal is iterated
   *
   * @param ast ASTConstantGroup to be evaluated
   * @return true iff ASTConstantGroup is iterated
   */
  public static boolean isIterated(ASTTerminal ast) {
    return ast.getIteration() == ASTConstantsGrammar.PLUS || ast
            .getIteration() == ASTConstantsGrammar.STAR;
  }

  /**
   * The result is true iff ASTOrGroup is iterated
   *
   * @param ast ASTOrGroup to be evaluated
   * @return true iff ASTOrGroup is iterated
   */
  public static boolean isIterated(ASTBlock ast) {
    return ast.getIteration() == ASTConstantsGrammar.PLUS || ast
            .getIteration() == ASTConstantsGrammar.STAR;
  }

  /**
   * Returns the name of a rule
   *
   * @param ast rule
   * @return Name of a rule
   */
  public static String getRuleName(ASTClassProd ast) {
    return ast.getName();
  }

  /**
   * Creates usage name from a NtSym usually from its attribute or creates name
   *
   * @param ast
   * @return
   */

  public static String getUsuageName(ASTNonTerminal ast) {
    // Use Nonterminal name as attribute name starting with lower case latter
    if (ast.isPresentUsageName()) {
      return ast.getUsageName();
    }
    else {
      return StringTransformations.uncapitalize(ast.getName());
    }
  }

  public static boolean isIterated(ASTNonTerminal ast) {
    return ast.getIteration() == ASTConstantsGrammar.PLUS || ast
            .getIteration() == ASTConstantsGrammar.STAR;
  }

  public static String getTypeNameForEnum(String surroundtype, ASTConstantGroup ast) {
    return new StringBuilder("[enum.").append(surroundtype).append(".")
            .append(ast.getUsageName()).toString();
  }

  /**
   * Printable representation of iteration
   *
   * @param i Value from AST
   * @return String representing value i
   */
  public static String printIteration(int i) {
    switch (i) {
      case ASTConstantsGrammar.PLUS:
        return "+";
      case ASTConstantsGrammar.STAR:
        return "*";
      case ASTConstantsGrammar.QUESTION:
        return "?";
      default:
        return "";
    }
  }

  public static String getDefinedType(ASTClassProd rule) {
    return rule.getName();
  }

  /**
   * Returns Human-Readable, antlr conformed name for a rulename
   *
   * @param ast rule name
   * @return Human-Readable, antlr conformed rule name
   */
  public static String getRuleNameForAntlr(ASTNonTerminal ast) {
    return getRuleNameForAntlr(ast.getName());
  }

  /**
   * Returns Human-Readable, antlr conformed name for a rulename
   *
   * @param rulename rule name
   * @return Human-Readable, antlr conformed rule name
   */
  public static String getRuleNameForAntlr(String rulename) {
    return JavaNamesHelper.getNonReservedName(rulename
            .toLowerCase());
  }


  public String getTmpVarNameForAntlrCode(ASTNonTerminal node) {
    Optional<ProdSymbol> prod = MCGrammarSymbolTableHelper.getEnclosingRule(node);
    if (!prod.isPresent()) {
      Log.error("0xA1006 ASTNonterminal " + node.getName() + "(usageName: " + node.getUsageName()
              + ") can't be resolved.");
      return "";
    }
    return getTmpVarName(node);
  }

  public Optional<ASTAlt> getAlternativeForFollowOption(String prodName) {
    return astGrammar.isPresentGrammarOption()
            ? astGrammar.getGrammarOption().getFollowOptionList().stream()
            .filter(f -> f.getProdName().equals(prodName)).map(ASTFollowOption::getAlt).findFirst()
            : Optional.empty();
  }

  public List<ASTAlt> getAlternatives(ASTClassProd ast) {
    if (!ast.getAltList().isEmpty()) {
      return ast.getAltList();
    }
    for (MCGrammarSymbolLoader g : grammarSymbol.getSuperGrammars()) {
      final Optional<ProdSymbol> ruleByName = g.getLoadedSymbol().getProdWithInherited(ast.getName());
      if (ruleByName.isPresent() && ruleByName.get().isClass()) {
        if (ruleByName.get().isPresentAstNode() && ruleByName.get().getAstNode() instanceof ASTClassProd) {
          return ((ASTClassProd)ruleByName.get().getAstNode()).getAltList();
        }
      }
    }
    return Lists.newArrayList();
  }

  public static String getASTClassName(ProdSymbol rule) {
    return MCGrammarSymbolTableHelper.getQualifiedName(rule);
  }

  public static Grammar_WithConceptsPrettyPrinter getPrettyPrinter() {
    if (prettyPrinter == null) {
      prettyPrinter = new Grammar_WithConceptsPrettyPrinter(new IndentPrinter());
    }
    return prettyPrinter;
  }

  /**
   * Neue Zwischenknoten: Action = Statements; ExpressionPredicate = Expression;
   * ASTScript = MCStatement; ASTAntlrCode = MemberDeclarations; ASTActionAntlr
   * = MemberDeclarations;
   *
   * @param node
   * @return
   */
  public static String getText(ASTNode node) {
    Log.errorIfNull(node);

    if (node instanceof ASTAction) {
      StringBuilder buffer = new StringBuilder();
      for (ASTBlockStatement action : ((ASTAction) node).getBlockStatementList()) {
        buffer.append(getPrettyPrinter().prettyprint(action));
      }
      return buffer.toString();
    }
    if (node instanceof ASTJavaCode) {
      StringBuilder buffer = new StringBuilder();
      for (ASTClassMemberDeclaration action : ((ASTJavaCode) node).getClassMemberDeclarationList()) {
        buffer.append(getPrettyPrinter().prettyprint(action));

      }
      return buffer.toString();
    }
    if (node instanceof ASTExpressionPredicate) {
      String exprPredicate = getPrettyPrinter()
              .prettyprint(((ASTExpressionPredicate) node).getExpression());
      Log.debug("ASTExpressionPredicate:\n" + exprPredicate, ParserGenerator.LOG);
      return exprPredicate;
    }
    if (node instanceof ASTGrammar_WithConceptsNode) {
      String output = getPrettyPrinter().prettyprint((ASTGrammar_WithConceptsNode) node);
      Log.debug("ASTGrammar_WithConceptsNode:\n" + output, ParserGenerator.LOG);
      return output;
    }
    return "";
  }

  public static String getParseRuleName(ProdSymbol rule) {
    return JavaNamesHelper.getNonReservedName(StringTransformations.uncapitalize(rule.getName()));
  }

  public static String getMCParserWrapperName(ProdSymbol rule) {
    return StringTransformations.capitalize(JavaNamesHelper.getNonReservedName(rule.getName()));
  }

  /**
   * @return the qualified name for this type
   */
  // TODO GV: change implementation
  public static String getQualifiedName(ProdSymbol symbol) {
    if (!symbol.isPresentAstNode()) {
      return "UNKNOWN_TYPE";
    }
    if (symbol.isPresentAstNode() && symbol.isIsLexerProd()) {
      return getLexType( symbol.getAstNode());
    }
    if (symbol.isIsEnum()) {

      return "int";
      // TODO GV:
      // return getConstantType();
    }
    return MCGrammarSymbolTableHelper.getQualifiedName(symbol.getAstNode(), symbol,
            GeneratorHelper.AST_PREFIX, "");
  }

  public static String getDefaultValue(ProdSymbol symbol) {
    String name = getQualifiedName(symbol);
    if ("int".equals(name)) {
      return "0";
    }
    if ("boolean".equals(name)) {
      return "false";
    }
    return "null";
  }

  private static String getLexType(ASTNode node) {
      if (node instanceof ASTLexProd) {
        return HelperGrammar.createConvertType((ASTLexProd) node);
      }
      if (node instanceof ASTLexActionOrPredicate) {
        return "String";
      }
    return "UNKNOWN_TYPE";

  }

  public static String formatAttributeValue(Optional<Integer> value) {
    if (!value.isPresent()) {
      return "undef";
    }
    else if (value.get() == GeneratorHelper.STAR) {
      return "*";
    }
    return Integer.toString(value.get());
  }

  public boolean isEmbeddedJavaCode() {
    return this.embeddedJavaCode;
  }

  public boolean isJava() {
    return this.isJava;
  }
}

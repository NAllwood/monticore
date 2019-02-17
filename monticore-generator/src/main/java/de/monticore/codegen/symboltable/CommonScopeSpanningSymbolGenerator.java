/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.symboltable;

import static com.google.common.collect.Lists.newArrayList;
import static de.monticore.codegen.GeneratorHelper.*;
import static de.se_rwth.commons.Names.getPathFromPackage;
import static de.se_rwth.commons.Names.getSimpleName;
import static java.nio.file.Paths.get;
import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTSymbolRule;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.Names;

public class CommonScopeSpanningSymbolGenerator implements ScopeSpanningSymbolGenerator {

  public void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
                       IterablePath handCodedPath, MCProdSymbol prodSymbol) {

    generateScopeSpanningSymbol(genEngine, genHelper, handCodedPath, prodSymbol);
  }

  protected void generateScopeSpanningSymbol(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
                                             IterablePath handCodedPath, MCProdSymbol prodSymbol) {
    String className = prodSymbol.getSymbolDefinitionKind().isPresent() ? prodSymbol.getSymbolDefinitionKind().get() : prodSymbol.getName();
    String symbolName = getSimpleTypeNameToGenerate(getSimpleName(className + SYMBOL),
            genHelper.getTargetPackage(), handCodedPath);
    String builderName = getSimpleTypeNameToGenerate(getSimpleName(className + SYMBOL + BUILDER),
            genHelper.getTargetPackage(), handCodedPath);
    String serializerName = getSimpleTypeNameToGenerate(getSimpleName(className + SYMBOL + SERIALIZER),
            genHelper.getTargetPackage(), handCodedPath);

    final Path filePath = get(getPathFromPackage(genHelper.getTargetPackage()), symbolName + ".java");
    final Path builderFilePath = get(getPathFromPackage(genHelper.getTargetPackage()), builderName + ".java");
    final Path serializerFilePath = get(getPathFromPackage(genHelper.getTargetPackage()), serializerName + ".java");

    if (prodSymbol.getAstNode().isPresent()) {
      Optional<ASTSymbolRule> symbolRule = empty();
      List<String> imports = newArrayList();
      genHelper.getAllCds(genHelper.getCd()).stream()
              .forEach(s -> imports.add(s.getFullName().toLowerCase()));
      ASTMCGrammar grammar = genHelper.getGrammarSymbol().getAstGrammar().get();
      List<ASTSymbolRule> symbolRules = grammar.getSymbolRuleList();
      if (prodSymbol.getAstNode().isPresent() && prodSymbol.getSymbolDefinitionKind().isPresent()) {
        for (ASTSymbolRule sr : symbolRules) {
          if (sr.getType().equals(prodSymbol.getSymbolDefinitionKind().get())) {
            symbolRule = of(sr);
            break;
          }
        }
      }
      genEngine.generate("symboltable.ScopeSpanningSymbol", filePath, prodSymbol.getAstNode().get(),
              symbolName, genHelper.getGrammarSymbol().getName() + SCOPE, prodSymbol, symbolRule, imports);
      genEngine.generate("symboltable.SymbolBuilder", builderFilePath, 
              prodSymbol.getAstNode().get(), builderName, className, symbolRule, imports);
      genEngine.generate("symboltable.serialization.SymbolSerialization", serializerFilePath, 
              prodSymbol.getAstNode().get(), serializerName, className, symbolRule, imports);
    }
  }

}

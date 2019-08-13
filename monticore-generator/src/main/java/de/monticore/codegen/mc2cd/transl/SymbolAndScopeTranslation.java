package de.monticore.codegen.mc2cd.transl;

import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.codegen.symboltable.SymbolTableGenerator;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDType;
import de.monticore.utils.Link;

import java.util.Optional;
import java.util.function.UnaryOperator;

import static de.monticore.codegen.GeneratorHelper.SYMBOL;

public class SymbolAndScopeTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(Link<ASTMCGrammar, ASTCDCompilationUnit> links) {
    for (Link<ASTClassProd, ASTCDClass> link : links.getLinks(ASTClassProd.class, ASTCDClass.class)) {
      final ASTClassProd astClassProd = link.source();
      final ASTCDClass astcdClass = link.target();
      addSymbolAndScopeStereotypes(astClassProd, astcdClass);
    }

    for (Link<ASTInterfaceProd, ASTCDInterface> link : links.getLinks(ASTInterfaceProd.class, ASTCDInterface.class)) {
      final ASTInterfaceProd astInterfaceProd = link.source();
      final ASTCDInterface astcdInterface = link.target();
      addSymbolAndScopeStereotypes(astInterfaceProd, astcdInterface);
    }
    return links;
  }

  private void addSymbolAndScopeStereotypes(ASTParserProd grammarProd, ASTCDType cdType) {
    for (ASTSymbolDefinition symbolDefinition : grammarProd.getSymbolDefinitionList()) {
      if (symbolDefinition.isGenSymbol()) {
        final Optional<MCGrammarSymbol> grammarSymbol = MCGrammarSymbolTableHelper
            .getMCGrammarSymbol(grammarProd);
        if (symbolDefinition.isPresentSymbolName()
            && grammarSymbol.isPresent()) {
          //extra information into stereotype value if a symboltype is already defined in the grammar
          String symbolName = symbolDefinition.getSymbolName();
          String qualifiedName;
          Optional<MCProdSymbol> symbolType = grammarProd.getEnclosingScope().<MCProdSymbol>resolve(symbolName, MCProdSymbol.KIND);
          if (symbolType.isPresent()) {
            String packageName = symbolType.get().getFullName().substring(0, symbolType.get().getFullName().lastIndexOf(".")).toLowerCase();
            qualifiedName = packageName + "." + SymbolTableGenerator.PACKAGE + "." + symbolName;
          } else {
            qualifiedName = grammarSymbol.get().getFullName().toLowerCase() + "." +
                SymbolTableGenerator.PACKAGE + "." + symbolName;
          }
          TransformationHelper.addStereoType(cdType,
              MC2CDStereotypes.SYMBOL.toString(), qualifiedName + SYMBOL);
        } else {
          TransformationHelper.addStereoType(cdType,
              MC2CDStereotypes.SYMBOL.toString());
        }
      } else {
        if (symbolDefinition.isGenScope()) {
          TransformationHelper.addStereoType(cdType,
              MC2CDStereotypes.SCOPE.toString());
        }
      }
    }
  }
}

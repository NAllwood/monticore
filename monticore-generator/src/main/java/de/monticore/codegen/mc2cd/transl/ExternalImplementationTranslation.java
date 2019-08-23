/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.utils.Link;

import java.util.Optional;
import java.util.function.UnaryOperator;

public class ExternalImplementationTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    for (Link<ASTClassProd, ASTCDClass> link : rootLink.getLinks(ASTClassProd.class,
        ASTCDClass.class)) {
      String name = link.source().getName();
      Optional<ProdSymbol> ruleSymbol = MCGrammarSymbolTableHelper.resolveRuleInSupersOnly(
          link.source(), name);
      if (ruleSymbol.isPresent() && ruleSymbol.get().isExternal()) {
        link.target().getInterfaceList().add(
            TransformationHelper.createObjectType(TransformationHelper.getGrammarNameAsPackage(
                ruleSymbol.get()) + "AST" + name + "Ext"));
      }
    }

    return rootLink;
  }

}

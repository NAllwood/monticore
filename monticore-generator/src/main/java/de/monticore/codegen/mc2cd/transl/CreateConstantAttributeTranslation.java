/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTConstantGroup;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesNodeFactory;
import de.monticore.utils.Link;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;
import java.util.function.UnaryOperator;

public class CreateConstantAttributeTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    
    for (Link<ASTClassProd, ASTCDClass> link : rootLink.getLinks(
        ASTClassProd.class,
        ASTCDClass.class)) {
      createConstantAttributes(link);
    }
    
    return rootLink;
  }
  
  // TODO SO <- GV : please change and move to the ConstantTypeTranslation
  private void createConstantAttributes(Link<ASTClassProd, ASTCDClass> link) {
    Optional<ProdSymbol> typeProd = MCGrammarSymbolTableHelper
        .getMCGrammarSymbol(link.source().getEnclosingScope()).get()
        .getSpannedScope()
        .resolveProd(link.source().getName());
    if (!typeProd.isPresent()) {
      Log.debug("Unknown type of the grammar rule "
          + link.source().getName() + " in the grammar "
          + MCGrammarSymbolTableHelper.getMCGrammarSymbol(link.source().getEnclosingScope()).get()
              .getFullName()
          + "\n Check if this a kind of rule A:B=... ",
          CreateConstantAttributeTranslation.class.getName());
      return;
    }
    
    ProdSymbol prodSymbol = typeProd.get();
    for (RuleComponentSymbol prodComponent : prodSymbol.getProdComponents()) {
      if (prodComponent.isConstantGroup() && prodComponent.getAstNode().isPresent()
          && prodComponent.getAstNode().get() instanceof ASTConstantGroup) {
        boolean iterated = MCGrammarSymbolTableHelper.isConstGroupIterated(prodComponent);
        ASTCDAttribute cdAttribute = CD4AnalysisNodeFactory
            .createASTCDAttribute();
        cdAttribute
            .setName(MCGrammarSymbolTableHelper.getConstantName(prodComponent).orElse(""));
        int constantType = iterated ? ASTConstantsMCBasicTypes.INT : ASTConstantsMCBasicTypes.BOOLEAN;
        cdAttribute.setMCType(MCBasicTypesNodeFactory
            .createASTMCPrimitiveType(constantType));
        link.target().getCDAttributeList().add(cdAttribute);
      }
    }
  }
  
}

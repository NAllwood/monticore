/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.AdditionalAttributeSymbol;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.utils.Link;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class InheritedAttributesTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    
    for (Link<ASTClassProd, ASTCDClass> link : rootLink.getLinks(ASTClassProd.class,
        ASTCDClass.class)) {
      handleInheritedNonTerminals(link);
      handleInheritedAttributeInASTs(link);
    }

    return rootLink;
  }
  
  private void handleInheritedNonTerminals(Link<ASTClassProd, ASTCDClass> link) {
    for (Entry<ASTProd, List<ASTNonTerminal>> entry : GeneratorHelper.getInheritedNonTerminals(link.source())
        .entrySet()) {
      for (ASTNonTerminal nonTerminal : entry.getValue()) {
        ASTCDAttribute cdAttribute = createCDAttribute(link.source(), entry.getKey());
        link.target().getCDAttributeList().add(cdAttribute);
        new Link<>(nonTerminal, cdAttribute, link);
      }
    }
  }
  
  private void handleInheritedAttributeInASTs(Link<ASTClassProd, ASTCDClass> link) {
    for (Entry<ASTProd, Collection<AdditionalAttributeSymbol>> entry : getInheritedAttributeInASTs(
        link.source()).entrySet()) {
      for (AdditionalAttributeSymbol attributeInAST : entry.getValue()) {
        ASTCDAttribute cdAttribute = createCDAttribute(link.source(), entry.getKey());
        link.target().getCDAttributeList().add(cdAttribute);
        if (attributeInAST.isPresentAstNode()) {
          new Link<>(attributeInAST.getAstNode(), cdAttribute, link);
        }
      }
    }
  }

  private ASTCDAttribute createCDAttribute(ASTProd inheritingNode, ASTProd definingNode) {
    List<ASTInterfaceProd> interfacesWithoutImplementation = getAllInterfacesWithoutImplementation(
        inheritingNode);
    
    String superGrammarName = MCGrammarSymbolTableHelper.getMCGrammarSymbol(definingNode.getEnclosingScope())
        .map(MCGrammarSymbol::getFullName)
        .orElse("");
    
    ASTCDAttribute cdAttribute = CD4AnalysisNodeFactory.createASTCDAttribute();
    if (!interfacesWithoutImplementation.contains(definingNode)) {
      TransformationHelper.addStereoType(
          cdAttribute, MC2CDStereotypes.INHERITED.toString(), superGrammarName);
    }
    return cdAttribute;
  }

  
  private Map<ASTProd, Collection<AdditionalAttributeSymbol>> getInheritedAttributeInASTs(
      ASTProd astNode) {
    return GeneratorHelper.getAllSuperProds(astNode).stream()
        .distinct()
        .collect(Collectors.toMap(Function.identity(), astProd -> astProd.getSymbolOpt()
            .map(ProdSymbol::getProdAttributes)
            .orElse(Collections.emptyList())));
  }

  /**
  * @return a list of interfaces that aren't already implemented by another
   * class higher up in the type hierarchy. (the list includes interfaces
   * extended transitively by other interfaces)
   */
  private List<ASTInterfaceProd> getAllInterfacesWithoutImplementation(ASTProd astNode) {
    List<ASTInterfaceProd> directInterfaces = GeneratorHelper.getDirectSuperProds(astNode).stream()
        .filter(ASTInterfaceProd.class::isInstance)
        .map(ASTInterfaceProd.class::cast)
        .collect(Collectors.toList());
    List<ASTInterfaceProd> allSuperRules = new ArrayList<>();
    for (ASTInterfaceProd superInterface : directInterfaces) {
      allSuperRules.addAll(getAllInterfacesWithoutImplementation(superInterface));
    }
    allSuperRules.addAll(directInterfaces);
    return allSuperRules;
  }

  
}

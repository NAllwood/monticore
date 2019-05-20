/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.ast.ASTNode;
import de.monticore.grammar.grammar._ast.ASTASTRule;
import de.monticore.grammar.grammar._ast.ASTAbstractProd;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.types.BasicGenericsTypesPrinter;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Checks that nonterminal names start lower-case.
 */
public class NTAndASTRuleExtendType implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA4013";

  public static final String ERROR_MSG_FORMAT = " The AST rule for %s must not extend the type " +
          "%s because the production already extends a type.";

  @Override
  public void check(ASTMCGrammar a) {
    MCGrammarSymbol grammarSymbol = (MCGrammarSymbol) a.getSymbol();
    for (ASTASTRule rule : a.getASTRuleList()) {
      if (!rule.getASTSuperClassList().isEmpty()) {
        Optional<MCProdSymbol> ruleSymbol = grammarSymbol.getProdWithInherited(rule.getType());
        if (ruleSymbol.isPresent()) {
          if (ruleSymbol.get().isClass()) {
            Optional<ASTNode> prod = ruleSymbol.get().getAstNode();
            if (prod.isPresent() && prod.get() instanceof ASTClassProd
                    && (!((ASTClassProd) prod.get()).getASTSuperClassList().isEmpty()
                    || !((ASTClassProd) prod.get()).getSuperRuleList().isEmpty())) {
              Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, rule.getType(),
                      BasicGenericsTypesPrinter.printType(rule.getASTSuperClassList().get(0))),
                      rule.get_SourcePositionStart());
            }
          } else if (ruleSymbol.get().getAstNode().isPresent()
                  && ruleSymbol.get().getAstNode().get() instanceof ASTAbstractProd) {
            ASTAbstractProd prod = (ASTAbstractProd) ruleSymbol.get().getAstNode().get();
            if (!prod.getASTSuperClassList().isEmpty() || !prod.getSuperRuleList().isEmpty()) {
              Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, rule.getType(),
                      BasicGenericsTypesPrinter.printType(rule.getASTSuperClassList().get(0))),
                      rule.get_SourcePositionStart());
            }
          }
        }
      }
    }
  }
}

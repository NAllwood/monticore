/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.grammar.cocos;

import java.util.List;
import java.util.Optional;

import de.monticore.grammar.grammar._ast.ASTInterfaceProd;
import de.monticore.grammar.grammar._ast.ASTRuleReference;
import de.monticore.grammar.grammar._cocos.GrammarASTInterfaceProdCoCo;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that nonterminals only extends abstract or normal nonterminals..
 *
 * @author KH
 */
public class InterfaceNTOnlyExtendInterfaceNTs implements GrammarASTInterfaceProdCoCo {
  
  public static final String ERROR_CODE = "0xA2116";
  
  public static final String ERROR_MSG_FORMAT = " The interface nonterminal %s must not extend the%s nonterminal %s. "
      +
      "Interface nonterminals may only extend interface nonterminals.";
  
  @Override
  public void check(ASTInterfaceProd a) {
    if (!a.getSuperInterfaceRuleList().isEmpty()) {
      List<ASTRuleReference> superRules = a.getSuperInterfaceRuleList();
      for (ASTRuleReference sr : superRules) {
        Optional<MCProdSymbol> ruleSymbol = a.getEnclosingScope().get().resolve(sr.getName(),
            MCProdSymbol.KIND);
        if (ruleSymbol.isPresent()) {
          MCProdSymbol r = ruleSymbol.get();
          boolean isAbstract = r.isAbstract();
          boolean isExternal = r.isExternal();
          if (!r.isInterface()) {
            Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName(),
                isAbstract ? " abstract" : isExternal ? " external" : "", r.getName()),
                a.get_SourcePositionStart());
          }
        }
      }
    }
  }
  
}

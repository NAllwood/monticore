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

package de.monticore.codegen.mc2cd.transl;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.LexNamer;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDEnum;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDEnumConstant;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.utils.Link;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 */
public class ConstantsTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  
  public static final String CONSTANTS_ENUM = "Literals";
  
  private LexNamer lexNamer;
  
  /**
   * Constructor for de.monticore.codegen.mc2cd.transl.ConstantsTranslation
   * 
   * @param lexNamer
   */
  public ConstantsTranslation(LexNamer lexNamer) {
    this.lexNamer = lexNamer;
  }
  
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    
    ASTCDEnum constantsEnum = CD4AnalysisNodeFactory.createASTCDEnum();
    constantsEnum.setName(rootLink.source().getName() + CONSTANTS_ENUM);
    rootLink.target().getCDDefinition().getCDEnumList().add(constantsEnum);
    Set<String> grammarConstants = TransformationHelper
        .getAllGrammarConstants(rootLink.source()).stream().map(c -> lexNamer.getConstantName(c))
        .collect(Collectors.toSet());
    Collection<String> sortedConstants = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
    sortedConstants.addAll(grammarConstants);
    for (String grammarConstant : sortedConstants) {
      ASTCDEnumConstant constant = CD4AnalysisNodeFactory.createASTCDEnumConstant();
      constant.setName(grammarConstant);
      constantsEnum.getCDEnumConstantList().add(constant);
    }
    
    return rootLink;
  }
  
}

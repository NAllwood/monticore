/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.Log;

public class OverridingEnumNTsTest extends CocoTest {
  
  private final String MESSAGE = " The production for the enum nonterminal E must not be overridden."; 
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4027.A4027";
  
  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new OverridingEnumNTs());
  }
  
  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, OverridingEnumNTs.ERROR_CODE, MESSAGE, checker);
  }
  
  @Test
  public void testCorrect() {
    testValidGrammar("cocos.valid.Overriding", checker);
  }
  
}

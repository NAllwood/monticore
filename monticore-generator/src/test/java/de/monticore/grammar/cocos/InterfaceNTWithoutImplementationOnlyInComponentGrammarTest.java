/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.Log;

import static de.monticore.grammar.cocos.InterfaceNTWithoutImplementationOnlyInComponentGrammar.ERROR_CODE;
import static de.se_rwth.commons.logging.Log.enableFailQuick;

public class InterfaceNTWithoutImplementationOnlyInComponentGrammarTest extends CocoTest {

  private final String MESSAGE = " The interface nonterminal A must not be used without nonterminals " +
          "implementing it in a grammar not marked as a grammar component.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A0278.A0278";
  private final String grammar2 = "cocos.invalid.A0278.A0278b";
  private final String grammar3 = "cocos.invalid.A0278.A0278b";

  @BeforeClass
  public static void disableFailQuick() {
    enableFailQuick(false);
    checker.addCoCo(new InterfaceNTWithoutImplementationOnlyInComponentGrammar());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, ERROR_CODE,
            MESSAGE, checker);
  }

  @Test
  public void testInvalid2() {
    testInvalidGrammar(grammar2, ERROR_CODE,
        MESSAGE, checker);
  }

  @Test
  public void testInvalid3() {
    testInvalidGrammar(grammar3, ERROR_CODE,
        MESSAGE, checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("cocos.valid.Component", checker);
  }

  @Test
  public void testCorrect2() {
    testValidGrammar("cocos.valid.Overriding", checker);
  }


}

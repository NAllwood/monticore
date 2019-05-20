/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mcbasicliterals;

import de.monticore.mcbasicliterals._ast.ASTBasicLongLiteral;
import de.monticore.mcbasicliterals._ast.ASTLiteral;
import de.monticore.testmcbasicliterals._parser.TestMCBasicLiteralsParser;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.Assert.*;

public class LongLiteralsTest {


  @BeforeClass
  public static void init() {
    Log.init();
    Log.enableFailQuick(false);
  }

  private void checkLongLiteral(long l, String s) throws IOException {
    TestMCBasicLiteralsParser parser = new TestMCBasicLiteralsParser();
    Optional<ASTLiteral> lit = parser.parseLiteral(new StringReader(s));
    assertTrue(lit.isPresent());
    assertTrue(lit.get() instanceof ASTBasicLongLiteral);
    assertEquals(l, ((ASTBasicLongLiteral) lit.get()).getValue());
  }

  private void checkFalse(String s) throws IOException {
    TestMCBasicLiteralsParser parser = new TestMCBasicLiteralsParser();
    Optional<ASTBasicLongLiteral> lit = parser.parseBasicLongLiteral(new StringReader(s));
    assertTrue(!lit.isPresent());
  }

  @Test
  public void testLongLiterals() {
    try {
      // decimal number
      checkLongLiteral(0L, "0L");
      checkLongLiteral(123L, "123L");
      checkLongLiteral(10L, "10L");
      checkLongLiteral(5L, "5L");
      checkLongLiteral(5L, "05L");

    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testFalse() {
    try {
      // hexadezimal number
      checkFalse("0x12L");
      checkFalse("0XeffL");
      checkFalse("0x1234567890L");
      checkFalse("0xabcdefL");
      checkFalse("0x0L");
      checkFalse("0xaL");
      checkFalse("0xC0FFEEL");
      checkFalse("0x005fL");
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
}

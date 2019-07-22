package de.monticore.codegen.cd2java.data;

import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertBoolean;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getInterfaceBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC_ABSTRACT;
import static org.junit.Assert.*;

public class DataInterfaceDecoratorTest extends DecoratorTestCase {
  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDInterface dataInterface;

  @Before
  public void setUp() {
    ASTCDCompilationUnit cd = this.parse("de", "monticore", "codegen", "data", "DataInterface");
    ASTCDInterface clazz = getInterfaceBy("ASTA", cd);
    this.glex.setGlobalValue("service", new AbstractService(cd));

    MethodDecorator methodDecorator = new MethodDecorator(glex);
    InterfaceDecorator dataDecorator = new InterfaceDecorator(this.glex, new DataDecoratorUtil(), methodDecorator, new ASTService(cd));
    this.dataInterface = dataDecorator.decorate(clazz);

    this.glex.setGlobalValue("astHelper", new DecorationHelper());
  }

  @Test
  public void testClassSignature() {
    assertEquals("ASTA", dataInterface.getName());
  }

  @Test
  public void testAttributesCount() {
    assertTrue(dataInterface.isEmptyCDAttributes());
  }
  
  @Test
  public void testMethodCount(){
    assertEquals(51, dataInterface.sizeCDMethods());
  }

  @Test
  public void testDeepEquals() {
    ASTCDMethod method = getMethodBy("deepEquals", 1, dataInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertBoolean(method.getReturnType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getType());
    assertEquals("o", parameter.getName());
  }

  @Test
  public void testDeepEqualsForceSameOrder() {
    ASTCDMethod method = getMethodBy("deepEquals", 2, dataInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertBoolean(method.getReturnType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(2, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getType());
    assertEquals("o", parameter.getName());

    parameter = method.getCDParameter(1);
    assertBoolean(parameter.getType());
    assertEquals("forceSameOrder", parameter.getName());
  }

  @Test
  public void testDeepEqualsWithComments() {
    ASTCDMethod method = getMethodBy("deepEqualsWithComments", 1, dataInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertBoolean(method.getReturnType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getType());
    assertEquals("o", parameter.getName());
  }

  @Test
  public void testDeepEqualsWithCommentsForceSameOrder() {
    ASTCDMethod method = getMethodBy("deepEqualsWithComments", 2, dataInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertBoolean(method.getReturnType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(2, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getType());
    assertEquals("o", parameter.getName());

    parameter = method.getCDParameter(1);
    assertBoolean(parameter.getType());
    assertEquals("forceSameOrder", parameter.getName());
  }

  @Test
  public void testEqualAttributes() {
    ASTCDMethod method = getMethodBy("equalAttributes", dataInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertBoolean(method.getReturnType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getType());
    assertEquals("o", parameter.getName());
  }

  @Test
  public void testEqualsWithComments() {
    ASTCDMethod method = getMethodBy("equalsWithComments", dataInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertBoolean(method.getReturnType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getType());
    assertEquals("o", parameter.getName());
  }

  @Test
  public void testDeepClone() {
    ASTCDMethod method = getMethodBy("deepClone", 0, dataInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertDeepEquals(dataInterface.getName(), method.getReturnType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.INTERFACE, dataInterface, dataInterface);
    System.out.println(sb.toString());
  }

}

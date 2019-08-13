package de.monticore.codegen.cd2java._ast.ast_new.reference.referencedSymbol;

import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.reference.ASTReferenceDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.factories.CDTypeFacade;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static org.junit.Assert.*;

public class ASTReferencedSymbolDecoratorOptionalTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDClass astClass;

  private CDTypeFacade cdTypeFacade = CDTypeFacade.getInstance();

  private static final String NAME_SYMBOL = "de.monticore.codegen.ast.referencedsymbol._symboltable.FooSymbol";

  @Before
  public void setup() {
    this.cdTypeFacade = CDTypeFacade.getInstance();
    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    ASTCDCompilationUnit ast = this.parse("de", "monticore", "codegen", "ast", "ReferencedSymbol");
    this.glex.setGlobalValue("service", new AbstractService(ast));

    ASTReferenceDecorator decorator = new ASTReferenceDecorator(this.glex, new SymbolTableService(ast));
    ASTCDClass clazz = getClassBy("ASTBarOpt", ast);
    this.astClass = decorator.decorate(clazz);
  }

  @Test
  public void testClass() {
    assertEquals("ASTBarOpt", astClass.getName());
  }

  @Test
  public void testAttributes() {
    assertFalse(astClass.isEmptyCDAttributes());
    assertEquals(2, astClass.sizeCDAttributes());
  }

  @Test
  public void testNameAttribute() {
    ASTCDAttribute nameAttribute = getAttributeBy("name", astClass);
    assertTrue(nameAttribute.getModifier().isProtected());
    assertTrue(nameAttribute.getModifier().isPresentStereotype());
    ASTCDStereotype stereotype = nameAttribute.getModifier().getStereotype();
    assertEquals(1, stereotype.sizeValues());
    assertEquals("referencedSymbol", stereotype.getValue(0).getName());
    assertTrue(stereotype.getValue(0).isPresentValue());
    assertEquals("de.monticore.codegen.ast.referencedSymbol.FooSymbol", stereotype.getValue(0).getValue());
    assertDeepEquals(cdTypeFacade.createTypeByDefinition("Optional<String>"), nameAttribute.getType());
  }

  @Test
  public void testSymbolAttribute() {
    ASTCDAttribute symbolAttribute = getAttributeBy("nameSymbol", astClass);
    assertTrue(symbolAttribute.getModifier().isProtected());
    assertOptionalOf(NAME_SYMBOL, symbolAttribute.getType());
  }

  @Test
  public void testMethods() {
    assertEquals(6, astClass.getCDMethodList().size());
  }


  @Test
  public void testGetNameSymbolMethod() {
    ASTCDMethod method = getMethodBy("getNameSymbol", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    ASTType astType = this.cdTypeFacade.createTypeByDefinition(NAME_SYMBOL);
    assertDeepEquals(astType, method.getReturnType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetNameSymbolOptMethod() {
    ASTCDMethod method = getMethodBy("getNameSymbolOpt", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertOptionalOf(NAME_SYMBOL, method.getReturnType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testIsPresentNameSymbolMethod() {
    ASTCDMethod method = getMethodBy("isPresentNameSymbol", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertBoolean(method.getReturnType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, astClass, astClass);
    System.out.println(sb.toString());
  }

}

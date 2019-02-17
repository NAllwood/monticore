package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCMultipleGenericType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCTypeParameters;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCTypeVariableDeclaration;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCWildcardType;
import de.monticore.types.mcfullgenerictypestest._parser.MCFullGenericTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MCFullGenericTypesPrettyPrinterTest {

  @BeforeClass
  public static void init() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Before
  public void setUp() {
    Log.getFindings().clear();
  }

  @Test
  public void testMCWildcardTypeExtends() throws IOException {
    MCFullGenericTypesTestParser parser = new MCFullGenericTypesTestParser();
    Optional<ASTMCWildcardType> ast = parser.parse_StringMCWildcardType("? extends java.util.List");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCWildcardType wildcardType = ast.get();
    MCFullGenericTypesPrettyPrinter printer = new MCFullGenericTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCWildcardType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(wildcardType.deepEquals(ast.get()));
  }

  @Test
  public void testMCWildcardTypeSuper() throws IOException {
    MCFullGenericTypesTestParser parser = new MCFullGenericTypesTestParser();
    Optional<ASTMCWildcardType> ast = parser.parse_StringMCWildcardType("? super de.monticore.ASTNode");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCWildcardType wildcardType = ast.get();
    MCFullGenericTypesPrettyPrinter printer = new MCFullGenericTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCWildcardType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(wildcardType.deepEquals(ast.get()));
  }

  @Test
  public void testMCMultipleGenericType() throws IOException {
    MCFullGenericTypesTestParser parser = new MCFullGenericTypesTestParser();
    Optional<ASTMCMultipleGenericType> ast = parser.parse_StringMCMultipleGenericType("java.util.List<Integer>.some.util.Set<String>.Opt<List<String>>");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCMultipleGenericType complexReferenceType = ast.get();
    MCFullGenericTypesPrettyPrinter printer = new MCFullGenericTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCMultipleGenericType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(complexReferenceType.deepEquals(ast.get()));
  }

  @Test
  public void testMCArrayType() throws IOException {
    //have to use ASTMCType because of left recursion in ASTMCArrayType there is no parse Method
    MCFullGenericTypesTestParser parser = new MCFullGenericTypesTestParser();
    Optional<ASTMCType> ast = parser.parse_StringMCType("String[][]");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCType type = ast.get();
    MCFullGenericTypesPrettyPrinter printer = new MCFullGenericTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(type.deepEquals(ast.get()));
  }

  @Test
  public void testMCTypeParameters() throws IOException {
    MCFullGenericTypesTestParser parser = new MCFullGenericTypesTestParser();
    Optional<ASTMCTypeParameters> ast = parser.parse_StringMCTypeParameters("<A, B extends List<A>.A<Name>, C extends Set<F>.Opt<H> &  Map<G>.List<T>>");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCTypeParameters typeParameters = ast.get();
    MCFullGenericTypesPrettyPrinter printer = new MCFullGenericTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCTypeParameters(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(typeParameters.deepEquals(ast.get()));
  }

  @Test
  public void testMCTypeVariableDeclaration() throws IOException {
    MCFullGenericTypesTestParser parser = new MCFullGenericTypesTestParser();
    Optional<ASTMCTypeVariableDeclaration> ast = parser.parse_StringMCTypeVariableDeclaration(" C extends Set<F>.Opt<H> &  Map<G>.List<T> & Foo<Bar>.A<B>");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCTypeVariableDeclaration typeVariableDeclaration = ast.get();
    MCFullGenericTypesPrettyPrinter printer = new MCFullGenericTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCTypeVariableDeclaration(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(typeVariableDeclaration.deepEquals(ast.get()));
  }
}

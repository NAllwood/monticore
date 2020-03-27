/* (c) https://github.com/MontiCore/monticore */
package de.monticore.prettyprint;

import de.monticore.javalight._ast.*;
import de.monticore.testjavalight._parser.TestJavaLightParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JavaLightPrettyPrinterTest {

  private TestJavaLightParser parser = new TestJavaLightParser();

  private JavaLightPrettyPrinterDelegator prettyPrinter = new JavaLightPrettyPrinterDelegator(new IndentPrinter());

  @BeforeClass
  public static void setUp() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Before
  public void init() {
    prettyPrinter.getPrinter().clearBuffer();
  }

  @Test
  public void testEmptyDeclaration() throws IOException {
    Optional<ASTEmptyDeclaration> result = parser.parse_StringEmptyDeclaration(";");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTEmptyDeclaration ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringEmptyDeclaration(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testClassBlock() throws IOException {
    Optional<ASTClassBlock> result = parser.parse_StringClassBlock("static { private Integer foo = a }");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTClassBlock ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringClassBlock(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testMethodDeclaration() throws IOException {
    Optional<ASTMethodDeclaration> result = parser.parse_StringMethodDeclaration("private static final int foo(String s, boolean b)[][][] throws e.Exception { private Integer foo = a }");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTMethodDeclaration ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringMethodDeclaration(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testConstructorDeclaration() throws IOException {
    Optional<ASTConstructorDeclaration> result = parser.parse_StringConstructorDeclaration("public ClassName(String s, boolean b) throws e.Exception { private Integer foo = a }");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTConstructorDeclaration ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringConstructorDeclaration(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testFieldDeclaration() throws IOException {
    Optional<ASTFieldDeclaration> result = parser.parse_StringFieldDeclaration("private static List a = b, c = d;");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTFieldDeclaration ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringFieldDeclaration(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testConstDeclaration() throws IOException {
    Optional<ASTConstDeclaration> result = parser.parse_StringConstDeclaration("private static Foo foo [][][] = a;");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTConstDeclaration ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringConstDeclaration(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testConstantDeclarator() throws IOException {
    Optional<ASTConstantDeclarator> result = parser.parse_StringConstantDeclarator("foo [][][] = a");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTConstantDeclarator ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringConstantDeclarator(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testInterfaceMethodDeclaration() throws IOException {
    Optional<ASTInterfaceMethodDeclaration> result = parser.parse_StringInterfaceMethodDeclaration("private static final int foo(String s, boolean b)[][][] throws e.Exception;");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTInterfaceMethodDeclaration ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringInterfaceMethodDeclaration(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testMethodSignature() throws IOException {
    Optional<ASTMethodSignature> result = parser.parse_StringMethodSignature("private static final int foo(String s, boolean b)[][][] throws e.Exception");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTMethodSignature ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringMethodSignature(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testThrows() throws IOException {
    Optional<ASTThrows> result = parser.parse_StringR__throws("a.b.c.D, person.A ");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTThrows ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringR__throws(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testFormalParameters() throws IOException {
    Optional<ASTFormalParameters> result = parser.parse_StringFormalParameters("(public float f, int i, private ASTNode n, Float ... a)");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTFormalParameters ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringFormalParameters(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testFormalParameterListing() throws IOException {
    Optional<ASTFormalParameterListing> result = parser.parse_StringFormalParameterListing("public float f, int i, private ASTNode n, Float ... a");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTFormalParameterListing ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringFormalParameterListing(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testFormalParameter() throws IOException {
    Optional<ASTFormalParameter> result = parser.parse_StringFormalParameter("public float f");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTFormalParameter ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringFormalParameter(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testLastFormalParameter() throws IOException {
    Optional<ASTLastFormalParameter> result = parser.parse_StringLastFormalParameter("private String ... a");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTLastFormalParameter ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringLastFormalParameter(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testAnnotation() throws IOException {
    Optional<ASTAnnotation> result = parser.parse_StringAnnotation("@java.util.List (a = ++a, b = {c, d})");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTAnnotation ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringAnnotation(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testAnnotationPairArguments() throws IOException {
    Optional<ASTAnnotationPairArguments> result = parser.parse_StringAnnotationPairArguments("a = ++a, b = {c, d}");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTAnnotationPairArguments ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringAnnotationPairArguments(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testAnnotationTypeDeclaration() throws IOException {
    Optional<ASTAnnotationTypeDeclaration> result = parser.parse_StringAnnotationTypeDeclaration("private static @ interface IScope {private double a();}");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTAnnotationTypeDeclaration ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringAnnotationTypeDeclaration(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testElementValueOrExpr() throws IOException {
    Optional<ASTElementValueOrExpr> result = parser.parse_StringElementValueOrExpr("++a");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTElementValueOrExpr ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringElementValueOrExpr(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testElementValueArrayInitializer() throws IOException {
    Optional<ASTElementValueArrayInitializer> result = parser.parse_StringElementValueArrayInitializer("{c, d}");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTElementValueArrayInitializer ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringElementValueArrayInitializer(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testElementValuePair() throws IOException {
    Optional<ASTElementValuePair> result = parser.parse_StringElementValuePair("a = ++a");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTElementValuePair ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringElementValuePair(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testAnnotationTypeBody() throws IOException {
    Optional<ASTAnnotationTypeBody> result = parser.parse_StringAnnotationTypeBody("{private double a();}");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTAnnotationTypeBody ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringAnnotationTypeBody(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testDefaultValue() throws IOException {
    Optional<ASTDefaultValue> result = parser.parse_StringDefaultValue("default a");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTDefaultValue ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringDefaultValue(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testAnnotationMethod() throws IOException {
    Optional<ASTAnnotationMethod> result = parser.parse_StringAnnotationMethod("private double a();");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTAnnotationMethod ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringAnnotationMethod(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testAnnotationConstant() throws IOException {
    Optional<ASTAnnotationConstant> result = parser.parse_StringAnnotationConstant("private ASTNode a = foo, e = d;");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTAnnotationConstant ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringAnnotationConstant(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testCreatorExpression() throws IOException {
    Optional<ASTCreatorExpression> result = parser.parse_StringCreatorExpression("new Integer(a,b)");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCreatorExpression ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringCreatorExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testArrayCreator() throws IOException {
    Optional<ASTArrayCreator> result = parser.parse_StringArrayCreator("char [a][b]");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTArrayCreator ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringArrayCreator(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testArrayDimensionByInitializer() throws IOException {
    Optional<ASTArrayDimensionByInitializer> result = parser.parse_StringArrayDimensionByInitializer("[][][] {a, b, c, d}");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTArrayDimensionByInitializer ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringArrayDimensionByInitializer(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testArrayDimensionByExpression() throws IOException {
    Optional<ASTArrayDimensionByExpression> result = parser.parse_StringArrayDimensionByExpression("[a][d] [][][]");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTArrayDimensionByExpression ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringArrayDimensionByExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testCreatedName() throws IOException {
    Optional<ASTCreatedName> result = parser.parse_StringCreatedName("boolean");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCreatedName ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringCreatedName(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testEnhancedForControl() throws IOException {
    Optional<ASTEnhancedForControl> result = parser.parse_StringEnhancedForControl("protected int c[] : a");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTEnhancedForControl ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringEnhancedForControl(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }
}

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.types.typesymbols._symboltable.TypeSymbolLoader;

public class SymTypeVariable extends SymTypeExpression {

  /**
   * Constructor:
   */
  public SymTypeVariable(TypeSymbolLoader typeSymbolLoader) {
    this.typeSymbolLoader = typeSymbolLoader;
  }

  public String getVarName() {
    return typeSymbolLoader.getName();
  }

  public void setVarName(String name) {
    typeSymbolLoader.setName(name);
  }

  /**
   * print: Umwandlung in einen kompakten String
   */
  @Override
  public String print() {
    return getVarName();
  }

  /**
   * printAsJson: Umwandlung in einen kompakten Json String
   */
  protected String printAsJson() {
    JsonPrinter jp = new JsonPrinter();
    jp.beginObject();
    // Care: the following String needs to be adapted if the package was renamed
    jp.member(JsonDeSers.KIND, "de.monticore.types.check.SymTypeVariable");
    jp.member("varName", getVarName());
    jp.endObject();
    return jp.getContent();
  }

  /**
   * Am I primitive? (such as "int")
   */
  public boolean isPrimitive() {
    return false;
    /**
     *     Please note that the var itself is not a primitive type, but it might
     *     be instantiated into a primitive type
     *     unless we always assume boxed implementations then return false would be correct
     *     according to the W algorithm of Hindley-Milner, we regard a variable
     *     a monomorphic type on its own and do hence not regard it as primitive type
     */
  }

  public boolean isTypeVariable() {
    return true;
  }

  @Override
  public SymTypeVariable deepClone() {
    return new SymTypeVariable(new TypeSymbolLoader(typeSymbolLoader.getName(), typeSymbolLoader.getEnclosingScope()));
  }

  // --------------------------------------------------------------------------
}

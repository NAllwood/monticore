/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symboltable.serialization.IDeSer;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonUtil;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.types.typesymbols._symboltable.ITypeSymbolsScope;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.logging.Log;

import java.nio.file.Paths;

/**
 * This DeSer reailizes serialization and deserialization of SymTypeExpressions.
 */
public class SymTypeExpressionDeSer implements IDeSer<SymTypeExpression, ITypeSymbolsScope> {

  /**
   * The singleton that DeSerializes all SymTypeExpressions.
   * It is stateless and can be reused recursively.
   */
  protected static SymTypeExpressionDeSer instance;
  // not realized as static delegator, but only as singleton

  protected SymTypeArrayDeSer symTypeArrayDeSer;

  protected SymTypeConstantDeSer symTypeConstantDeSer;

  protected SymTypeOfGenericsDeSer symTypeOfGenericsDeSer;

  protected SymTypeOfObjectDeSer symTypeOfObjectDeSer;

  protected SymTypeVariableDeSer symTypeVariableDeSer;

  protected SymTypeExpressionDeSer() {
    //this is a singleton, do not use constructor
    this.symTypeArrayDeSer = new SymTypeArrayDeSer();
    this.symTypeConstantDeSer = new SymTypeConstantDeSer();
    this.symTypeOfGenericsDeSer = new SymTypeOfGenericsDeSer();
    this.symTypeOfObjectDeSer = new SymTypeOfObjectDeSer();
    this.symTypeVariableDeSer = new SymTypeVariableDeSer();
  }

  public static SymTypeExpressionDeSer getInstance() {
    if (null == instance) {
      instance = new SymTypeExpressionDeSer();
    }
    return instance;
  }

  /**
   * This method can be used to set the instance of the SymTypeExpressionDeSer to a custom suptype
   *
   * @param theInstance
   */
  public static void setInstance(SymTypeExpressionDeSer theInstance) {
    if (null == theInstance) {  //in this case, "reset" to default type
      instance = new SymTypeExpressionDeSer();
    }
    else {
      instance = theInstance;
    }
  }

  public  void store(SymTypeExpression expr, String symbolPath)  {
    TypeSymbol symbol = expr.getTypeInfo();
    store(expr, Paths.get(symbolPath, symbol.getPackageName(), symbol.getName()+".symtype"));
  }

  /**
   * @see de.monticore.symboltable.serialization.IDeSer#getSerializedKind()
   */
  @Override
  public String getSerializedKind() {
    // Care: this String is never to occur, because all subclasses override this function
    return "de.monticore.types.check.SymTypeExpression";
  }

  /**
   * @see de.monticore.symboltable.serialization.IDeSer#serialize(java.lang.Object)
   */
  @Override
  public String serialize(SymTypeExpression toSerialize) {
    return toSerialize.printAsJson();
  }

  /**
   *
   * @param serialized
   * @return
   */
  @Override
  public SymTypeExpression deserialize(String serialized, ITypeSymbolsScope enclosingScope) {
    return deserialize(JsonParser.parse(serialized), enclosingScope);
  }

  /**
   *
   * @param serialized
   * @return
   */
  public SymTypeExpression deserialize(JsonElement serialized, ITypeSymbolsScope enclosingScope) {

    // void and null are stored as strings
    if (serialized.isJsonString()) {
      String value = serialized.getAsJsonString().getValue();

      if (value.equals(DefsTypeBasic._nullTypeString)) {
        return SymTypeExpressionFactory.createTypeOfNull();
      }
      else if (value.equals(DefsTypeBasic._voidTypeString)) {
        return SymTypeExpressionFactory.createTypeVoid();
      }
      else {
        Log.error(
            "0x823F3 Internal error: Loading ill-structured SymTab: Unknown serialization of SymTypeExpression: "
                + serialized);
        return null;
      }
    }

    // all other serialized SymTypeExrpressions are json objects with a kind
    if (JsonUtil.isCorrectDeSerForKind(symTypeArrayDeSer, serialized)) {
      return symTypeArrayDeSer.deserialize(serialized, enclosingScope);
    }
    else if (JsonUtil.isCorrectDeSerForKind(symTypeConstantDeSer, serialized)) {
      return symTypeConstantDeSer.deserialize(serialized, enclosingScope);
    }
    else if (JsonUtil.isCorrectDeSerForKind(symTypeOfGenericsDeSer, serialized)) {
      return symTypeOfGenericsDeSer.deserialize(serialized, enclosingScope);
    }
    else if (JsonUtil.isCorrectDeSerForKind(symTypeOfObjectDeSer, serialized)) {
      return symTypeOfObjectDeSer.deserialize(serialized, enclosingScope);
    }
    else if (JsonUtil.isCorrectDeSerForKind(symTypeVariableDeSer, serialized)) {
      return symTypeVariableDeSer.deserialize(serialized, enclosingScope);
    }
    else {
      Log.error(
          "0x823FE Internal error: Loading ill-structured SymTab: Unknown serialization of SymTypeExpression: "
              + serialized);
      return null;
    }
  }

}

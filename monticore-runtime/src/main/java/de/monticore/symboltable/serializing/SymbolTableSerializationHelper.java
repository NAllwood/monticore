/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.symboltable.serializing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.ScopeSpanningSymbol;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;

public class SymbolTableSerializationHelper {
  
  /**
   * Returns all Symbols that are directly contained within a given scope and returns these as a
   * Collection
   * 
   * @param scope
   * @return
   */
  public static Collection<Symbol> getLocalSymbols(Scope scope) {
    Collection<Symbol> symbols = new ArrayList<>();
    for (Collection<Symbol> s : scope.getLocalSymbols().values()) {
      symbols.addAll(s);
    }
    return symbols;
  }
  
  /**
   * Given a scope, returns a collection of direct subscopes that export symbols and that contain at
   * least one symbol TODO AB: in any transitive subscope
   * 
   * @param src
   * @return
   */
  public static Collection<Scope> filterRelevantSubScopes(MutableScope src) {
     return src.getSubScopes()
     .stream()
     .filter(s -> s.exportsSymbols())
     .filter(s -> SymbolTableSerializationHelper.getLocalSymbols(s).size() > 0)
     .collect(Collectors.toList());
  }
  
  /**
   * Deserializes a list of ImportStatements. Is the passed JsonElement is null, returns an empty
   * list
   * 
   * @param i
   * @return
   */
  public static List<ImportStatement> deserializeImports(JsonElement i) {
    List<ImportStatement> imports = new ArrayList<>();
    if (null != i) {
      JsonArray list = i.getAsJsonArray();
      for (JsonElement e : list) {
        String importStatement = e.getAsString();
        imports.add(new ImportStatement(importStatement, importStatement.endsWith("*")));
      }
    }
    return imports;
  }
  
  public static String deserializePackage(JsonObject json) {
    if (json.has(ISerialization.PACKAGE)) {
      return json.get(ISerialization.PACKAGE).getAsString();
    }
    return "";
  }
  
  public static void deserializeName(JsonObject json, MutableScope result) {
    if (json.has(ISerialization.NAME)) {
      String name = json.get(ISerialization.NAME).getAsString();
      result.setName(name);
    }
  }
  
  public static String getClassName(JsonObject json) {
    if (json.has(ISerialization.CLASS)) {
      String name = json.get(ISerialization.CLASS).getAsString();
      return name;
    }
    else {
      Log.error("0x" + "Class of scope or symbol could not be deserialized!");
      return "";
    }
  }
  
  public static boolean getIsShadowingScopeFlag(JsonObject json) {
    if (json.has(ISerialization.IS_SHADOWING_SCOPE)) {
      return json.get(ISerialization.IS_SHADOWING_SCOPE).getAsBoolean();
    }
    else {
      return true;
    }
  }
  
  public static void deserializeSymbols(JsonObject json, JsonDeserializationContext context,
      MutableScope result) {
    if (json.has(ISerialization.SYMBOLS)) {
      for (JsonElement e : json.get(ISerialization.SYMBOLS).getAsJsonArray()) {
        Symbol sym = context.deserialize(e, Symbol.class);
        result.add(sym);
      }
    }
  }
  
  public static void deserializeSubscopes(JsonObject json, JsonDeserializationContext context,
      MutableScope result) {
    if (json.has(ISerialization.SUBSCOPES)) {
      for (JsonElement e : json.get(ISerialization.SUBSCOPES).getAsJsonArray()) {
        MutableScope subScope = context.deserialize(e, MutableScope.class);
        // TODO: Remove if ScopeSpanningSymbols are removed
        deserializeSpanningSymbol(result, subScope, e);
        result.addSubScope(subScope);
      }
    }
  }
  
  /**
   * TODO: This method can be removed, if ScopeSpanningSymbols are removed
   * 
   * @param src
   * @param json
   */
  public static void serializeSpanningSymbol(MutableScope src, JsonObject json) {
    if (src.isSpannedBySymbol()) {
      ScopeSpanningSymbol spanningSymbol = src.getSpanningSymbol().get();
      JsonObject jsonSpanningSymbol = new JsonObject();
      jsonSpanningSymbol.addProperty(ISerialization.CLASS, spanningSymbol.getKind().getName());
      jsonSpanningSymbol.addProperty(ISerialization.NAME, spanningSymbol.getName());
      json.add(ISerialization.SCOPESPANNING_SYMBOL, jsonSpanningSymbol);
    }
  }
  
  /**
   * TODO: This method can be removed, if ScopeSpanningSymbols are removed
   * 
   * @param result
   * @param subScope
   * @param e
   */
  public static void deserializeSpanningSymbol(MutableScope result, MutableScope subScope,
      JsonElement e) {
    JsonObject jsonSubScope = e.getAsJsonObject();
    if (jsonSubScope.has(ISerialization.SCOPESPANNING_SYMBOL)) {
      JsonObject asJsonObject = jsonSubScope.get(ISerialization.SCOPESPANNING_SYMBOL)
          .getAsJsonObject();
      String spanningSymbolName = asJsonObject.get(ISerialization.NAME).getAsString();
      String spanningSymbolKind = asJsonObject.get(ISerialization.CLASS).getAsString();
      
      Collection<Symbol> allLocalSymbols = result.getLocalSymbols().get(spanningSymbolName);
      List<Symbol> symbolsOfCorrectKind = allLocalSymbols.stream()
          .filter(x -> x.getKind().getName().equals(spanningSymbolKind))
          .collect(Collectors.toList());
      if (symbolsOfCorrectKind.size() == 1) {
        ScopeSpanningSymbol symbol = (ScopeSpanningSymbol) symbolsOfCorrectKind.get(0);
        subScope.setSpanningSymbol(symbol);
        Optional<? extends Scope> enclosingScope = symbol.getSpannedScope().getEnclosingScope();
        if (enclosingScope.isPresent()) {
          enclosingScope.get().getAsMutableScope()
              .removeSubScope((MutableScope) symbol.getSpannedScope());
        }
      }
      else {
        Log.error("Spanning Symbol '" + spanningSymbolName + "' of kind '" + spanningSymbolName
            + "' not resolved!");
      }
    }
  }
  
}

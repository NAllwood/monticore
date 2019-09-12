/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.types2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.monticore.symboltable.serialization.IDeSer;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonUtil;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class SymTypeOfGenericsDeSer implements IDeSer<SymTypeOfGenerics> {
  
  /**
   * @see de.monticore.symboltable.serialization.IDeSer#getSerializedKind()
   */
  @Override
  public String getSerializedKind() {
    // TODO: anpassen, nachdem package umbenannt ist
    return "de.monticore.types2.SymTypeOfGenerics";
  }
  
  /**
   * @see de.monticore.symboltable.serialization.IDeSer#serialize(java.lang.Object)
   */
  @Override
  public String serialize(SymTypeOfGenerics toSerialize) {
    return toSerialize.printAsJson();
  }
  
  /**
   * @see de.monticore.symboltable.serialization.IDeSer#deserialize(java.lang.String)
   */
  @Override
  public Optional<SymTypeOfGenerics> deserialize(String serialized) {
    JsonElement json = JsonParser.parseJson(serialized);
    if (JsonUtil.isCorrectDeSerForKind(this, json)) {
      Optional<String> typeConstructorFullName = JsonUtil.getOptStringMember(json,
          "typeConstructorFullName");
      if (!typeConstructorFullName.isPresent()) {
        Log.error("Could not find typeConstructorFullName of SymTypeOfGenerics " + serialized);
      }
      
      List<SymTypeExpression> arguments = new ArrayList<>();
      // delegate deserialization of individual arguments to the SymTypeExpressionDeSer
      SymTypeExpressionDeSer symTypeExpressionDeSer = new SymTypeExpressionDeSer();
      if (json.getAsJsonObject().containsKey("arguments")
          && json.getAsJsonObject().get("arguments").isJsonArray()) {
        for (JsonElement e : json.getAsJsonObject().get("arguments").getAsJsonArray().getValues()) {
          Optional<SymTypeExpression> arg = symTypeExpressionDeSer.deserialize(e.toString());
          if (arg.isPresent()) {
            arguments.add(arg.get());
          }
        }
      }
      
      // TODO: Deserialize TypeSymbol if it is present
      TypeSymbol symbol = null;
      return Optional.of(new SymTypeOfGenerics(typeConstructorFullName.get(), arguments, symbol));
    }
    return Optional.empty();
  }
  
}
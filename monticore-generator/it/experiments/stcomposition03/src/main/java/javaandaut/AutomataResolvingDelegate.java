/* (c) https://github.com/MontiCore/monticore */

package javaandaut;

import automata6._symboltable.*;
import basicjava.BasicJavaMill;
import basicjava._symboltable.BasicJavaGlobalScope;
import basicjava._symboltable.BasicJavaLanguage;
import basicjava._symboltable.ClassDeclarationSymbol;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;


public class AutomataResolvingDelegate implements IStimulusSymbolResolvingDelegate {

  BasicJavaGlobalScope javaGS;

  public AutomataResolvingDelegate(ModelPath mp){
    javaGS =   BasicJavaMill
        .basicJavaGlobalScopeBuilder()
        .setModelPath(mp) //hand over modelpath
        .setBasicJavaLanguage(new BasicJavaLanguage()) //will be removed soon
        .build();
  }


  @Override public List<StimulusSymbol> resolveAdaptedStimulusSymbol(boolean foundSymbols,
      String name, AccessModifier modifier, Predicate<StimulusSymbol> predicate) {
    List<StimulusSymbol> result = new ArrayList<>();
    Optional<ClassDeclarationSymbol> classDeclSymbol = javaGS.resolveClassDeclaration(name, modifier);
    if(classDeclSymbol.isPresent()){
      result.add(new Class2StimulusAdapter(classDeclSymbol.get()));
    }
    return result;
  }
}
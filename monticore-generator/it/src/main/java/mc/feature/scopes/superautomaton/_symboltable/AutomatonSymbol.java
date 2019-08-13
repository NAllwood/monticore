/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package mc.feature.scopes.superautomaton._symboltable;

import de.monticore.symboltable.ISymbol;

import java.util.Collection;
import java.util.Optional;

public class AutomatonSymbol extends AutomatonSymbolTOP {

  public AutomatonSymbol(final String name) {
    super(name);
  }

  public Optional<StateSymbol> getState(final String name) {
    return getSpannedScope().resolveStateLocally(name);
  }

  public Collection<StateSymbol> getStates() {
    return ISymbol.sortSymbolsByPosition(getSpannedScope().getStateSymbols().values());
  }
}

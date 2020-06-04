/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.SynthesizeSymTypeFromMCBasicTypes;
import de.monticore.types.typesymbols._symboltable.TypeSymbolLoader;
import mc.typescalculator.unittypes._ast.ASTMinuteType;
import mc.typescalculator.unittypes._visitor.UnitTypesVisitor;

public class SynthesizeSymTypeFromUnitTypes extends SynthesizeSymTypeFromMCBasicTypes implements UnitTypesVisitor, ISynthesize {

  private UnitTypesVisitor realThis;

  public SynthesizeSymTypeFromUnitTypes() {
    super();
  }

  @Override
  public UnitTypesVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(UnitTypesVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public void endVisit(ASTMinuteType type){
    typeCheckResult.setLast(new SymTypeOfSIUnit(new TypeSymbolLoader(type.getUnit(),getScope(type.getEnclosingScope()))));
  }
}

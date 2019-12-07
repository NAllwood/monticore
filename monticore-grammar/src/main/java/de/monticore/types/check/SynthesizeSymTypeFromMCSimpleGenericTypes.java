// (c) https://github.com/MontiCore/monticore

package de.monticore.types.check;

import de.monticore.mcbasics._ast.MCBasicsMill;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;
import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesVisitor;
import de.monticore.types.typesymbols._symboltable.TypeSymbolLoader;
import de.se_rwth.commons.logging.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static de.monticore.types.check.SymTypeExpressionFactory.createTypeObject;

/**
 * Visitor for Derivation of SymType from MCSimpleGenericTypes
 * i.e. for
 * types/MCSimpleGenericTypes.mc4
 */
public class SynthesizeSymTypeFromMCSimpleGenericTypes extends SynthesizeSymTypeFromMCCollectionTypes
    implements MCSimpleGenericTypesVisitor {

  public SynthesizeSymTypeFromMCSimpleGenericTypes() {
  }

  /**
   * Using the visitor functionality to calculate the SymType Expression
   */

  // ----------------------------------------------------------  realThis start
  // setRealThis, getRealThis are necessary to make the visitor compositional
  //
  // (the Vistors are then composed using theRealThis Pattern)
  //
  MCSimpleGenericTypesVisitor realThis = this;

  @Override
  public void setRealThis(MCSimpleGenericTypesVisitor realThis) {
    this.realThis = realThis;
    super.realThis = realThis;  // not necessarily needed, but to be safe ...
  }

  @Override
  public MCSimpleGenericTypesVisitor getRealThis() {
    return realThis;
  }
  // ---------------------------------------------------------- realThis end

  /**
   * Storage in the Visitor: result of the last endVisit
   * is inherited
   * This attribute is synthesized upward.
   */

  /**
   * We use mainly endVisit, because the result is synthesized along the
   * tree, when walking upwards
   */

  public void traverse(ASTMCBasicGenericType genericType) {

    List<SymTypeExpression> arguments = new LinkedList<SymTypeExpression>();
    for (ASTMCTypeArgument arg : genericType.getMCTypeArgumentList()) {
      if (null != arg) {
        arg.accept(getRealThis());
      }

      if (!result.isPresent()) {
        Log.error("0xE9CDA Internal Error: SymType argument missing for generic type. "
            + " Probably TypeCheck mis-configured.");
      }
      arguments.add(result.get());
    }

    SymTypeExpression tex = SymTypeExpressionFactory.createGenerics(
        new TypeSymbolLoader(genericType.printWithoutTypeArguments(), genericType.getEnclosingScope()), arguments);
    result = Optional.of(tex);

  }

  /**
   * There are several forms of qualified Types possible:
   * ** Object-types
   * ** Boxed primitives, such as "java.lang.Boolean"
   * ** Type Variables
   * Primitives, like "int", void, null are not possible here.
   * This are the qualified Types that may occur.
   * <p>
   * To distinguish these kinds, we use the symbol that the ASTMCQualifiedType identifies
   *
   * @param qType
   */
  @Override
  public void endVisit(ASTMCQualifiedType qType) {

    // TODO TODO ! This implementation is incomplete, it does only create Object-Types, but the
    // type could also be a boxed Primitive or an Type Variable!
    // We need the SymbolTable to distinguish this stuff
    // PS: that also applies to other Visitors.
    result = Optional.of(SymTypeExpressionFactory.createTypeObject(new TypeSymbolLoader(qType.printType(MCBasicTypesMill.mcBasicTypesPrettyPrinter()), qType.getEnclosingScope())));
  }

  @Override
  public void endVisit(ASTMCQualifiedName qName) {
    SymTypeOfObject oType = createTypeObject(new TypeSymbolLoader(qName.getQName(), qName.getEnclosingScope()));
    result = Optional.of(oType);
  }

}

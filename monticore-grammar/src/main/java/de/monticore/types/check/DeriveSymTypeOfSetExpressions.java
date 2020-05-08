/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.setexpressions._ast.ASTIntersectionExpressionInfix;
import de.monticore.expressions.setexpressions._ast.ASTIsInExpression;
import de.monticore.expressions.setexpressions._ast.ASTSetInExpression;
import de.monticore.expressions.setexpressions._ast.ASTUnionExpressionInfix;
import de.monticore.expressions.setexpressions._visitor.SetExpressionsVisitor;
import de.monticore.types.typesymbols._symboltable.TypeSymbolLoader;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;

import static de.monticore.types.check.SymTypeConstant.unbox;
import static de.monticore.types.check.TypeCheck.isSubtypeOf;

/**
 * This Visitor can calculate a SymTypeExpression (type) for the expressions in SetExpressions
 * It can be combined with other expressions in your language by creating a DelegatorVisitor
 */
public class DeriveSymTypeOfSetExpressions extends DeriveSymTypeOfExpression implements SetExpressionsVisitor {

  private SetExpressionsVisitor realThis;

  public DeriveSymTypeOfSetExpressions(){
    this.realThis = this;
  }

  @Override
  public void setRealThis(SetExpressionsVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public SetExpressionsVisitor getRealThis(){
    return realThis;
  }

  @Override
  public void traverse(ASTIsInExpression node) {
    //e isin E checks whether e is an Element in Set E and can only be calculated if e is a subtype or the same type as the set type

    SymTypeExpression elemResult = null;
    SymTypeExpression setResult = null;
    SymTypeExpression wholeResult = null;

    //element
    node.getElem().accept(realThis);
    if(lastResult.isPresentLast()){
      elemResult = lastResult.getLast();
    }else{
      Log.error("0xA0286"+String.format(ERROR_MSG,prettyPrinter.prettyprint(node.getElem())));
    }
    //set
    node.getSet().accept(realThis);
    if(lastResult.isPresentLast()){
      setResult = lastResult.getLast();
    }else{
      Log.error("0xA0287"+String.format(ERROR_MSG,prettyPrinter.prettyprint(node.getSet())));
    }
    List<String> collections = Lists.newArrayList("List","Set");
    boolean correct = false;
    for(String s: collections) {
      if (setResult.isGenericType() && setResult.getTypeInfo().getName().equals(s)) {
        correct = true;
      }
    }
    if(correct){
      SymTypeOfGenerics genericResult = (SymTypeOfGenerics) setResult;
      if(unbox(elemResult.print()).equals(unbox(genericResult.getArgument(0).print()))||isSubtypeOf(elemResult,genericResult.getArgument(0))){
        wholeResult = SymTypeExpressionFactory.createTypeConstant("boolean");
      }
    }

    if(null!=wholeResult){
      lastResult.setLast(wholeResult);
      result = wholeResult;
    }else{
      lastResult.reset();
      Log.error("0xA0288"+String.format(ERROR_MSG,prettyPrinter.prettyprint(node)));
    }
  }

  @Override
  public void traverse(ASTSetInExpression node) {
    //e in E checks whether e is an Element in Set E and can only be calculated if e is a subtype or the same type as the set type

    SymTypeExpression elemResult = null;
    SymTypeExpression setResult = null;
    SymTypeExpression wholeResult = null;

    //element
    node.getElem().accept(realThis);
    if(lastResult.isPresentLast()){
      elemResult = lastResult.getLast();
    }else{
      Log.error("0xA0289"+String.format(ERROR_MSG,prettyPrinter.prettyprint(node.getElem())));
    }
    //set
    node.getSet().accept(realThis);
    if(lastResult.isPresentLast()){
      setResult = lastResult.getLast();
    }else{
      Log.error("0xA0290"+String.format(ERROR_MSG,prettyPrinter.prettyprint(node.getSet())));
    }
    List<String> collections = Lists.newArrayList("List","Set");
    boolean correct = false;
    for(String s: collections) {
      if (setResult.isGenericType() && setResult.getTypeInfo().getName().equals(s)) {
        correct = true;
      }
    }
    if(correct){
      SymTypeOfGenerics genericResult = (SymTypeOfGenerics) setResult;
      if(unbox(elemResult.print()).equals(unbox(genericResult.getArgument(0).print()))||isSubtypeOf(elemResult,genericResult.getArgument(0))){
        wholeResult = genericResult.getArgument(0).deepClone();
      }
    }

    if(null!=wholeResult){
      lastResult.setLast(wholeResult);
      result = wholeResult;
    }else{
      lastResult.reset();
      Log.error("0xA0291"+String.format(ERROR_MSG,prettyPrinter.prettyprint(node)));
    }
  }

  @Override
  public void traverse(ASTUnionExpressionInfix node) {
    //union of two sets -> both sets need to have the same type or their types need to be sub/super types
    Optional<SymTypeExpression> wholeResult = calculateUnionAndIntersectionInfix(node.getLeft(),node.getRight());

    if(wholeResult.isPresent()){
      lastResult.setLast(wholeResult.get());
      result = wholeResult.get();
    }else{
      lastResult.reset();
      Log.error("0xA0292"+String.format(ERROR_MSG,prettyPrinter.prettyprint(node)));
    }
  }

  @Override
  public void traverse(ASTIntersectionExpressionInfix node) {
    //intersection of two sets -> both sets need to have the same type or their types need to be sub/super types
    Optional<SymTypeExpression> wholeResult = calculateUnionAndIntersectionInfix(node.getLeft(),node.getRight());

    if(wholeResult.isPresent()){
      lastResult.setLast(wholeResult.get());
      result = wholeResult.get();
    }else{
      lastResult.reset();
      Log.error("0xA0293"+String.format(ERROR_MSG,prettyPrinter.prettyprint(node)));
    }
  }

  public Optional<SymTypeExpression> calculateUnionAndIntersectionInfix(ASTExpression leftExpr, ASTExpression rightExpr){
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;
    Optional<SymTypeExpression> wholeResult = Optional.empty();

    //element
    leftExpr.accept(realThis);
    if(lastResult.isPresentLast()){
      leftResult = lastResult.getLast();
    }else{
      Log.error("0xA0294"+String.format(ERROR_MSG,prettyPrinter.prettyprint(leftExpr)));
    }
    //set
    rightExpr.accept(realThis);
    if(lastResult.isPresentLast()){
      rightResult = lastResult.getLast();
    }else{
      Log.error("0xA0295"+String.format(ERROR_MSG,prettyPrinter.prettyprint(rightExpr)));
    }
    List<String> collections = Lists.newArrayList("List","Set");
    if(rightResult.isGenericType()&&leftResult.isGenericType()){
      SymTypeOfGenerics leftGeneric = (SymTypeOfGenerics) leftResult;
      SymTypeOfGenerics rightGeneric = (SymTypeOfGenerics) rightResult;
      String left = leftGeneric.getTypeInfo().getName();
      String right = rightGeneric.getTypeInfo().getName();
      if(collections.contains(left) && unbox(left).equals(unbox(right))) {
        if(unbox(leftGeneric.getArgument(0).print()).equals(unbox(rightGeneric.getArgument(0).print()))) {
          wholeResult = Optional.of(SymTypeExpressionFactory.createGenerics(new TypeSymbolLoader(left,scope),leftGeneric.getArgument(0).deepClone()));
        }else if(isSubtypeOf(leftGeneric.getArgument(0),rightGeneric.getArgument(0))){
          wholeResult = Optional.of(SymTypeExpressionFactory.createGenerics(new TypeSymbolLoader(right,scope),rightGeneric.getArgument(0).deepClone()));
        }else if(isSubtypeOf(rightGeneric.getArgument(0),leftGeneric.getArgument(0))){
          wholeResult = Optional.of(SymTypeExpressionFactory.createGenerics(new TypeSymbolLoader(left,scope),leftGeneric.getArgument(0).deepClone()));
        }
      }
    }
    return wholeResult;
  }
}

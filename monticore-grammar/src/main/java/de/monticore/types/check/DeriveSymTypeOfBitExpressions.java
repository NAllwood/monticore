/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.bitexpressions._ast.*;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

import static de.monticore.types.check.TypeCheck.*;

/**
 * This Visitor can calculate a SymTypeExpression (type) for the expressions in BitExpressions
 * It can be combined with other expressions in your language by creating a DelegatorVisitor
 */
public class DeriveSymTypeOfBitExpressions extends DeriveSymTypeOfExpression implements BitExpressionsVisitor {

  private BitExpressionsVisitor realThis;

  @Override
  public void setRealThis(BitExpressionsVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public BitExpressionsVisitor getRealThis(){
    return realThis;
  }

  public DeriveSymTypeOfBitExpressions(){
    realThis = this;
  }

  @Override
  public void traverse(ASTLeftShiftExpression expr){
    Optional<SymTypeExpression> wholeResult = calculateTypeShift(expr.getLeft(),expr.getRight());
    if(wholeResult.isPresent()){
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    }else{
      typeCheckResult.reset();
      logError("0xA0200", expr.get_SourcePositionStart());
    }
  }

  @Override
  public void traverse(ASTRightShiftExpression expr){
    Optional<SymTypeExpression> wholeResult = calculateTypeShift(expr.getLeft(),expr.getRight());
    if(wholeResult.isPresent()){
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    }else{
      typeCheckResult.reset();
      logError("0xA0201", expr.get_SourcePositionStart());
    }
  }

  @Override
  public void traverse(ASTLogicalRightShiftExpression expr){
    Optional<SymTypeExpression> wholeResult = calculateTypeShift(expr.getLeft(),expr.getRight());
    if(wholeResult.isPresent()){
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    }else{
      typeCheckResult.reset();
      logError("0xA0202", expr.get_SourcePositionStart());
    }
  }

  @Override
  public void traverse(ASTBinaryAndExpression expr){
    Optional<SymTypeExpression> wholeResult = calculateTypeBinary(expr.getLeft(),expr.getRight());
    if(wholeResult.isPresent()){
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    }else{
      typeCheckResult.reset();
      logError("0xA0203", expr.get_SourcePositionStart());
    }
  }

  @Override
  public void traverse(ASTBinaryOrOpExpression expr){
    Optional<SymTypeExpression> wholeResult = calculateTypeBinary(expr.getLeft(),expr.getRight());
    if(wholeResult.isPresent()){
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    }else{
      typeCheckResult.reset();
      logError("0xA0204", expr.get_SourcePositionStart());
    }
  }

  @Override
  public void traverse(ASTBinaryXorExpression expr){
    Optional<SymTypeExpression> wholeResult = calculateTypeBinary(expr.getLeft(),expr.getRight());
    if(wholeResult.isPresent()){
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    }else{
      typeCheckResult.reset();
      logError("0xA0205", expr.get_SourcePositionStart());
    }
  }

  /**
   * helper method for the calculation of the type of the ShiftExpressions
   */
  private Optional<SymTypeExpression> calculateTypeShift(ASTExpression left, ASTExpression right){
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;

    left.accept(getRealThis());
    if(typeCheckResult.isPresentLast()){
      //store the type of the left expression in a variable for later use
      leftResult = typeCheckResult.getLast();
    }else{
      logError("0xA0206", left.get_SourcePositionStart());
    }

    right.accept(getRealThis());
    if(typeCheckResult.isPresentLast()){
      //store the type of the right expression in a variable for later use
      rightResult = typeCheckResult.getLast();
    }else{
      logError("0xA0207", right.get_SourcePositionStart());
    }
    
    if(leftResult.isPrimitive()&&rightResult.isPrimitive()){
      SymTypeConstant leftEx = (SymTypeConstant) leftResult;
      SymTypeConstant rightEx = (SymTypeConstant) rightResult;

      //only defined on integral type - integral type
      if(leftEx.isIntegralType()&&rightEx.isIntegralType()){
        return shiftCalculator(leftResult,rightResult);
      }
    }
    //should not happen, will be handled in traverse
    return Optional.empty();
  }

  /**
   * helper method for the calculation of the type of the BinaryExpressions
   */
  private Optional<SymTypeExpression> calculateTypeBinary(ASTExpression left, ASTExpression right){
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;

    left.accept(getRealThis());
    if(typeCheckResult.isPresentLast()){
      //store the type of the left expression in a variable for later use
      leftResult = typeCheckResult.getLast();
    }else{
      logError("0xA0208", left.get_SourcePositionStart());

    }

    right.accept(getRealThis());
    if(typeCheckResult.isPresentLast()){
      //store the type of the right expression in a variable for later use
      rightResult = typeCheckResult.getLast();
    }else{
      logError("0xA0209", right.get_SourcePositionStart());
    }
    
    if(leftResult.isPrimitive()&&rightResult.isPrimitive()) {
      SymTypeConstant leftEx = (SymTypeConstant) leftResult;
      SymTypeConstant rightEx = (SymTypeConstant) rightResult;

      //only defined on boolean - boolean and integral type - integral type
      if (isBoolean(leftResult) && isBoolean(rightResult)) {
        return Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
      }else if (leftEx.isIntegralType()&&rightEx.isIntegralType()) {
        return getBinaryNumericPromotion(leftResult,rightResult);
      }
    }
    //should not happen, no valid result, error will be handled in traverse
    return Optional.empty();
  }

  /**
   * helper method to calculate the type of the ShiftExpressions
   * cannot be linked with the BinaryExpressions because they are not calculated the same way
   */
  private Optional<SymTypeExpression> shiftCalculator(SymTypeExpression left, SymTypeExpression right){
    if(left.isPrimitive() && right.isPrimitive()) {
      SymTypeConstant leftResult = (SymTypeConstant) left;
      SymTypeConstant rightResult = (SymTypeConstant) right;

      //only defined on integral type - integral type
      if(leftResult.isIntegralType()&&rightResult.isIntegralType()){
        if(isLong(rightResult) && isLong(leftResult)){
          return Optional.of(SymTypeExpressionFactory.createTypeConstant("long"));
        }else{
          return Optional.of(SymTypeExpressionFactory.createTypeConstant("int"));
        }
      }
    }
    //should never happen, no valid result, error will be handled in traverse
    return Optional.empty();
  }

  /**
   * helper method to calculate the type of the BinaryExpressions
   */
  private Optional<SymTypeExpression> getBinaryNumericPromotion(SymTypeExpression left, SymTypeExpression right){
    //only integral type - integral type
    if(left.isPrimitive() && right.isPrimitive()) {
      SymTypeConstant leftResult = (SymTypeConstant) left;
      SymTypeConstant rightResult = (SymTypeConstant) right;
      if ((isLong(leftResult) && rightResult.isIntegralType()) ||
          (isLong(rightResult) && leftResult.isIntegralType())) {
        return Optional.of(SymTypeExpressionFactory.createTypeConstant("long"));
        //no part of the expression is a long -> if both parts are integral types then the result is a int
      }else{
        if (leftResult.isIntegralType()&&rightResult.isIntegralType()) {
          return Optional.of(SymTypeExpressionFactory.createTypeConstant("int"));
        }
      }
    }
    //should not happen, no valid result, error will be handled in traverse
    return Optional.empty();
  }
}

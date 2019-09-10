/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.types2.DeriveSymTypeOfLiterals;
import de.monticore.types2.SymTypeExpression;
import de.monticore.typescalculator.*;
import mc.typescalculator.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsDelegatorVisitor;

import java.util.HashMap;
import java.util.Map;

public class CombineExpressionsWithLiteralsTypesCalculator extends CombineExpressionsWithLiteralsDelegatorVisitor implements IExpressionAndLiteralsTypeCalculatorVisitor {

  private CombineExpressionsWithLiteralsDelegatorVisitor realThis;

  private Map<ASTNode, SymTypeExpression> types;

  private AssignmentExpressionTypesCalculator assignmentExpressionTypesCalculator;

  private CommonExpressionTypesCalculator commonExpressionTypesCalculator;

  private ExpressionsBasisTypesCalculator expressionsBasisTypesCalculator;

  private CombineExpressionsWithLiteralsLiteralTypesCalculator literalsLiteralTypesCalculator;

  private DeriveSymTypeOfLiterals deriveSymTypeOfLiterals;

  private CommonLiteralsTypesCalculator commonLiteralsTypesCalculator;


  public CombineExpressionsWithLiteralsTypesCalculator(IExpressionsBasisScope scope){
    this.realThis=this;
    this.types = new HashMap<>();

    commonExpressionTypesCalculator = new CommonExpressionTypesCalculator();
    commonExpressionTypesCalculator.setScope(scope);
    commonExpressionTypesCalculator.setTypes(types);
    setCommonExpressionsVisitor(commonExpressionTypesCalculator);

    assignmentExpressionTypesCalculator = new AssignmentExpressionTypesCalculator();
    assignmentExpressionTypesCalculator.setScope(scope);
    assignmentExpressionTypesCalculator.setTypes(types);
    setAssignmentExpressionsVisitor(assignmentExpressionTypesCalculator);

    expressionsBasisTypesCalculator = new ExpressionsBasisTypesCalculator();
    expressionsBasisTypesCalculator.setScope(scope);
    expressionsBasisTypesCalculator.setTypes(types);
    setExpressionsBasisVisitor(expressionsBasisTypesCalculator);

    CombineExpressionsWithLiteralsLiteralTypesCalculator literalsLiteralTypesCalculator = new CombineExpressionsWithLiteralsLiteralTypesCalculator();
    literalsLiteralTypesCalculator.setTypes(types);
    setCombineExpressionsWithLiteralsVisitor(literalsLiteralTypesCalculator);
    this.literalsLiteralTypesCalculator=literalsLiteralTypesCalculator;
  
    DeriveSymTypeOfLiterals deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    setMCLiteralsBasisVisitor(deriveSymTypeOfLiterals);
    this.deriveSymTypeOfLiterals = deriveSymTypeOfLiterals;

    CommonLiteralsTypesCalculator commonLiteralsTypesCalculator = new CommonLiteralsTypesCalculator();
    commonLiteralsTypesCalculator.setTypes(types);
    commonLiteralsTypesCalculator.setScope(scope);
    setMCCommonLiteralsVisitor(commonLiteralsTypesCalculator);
    this.commonLiteralsTypesCalculator=commonLiteralsTypesCalculator;
  }

  public SymTypeExpression calculateType(ASTExpression e){
    e.accept(realThis);
    if(types.get(e)!=null){
      return types.get(e);
    }
    return null;
  }

  @Override
  public CombineExpressionsWithLiteralsDelegatorVisitor getRealThis(){
    return realThis;
  }

  public Map<ASTNode,SymTypeExpression> getTypes(){
    return types;
  }

  public void setScope(IExpressionsBasisScope scope){
    assignmentExpressionTypesCalculator.setScope(scope);
    expressionsBasisTypesCalculator.setScope(scope);
    commonExpressionTypesCalculator.setScope(scope);
  }

}
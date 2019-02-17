/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor;
import de.se_rwth.commons.Names;

import java.util.Iterator;

public class MCBasicTypesPrettyPrinter implements MCBasicTypesVisitor {

  private MCBasicTypesVisitor realThis = this;

  public IndentPrinter getPrinter() {
    return printer;
  }

  public MCBasicTypesPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  // printer to use
  protected IndentPrinter printer;


  /**
   * Prints qualified names
   *
   * @param a qualified name
   */
  @Override
  public void handle(ASTMCQualifiedName a) {
    getPrinter().print(Names.getQualifiedName(a.getPartList()));
  }


  /**
   * Prints a void type.
   *
   * @param a void type
   */
  @Override
  public void handle(ASTMCVoidType a) {
    getPrinter().print("void");
  }

  /**
   * Prints a primitive type.
   *
   * @param a primitive type
   */
  @Override
  public void handle(ASTMCPrimitiveType a) {
    switch (a.getPrimitive()) {
      case ASTConstantsMCBasicTypes.BOOLEAN:
        getPrinter().print("boolean");
        break;
      case ASTConstantsMCBasicTypes.BYTE:
        getPrinter().print("byte");
        break;
      case ASTConstantsMCBasicTypes.CHAR:
        getPrinter().print("char");
        break;
      case ASTConstantsMCBasicTypes.SHORT:
        getPrinter().print("short");
        break;
      case ASTConstantsMCBasicTypes.INT:
        getPrinter().print("int");
        break;
      case ASTConstantsMCBasicTypes.FLOAT:
        getPrinter().print("float");
        break;
      case ASTConstantsMCBasicTypes.LONG:
        getPrinter().print("long");
        break;
      case ASTConstantsMCBasicTypes.DOUBLE:
        getPrinter().print("double");
        break;
      default: getPrinter().print("");
    }
  }

  @Override
  public void handle(ASTMCImportStatement a){
    getPrinter().print("import " + a.getMCQualifiedName().toString());
    if(a.isStar()){
      getPrinter().print(".*");
    }
    getPrinter().print(";");
  }

  /**
   * This method prettyprints a given node from type grammar.
   *
   * @param a A node from type grammar.
   * @return String representation.
   */
  public String prettyprint(ASTMCBasicTypesNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }


  public void setRealThis(MCBasicTypesVisitor realThis) {
    this.realThis = realThis;
  }


  @Override
  public MCBasicTypesVisitor getRealThis() {
    return realThis;
  }

  /**
   * Prints a list
   *
   * @param iter iterator for the list
   * @param seperator string for seperating list
   */
  protected void printList(Iterator<? extends ASTMCBasicTypesNode> iter, String seperator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      getPrinter().print(sep);
      iter.next().accept(getRealThis());
      sep = seperator;
    }
  }

}

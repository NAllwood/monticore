/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcfullgenerictypes._ast;

import com.google.common.collect.ImmutableList;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;

import java.util.List;

public class ASTMCMultipleGenericType extends ASTMCMultipleGenericTypeTOP {
  public ASTMCMultipleGenericType() {
  }

  protected  ASTMCMultipleGenericType (/* generated by template ast.ConstructorParametersDeclaration*/
          de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType mCBasicGenericType
          ,
          java.util.List<de.monticore.types.mcfullgenerictypes._ast.ASTMCInnerType> mCInnerTypes

  )
    /* generated by template ast.ConstructorAttributesSetter*/
  {
    setMCBasicGenericType(mCBasicGenericType);
    setMCInnerTypeList(mCInnerTypes);
  }

  @Override
  public String getBaseName() {
    return getNameList().get(getNameList().size()-1);
  }

  @Override
  public List<String> getNameList() {
    ImmutableList.Builder<String> nameList = new ImmutableList.Builder<String>();

    nameList.addAll(getMCBasicGenericType().getNameList());

    for(ASTMCInnerType inner : getMCInnerTypeList()) {
      nameList.add(inner.getName());
    }

    return null;
  }

  public void setNameList(List<String> names) {

  }

  @Override
  public List<ASTMCTypeArgument> getMCTypeArgumentList() {
    return null;
  }

  public void setMCTypeArgumentList(List<ASTMCTypeArgument> arguments) {

  }

}

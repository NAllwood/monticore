<#-- (c) https://github.com/MontiCore/monticore -->
    abstract void handle${ast.getInput()?cap_first}(${glex.getGlobalVar("modelName")?cap_first} model);
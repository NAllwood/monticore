<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
  ${tc.includeArgs("methods.opt.Set", [attribute, attribute.getName()])}
  setName(spanningSymbol.getName());
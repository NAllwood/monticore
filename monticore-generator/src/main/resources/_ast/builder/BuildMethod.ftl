<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domainClass", "mandatoryAttributes")}
        if (!isValid()) {
        <#list mandatoryAttributes as attribute>
            if (${attribute.getName()} == null) {
                Log.error("0xA7222 ${attribute.getName()} of type ${attribute.printType()} must not be null");
            }
        </#list>
          throw new IllegalStateException();
        }
        ${domainClass.getName()} value;
        ${tc.include("_ast.builder.BuildInit")}
        return value;
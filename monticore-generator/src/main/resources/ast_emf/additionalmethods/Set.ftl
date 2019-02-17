<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("grammarName", "cdAttribute","emfAttribute", "cDAndJavaConformName")}
  <#assign genHelper = glex.getGlobalVar("astHelper")>
<#if genHelper.isReferencedSymbolAttribute(cdAttribute)>
  <#if astHelper.isListType(cdAttribute.printType())>
    ${cDAndJavaConformName}Map.clear();
  <#else >
    ${cDAndJavaConformName}Symbol = Optional.empty();
  </#if>
</#if>
  <#if emfAttribute.isOptional()>
    ${astHelper.getTypeNameWithoutOptional(emfAttribute.getCdAttribute())} old${cDAndJavaConformName?cap_first} = this.${cDAndJavaConformName}.isPresent()? this.${cDAndJavaConformName}.get() : null;
    this.${cDAndJavaConformName} = Optional.ofNullable(${cDAndJavaConformName});
  <#else>
    <#if emfAttribute.isAstList()>
    if (${cDAndJavaConformName}.isEmpty() && this.${cDAndJavaConformName}.isEmpty()) {
      return;
    }  
    </#if>
    ${emfAttribute.getTypeName()} old${cDAndJavaConformName?cap_first} = this.${cDAndJavaConformName};
    this.${cDAndJavaConformName} = ${cDAndJavaConformName};
  </#if>
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, ${grammarName}Package.${emfAttribute.getFullName()}, old${cDAndJavaConformName?cap_first}, ${cDAndJavaConformName}));
    }  

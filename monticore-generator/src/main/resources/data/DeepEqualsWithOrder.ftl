<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("allAttributes", "simpleClassName")}
   <#assign genHelper = glex.getGlobalVar("astHelper")>
      ${simpleClassName} comp;
    if ((o instanceof ${simpleClassName})) {
      comp = (${simpleClassName}) o;
    } else {
      return false;
    }
    if (!equalAttributes(comp)) {
      return false;
    }
    <#-- TODO: attributes of super class - use symbol table -->
    <#list allAttributes  as attribute>
       <#assign attrName = attribute.getName()>
       <#if genHelper.isOptionalAstNode(attribute)>
    // comparing ${attrName}   
    if ( this.${attrName}.isPresent() != comp.${attrName}.isPresent() ||
      (this.${attrName}.isPresent() && !this.${attrName}.get().deepEquals(comp.${attrName}.get(), forceSameOrder)) ) {
      return false;
    }
       <#elseif genHelper.isListAstNode(attribute)>
    // comparing ${attrName}
    if (this.${attrName}.size() != comp.${attrName}.size()) {
      return false;
    } else {
      <#assign astChildTypeName = genHelper.getAstClassNameForASTLists(attribute)>
      if (forceSameOrder) {
        Iterator<${astChildTypeName}> it1 = this.${attrName}.iterator();
        Iterator<${astChildTypeName}> it2 = comp.${attrName}.iterator();
        while (it1.hasNext() && it2.hasNext()) {
          if (!it1.next().deepEquals(it2.next(), forceSameOrder)) {
            return false;
          }
        }
      } else {
        java.util.Iterator<${astChildTypeName}> it1 = this.${attrName}.iterator();
        while (it1.hasNext()) {
          ${astChildTypeName} oneNext = it1.next();
          boolean matchFound = false;
          java.util.Iterator<${astChildTypeName}> it2 = comp.${attrName}.iterator();
          while (it2.hasNext()) {
            if (oneNext.deepEquals(it2.next(), forceSameOrder)) {
              matchFound = true;
              break;
            }
          }
          if (!matchFound) {
            return false;
          }
        }
      }
    }
   <#elseif genHelper.isSimpleAstNode(attribute)>
     // comparing ${attrName}
     if ( (this.${attrName} == null && comp.${attrName} != null) ||
        (this.${attrName} != null && !this.${attrName}.deepEquals(comp.${attrName}, forceSameOrder)) ) {
        return false;
     }
       </#if>
     </#list>
    return true;     


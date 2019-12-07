/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable;

import de.monticore.cd.CD4AnalysisHelper;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbolLoader;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.Names;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PACKAGE;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PREFIX;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILDER_SUFFIX;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.utils.Names.getSimpleName;
import static de.se_rwth.commons.Names.getQualifier;

public class SymbolTableService extends AbstractService<SymbolTableService> {

  public SymbolTableService(ASTCDCompilationUnit compilationUnit) {
    super(compilationUnit);
  }

  public SymbolTableService(CDDefinitionSymbol cdSymbol) {
    super(cdSymbol);
  }

  @Override
  public String getSubPackage() {
    return SYMBOL_TABLE_PACKAGE;
  }

  @Override
  protected SymbolTableService createService(CDDefinitionSymbol cdSymbol) {
    return createSymbolTableService(cdSymbol);
  }

  public static SymbolTableService createSymbolTableService(CDDefinitionSymbol cdSymbol) {
    return new SymbolTableService(cdSymbol);
  }

  public String getSerializationPackage() {
    return getSerializationPackage(getCDSymbol());
  }

  public String getSerializationPackage(CDDefinitionSymbol cdDefinitionSymbol) {
    return getPackage(cdDefinitionSymbol) + "." + SERIALIZATION_PACKAGE;
  }

  /**
   * scope class names e.g. AutomataScope
   */

  public String getScopeClassSimpleName() {
    return getScopeClassSimpleName(getCDSymbol());
  }

  public String getScopeClassSimpleName(CDDefinitionSymbol cdSymbol) {
    return cdSymbol.getName() + SCOPE_SUFFIX;
  }

  public String getScopeClassFullName() {
    return getScopeClassFullName(getCDSymbol());
  }

  public String getScopeClassFullName(CDDefinitionSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getScopeClassSimpleName(cdSymbol);
  }

  public ASTMCQualifiedType getScopeType() {
    return getMCTypeFacade().createQualifiedType(getScopeClassFullName());
  }

  /**
   * scope interface names e.g. IAutomataScope
   */

  public String getScopeInterfaceSimpleName() {
    return getScopeInterfaceSimpleName(getCDSymbol());
  }

  public String getScopeInterfaceSimpleName(CDDefinitionSymbol cdSymbol) {
    return INTERFACE_PREFIX + cdSymbol.getName() + SCOPE_SUFFIX;
  }

  public String getScopeInterfaceFullName() {
    return getScopeInterfaceFullName(getCDSymbol());
  }

  public String getScopeInterfaceFullName(CDDefinitionSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getScopeInterfaceSimpleName(cdSymbol);
  }

  public ASTMCQualifiedType getScopeInterfaceType() {
    return getScopeInterfaceType(getCDSymbol());
  }

  public ASTMCQualifiedType getScopeInterfaceType(CDDefinitionSymbol cdSymbol) {
    return getMCTypeFacade().createQualifiedType(getScopeInterfaceFullName(cdSymbol));
  }

  /**
   * artifact scope class names e.g. AutomataArtifactScope
   */

  public String getArtifactScopeSimpleName(CDDefinitionSymbol cdSymbol) {
    return cdSymbol.getName() + ARTIFACT_PREFIX + SCOPE_SUFFIX;
  }

  public String getArtifactScopeSimpleName() {
    return getArtifactScopeSimpleName(getCDSymbol());
  }

  public String getArtifactScopeFullName(CDDefinitionSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getArtifactScopeSimpleName(cdSymbol);
  }

  public String getArtifactScopeFullName() {
    return getArtifactScopeFullName(getCDSymbol());
  }

  /**
   * global scope class names e.g. AutomataGlobalScope
   */

  public String getGlobalScopeFullName(CDDefinitionSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getGlobalScopeSimpleName(cdSymbol);
  }

  public String getGlobalScopeFullName() {
    return getGlobalScopeFullName(getCDSymbol());
  }

  public String getGlobalScopeSimpleName(CDDefinitionSymbol cdSymbol) {
    return cdSymbol.getName() + GLOBAL_SUFFIX + SCOPE_SUFFIX;
  }

  public String getGlobalScopeSimpleName() {
    return getGlobalScopeSimpleName(getCDSymbol());
  }

  /**
   * global scope interface names e.g. IAutomataGlobalScope
   */

  public String getGlobalScopeInterfaceFullName(CDDefinitionSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getGlobalScopeInterfaceSimpleName(cdSymbol);
  }

  public String getGlobalScopeInterfaceFullName() {
    return getGlobalScopeInterfaceFullName(getCDSymbol());
  }

  public String getGlobalScopeInterfaceSimpleName(CDDefinitionSymbol cdSymbol) {
    return INTERFACE_PREFIX + cdSymbol.getName() + GLOBAL_SUFFIX + SCOPE_SUFFIX;
  }

  public String getGlobalScopeInterfaceSimpleName() {
    return getGlobalScopeInterfaceSimpleName(getCDSymbol());
  }

  /**
   * language class names e.g. AutomataLanguage
   */

  public String getLanguageClassFullName(CDDefinitionSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getLanguageClassSimpleName(cdSymbol);
  }

  public String getLanguageClassFullName() {
    return getLanguageClassFullName(getCDSymbol());
  }

  public String getLanguageClassSimpleName(CDDefinitionSymbol cdSymbol) {
    return cdSymbol.getName() + LANGUAGE_SUFFIX;
  }

  public String getLanguageClassSimpleName() {
    return getLanguageClassSimpleName(getCDSymbol());
  }

  /**
   * model loader class names e.g. AutomataModelLoader
   */

  public String getModelLoaderClassFullName(CDDefinitionSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getModelLoaderClassSimpleName(cdSymbol);
  }

  public String getModelLoaderClassFullName() {
    return getModelLoaderClassFullName(getCDSymbol());
  }

  public String getModelLoaderClassSimpleName(CDDefinitionSymbol cdSymbol) {
    return cdSymbol.getName() + MODEL_LOADER_SUFFIX;
  }

  public String getModelLoaderClassSimpleName() {
    return getModelLoaderClassSimpleName(getCDSymbol());
  }

  /**
   * symbol reference class names e.g. AutomatonSymbolReference
   */

  public String getSymbolReferenceClassFullName(ASTCDType astcdType, CDDefinitionSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getSymbolLoaderSimpleName(astcdType);
  }

  public String getSymbolReferenceClassFullName(ASTCDType astcdType) {
    return getSymbolReferenceClassFullName(astcdType, getCDSymbol());
  }

  public String getSymbolLoaderSimpleName(ASTCDType astcdType) {
    return getSymbolSimpleName(astcdType) + LOADER_SUFFIX;
  }

  /**
   * resolving delegate symbol interface e.g. IAutomatonSymbolResolvingDelegate
   */

  public String getSymbolResolvingDelegateInterfaceSimpleName(ASTCDType astcdType) {
    return INTERFACE_PREFIX + getSymbolSimpleName(astcdType) + RESOLVING_DELEGATE_SUFFIX;
  }

  public String getSymbolResolvingDelegateInterfaceFullName(ASTCDType astcdType) {
    return getSymbolResolvingDelegateInterfaceFullName(astcdType, getCDSymbol());
  }

  public String getSymbolResolvingDelegateInterfaceFullName(ASTCDType astcdType, CDDefinitionSymbol cdDefinitionSymbol) {
    return getPackage(cdDefinitionSymbol) + "." + getSymbolResolvingDelegateInterfaceSimpleName(astcdType);
  }

  /**
   * common symbol interface names e.g. ICommonAutomataSymbol
   */

  public String getCommonSymbolInterfaceSimpleName(CDDefinitionSymbol cdSymbol) {
    return INTERFACE_PREFIX + COMMON_PREFIX + cdSymbol.getName() + SYMBOL_SUFFIX;
  }

  public String getCommonSymbolInterfaceSimpleName() {
    return getCommonSymbolInterfaceSimpleName(getCDSymbol());
  }

  public String getCommonSymbolInterfaceFullName(CDDefinitionSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getCommonSymbolInterfaceSimpleName();
  }

  public String getCommonSymbolInterfaceFullName() {
    return getCommonSymbolInterfaceFullName(getCDSymbol());
  }

  /**
   * symbol table symbol interface names e.g. AutomataSymbolTable
   */

  public String getSymbolTableCreatorSimpleName(CDDefinitionSymbol cdSymbol) {
    return cdSymbol.getName() + SYMBOL_TABLE_CREATOR_SUFFIX;
  }

  public String getSymbolTableCreatorSimpleName() {
    return getSymbolTableCreatorSimpleName(getCDSymbol());
  }

  public String getSymbolTableCreatorFullName(CDDefinitionSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getSymbolTableCreatorSimpleName(cdSymbol);
  }

  public String getSymbolTableCreatorFullName() {
    return getSymbolTableCreatorFullName(getCDSymbol());
  }

  /**
   * symbol table symbol interface names e.g. AutomataSymbolTable
   */

  public String getSymbolTableCreatorDelegatorSimpleName(CDDefinitionSymbol cdSymbol) {
    return cdSymbol.getName() + SYMBOL_TABLE_CREATOR_SUFFIX + DELEGATOR_SUFFIX;
  }

  public String getSymbolTableCreatorDelegatorSimpleName() {
    return getSymbolTableCreatorDelegatorSimpleName(getCDSymbol());
  }

  public String getSymbolTableCreatorDelegatorFullName(CDDefinitionSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getSymbolTableCreatorDelegatorSimpleName(cdSymbol);
  }

  public String getSymbolTableCreatorDelegatorFullName() {
    return getSymbolTableCreatorDelegatorFullName(getCDSymbol());
  }

  /**
   * deser class names e.g. AutomataDeSer
   */

  public String getScopeDeSerSimpleName(CDDefinitionSymbol cdSymbol) {
    return cdSymbol.getName() + SCOPE_SUFFIX + DE_SER_SUFFIX;
  }

  public String getScopeDeSerSimpleName() {
    return getScopeDeSerSimpleName(getCDSymbol());
  }

  public String getScopeDeSerFullName(CDDefinitionSymbol cdSymbol) {
    return getSerializationPackage(cdSymbol) + "." + getScopeDeSerSimpleName(cdSymbol);
  }

  public String getScopeDeSerFullName() {
    return getScopeDeSerFullName(getCDSymbol());
  }


  /**
   * symTabMill interface names e.g. AutomataSymTabMill
   */

  public String getSymTabMillSimpleName(CDDefinitionSymbol cdSymbol) {
    return cdSymbol.getName() + SYM_TAB_MILL_SUFFIX;
  }

  public String getSymTabMillSimpleName() {
    return getSymTabMillSimpleName(getCDSymbol());
  }

  public String getSymTabMillFullName(CDDefinitionSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getSymTabMillSimpleName(cdSymbol);
  }

  public String getSymTabMillFullName() {
    return getSymTabMillFullName(getCDSymbol());
  }

  /**
   * symTabMill interface names e.g. AutomataSymTabMill
   */

  public String getSuperSTCForSubSTCSimpleName(CDDefinitionSymbol superCDSymbol, CDDefinitionSymbol subCDSymbol) {
    return String.format(STC_FOR, superCDSymbol.getName(), subCDSymbol.getName());
  }

  public String getSuperSTCForSubSTCSimpleName(CDDefinitionSymbol superCDSymbol) {
    return getSuperSTCForSubSTCSimpleName(superCDSymbol, getCDSymbol());
  }

  public String getSuperSTCForSubSTCFullName(CDDefinitionSymbol superCDSymbol, CDDefinitionSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getSuperSTCForSubSTCSimpleName(superCDSymbol, cdSymbol);
  }

  public String getSuperSTCForSubSTCFullName(CDDefinitionSymbol superCDSymbol) {
    return getSuperSTCForSubSTCFullName(superCDSymbol, getCDSymbol());
  }

  /**
   * symbolDeSer class names e.g. StateSymbolDeSer
   */

  public String getSymbolDeSerFullName(ASTCDType astcdType, CDDefinitionSymbol cdSymbol) {
    return getSerializationPackage(cdSymbol) + "." + getSymbolDeSerSimpleName(astcdType);
  }

  public String getSymbolDeSerFullName(ASTCDType astcdType) {
    return getSymbolDeSerFullName(astcdType, getCDSymbol());
  }

  public String getSymbolDeSerSimpleName(ASTCDType astcdType) {
    return getSymbolSimpleName(astcdType) + DE_SER_SUFFIX;
  }

  /**
   * symTabMill interface names e.g. AutomataSymTabMill
   */

  public String getSymbolTablePrinterSimpleName(CDDefinitionSymbol cdSymbol) {
    return cdSymbol.getName() + SYMBOL_TABLE_PRINTER_SUFFIX;
  }

  public String getSymbolTablePrinterSimpleName() {
    return getSymbolTablePrinterSimpleName(getCDSymbol());
  }

  public String getSymbolTablePrinterFullName(CDDefinitionSymbol cdSymbol) {
    return getSerializationPackage(cdSymbol) + "." + getSymbolTablePrinterSimpleName(cdSymbol);
  }

  public String getSymbolTablePrinterFullName() {
    return getSymbolTablePrinterFullName(getCDSymbol());
  }


  /**
   * symbol class names e.g. AutomatonSymbol
   */

  public String getNameWithSymbolSuffix(ASTCDType clazz) {
    // normal symbol name calculation from -> does not consider manually given symbol types
    // e.g. 'symbol (MCType) MCQualifiedType implements MCObjectType = MCQualifiedName;' will be MCQualifiedTypeSymbol
    return removeASTPrefix(clazz) + SYMBOL_SUFFIX;
  }

  public String getSymbolSimpleName(ASTCDType clazz) {
    // if in grammar other symbol Name is defined e.g. 'symbol (MCType) MCQualifiedType implements MCObjectType = MCQualifiedName;'
    // this will evaluate to MCTypeSymbol
    if (clazz.getModifierOpt().isPresent()) {
      Optional<String> symbolTypeValue = getSymbolTypeValue(clazz.getModifierOpt().get());
      if (symbolTypeValue.isPresent()) {
        return Names.getSimpleName(symbolTypeValue.get());
      }
    }
    return getNameWithSymbolSuffix(clazz);
  }

  public String getSymbolFullName(ASTCDType clazz) {
    //if in grammar other symbol Name is defined e.g. 'symbol (MCType) MCQualifiedType implements MCObjectType = MCQualifiedName;'
    return getSymbolFullName(clazz, getCDSymbol());
  }

  public String getSymbolFullName(ASTCDType clazz, CDDefinitionSymbol cdDefinitionSymbol) {
    //if in grammar other symbol Name is defined e.g. 'symbol (MCType) MCQualifiedType implements MCObjectType = MCQualifiedName;'
    if (clazz.getModifierOpt().isPresent()) {
      Optional<String> symbolTypeValue = getSymbolTypeValue(clazz.getModifierOpt().get());
      if (symbolTypeValue.isPresent()) {
        return symbolTypeValue.get();
      }
    }
    return getPackage(cdDefinitionSymbol) + "." + getNameWithSymbolSuffix(clazz);
  }

  public Optional<String> getDefiningSymbolFullName(ASTCDType clazz) {
    // does only return symbol defining parts, not parts with e.g. symbol (MCType)
    return getDefiningSymbolFullName(clazz, getCDSymbol());
  }

  public Optional<String> getDefiningSymbolFullName(ASTCDType clazz, CDDefinitionSymbol cdDefinitionSymbol) {
    //if in grammar other symbol Name is defined e.g. 'symbol (MCType) MCQualifiedType implements MCObjectType = MCQualifiedName;'
    if (clazz.getModifierOpt().isPresent()) {
      Optional<String> symbolTypeValue = getSymbolTypeValue(clazz.getModifierOpt().get());
      if (symbolTypeValue.isPresent()) {
        // is a symbol, but no defining one
        return Optional.empty();
      } else if (hasSymbolStereotype(clazz.getModifierOpt().get())) {
        // is a defining symbol
        return Optional.of(getPackage(cdDefinitionSymbol) + "." + getNameWithSymbolSuffix(clazz));
      }
    }
    // no symbol at all
    return Optional.empty();
  }

  public Optional<String> getDefiningSymbolSimpleName(ASTCDType clazz) {
    // does only return symbol defining parts, not parts with e.g. symbol (MCType)
    if (clazz.getModifierOpt().isPresent()) {
      Optional<String> symbolTypeValue = getSymbolTypeValue(clazz.getModifierOpt().get());
      if (symbolTypeValue.isPresent()) {
        // is a symbol, but no defining one
        return Optional.empty();
      } else if (hasSymbolStereotype(clazz.getModifierOpt().get())) {
        // is a defining symbol
        return Optional.ofNullable(getNameWithSymbolSuffix(clazz));
      }
    }
    // no symbol at all
    return Optional.empty();
  }

  public String getSimpleNameFromSymbolName(String referencedSymbol) {
    return getSimpleName(referencedSymbol).substring(0, getSimpleName(referencedSymbol).indexOf(SYMBOL_SUFFIX));
  }

  /**
   * symbol builder class name e.g. AutomatonSymbolBuilder
   */
  public String getSymbolBuilderSimpleName(ASTCDType astcdType) {
    return getSymbolSimpleName(astcdType) + BUILDER_SUFFIX;
  }

  public String getSymbolBuilderFullName(ASTCDType astcdType, CDDefinitionSymbol cdDefinitionSymbol) {
    return getSymbolFullName(astcdType, cdDefinitionSymbol) + BUILDER_SUFFIX;
  }

  public String getSymbolBuilderFullName(ASTCDType astcdType) {
    return getSymbolBuilderFullName(astcdType, getCDSymbol());
  }

  public String getReferencedSymbolTypeName(ASTCDAttribute attribute) {
    return CD4AnalysisHelper.getStereotypeValues(attribute,
        MC2CDStereotypes.REFERENCED_SYMBOL.toString()).get(0);
  }

  public boolean isReferencedSymbol(ASTCDAttribute attribute) {
    return attribute.isPresentModifier() && hasStereotype(attribute.getModifier(), MC2CDStereotypes.REFERENCED_SYMBOL);
  }

  /**
   * returns the stereotype value for 'symbol' or 'inheritedSymbol'
   * only returns a value if it is a symbol reference and no symbol definition
   */
  public Optional<String> getSymbolTypeValue(ASTModifier modifier) {
    List<String> symbolStereotypeValues = getStereotypeValues(modifier, MC2CDStereotypes.SYMBOL);
    if (!symbolStereotypeValues.isEmpty()) {
      return Optional.ofNullable(symbolStereotypeValues.get(0));
    } else {
      List<String> inheritedStereotypeValues = getStereotypeValues(modifier, MC2CDStereotypes.INHERITED_SYMBOL);
      if (!inheritedStereotypeValues.isEmpty()) {
        return Optional.ofNullable(inheritedStereotypeValues.get(0));
      }
    }
    return Optional.empty();
  }

  public Optional<ASTCDType> getTypeWithSymbolInfo(ASTCDType type) {
    if (type.getModifierOpt().isPresent() && hasSymbolStereotype(type.getModifierOpt().get())) {
      return Optional.of(type);
    }
    if (!type.getSymbolOpt().isPresent()) {
      return Optional.empty();
    }
    for (CDTypeSymbolLoader superType : type.getSymbol().getCdInterfaceList()) {
      if (superType.isSymbolLoaded() && superType.getLoadedSymbol().isPresentAstNode()) {
        Optional<ASTCDType> result = getTypeWithSymbolInfo(superType.getLoadedSymbol().getAstNode());
        if (result.isPresent()) {
          return result;
        }
      }
    }
    return Optional.empty();
  }

  public Optional<ASTCDType> getTypeWithScopeInfo(ASTCDType type) {
    if (type.getModifierOpt().isPresent() && hasScopeStereotype(type.getModifierOpt().get())) {
      return Optional.of(type);
    }
    if (!type.isPresentSymbol()) {
      return Optional.empty();
    }
    for (CDTypeSymbolLoader superType : type.getSymbol().getCdInterfaceList()) {
      if (superType.isSymbolLoaded() && superType.getLoadedSymbol().isPresentAstNode()) {
        Optional<ASTCDType> result = getTypeWithScopeInfo(superType.getLoadedSymbol().getAstNode());
        if (result.isPresent()) {
          return result;
        }
      }
    }
    return Optional.empty();
  }

  /**
   * get classes and interfaces with scope or symbol stereotype
   */

  public List<ASTCDType> getSymbolDefiningProds(ASTCDDefinition astcdDefinition) {
    List<ASTCDType> symbolProds = getSymbolDefiningProds(astcdDefinition.getCDClassList());
    symbolProds.addAll(getSymbolDefiningProds(astcdDefinition.getCDInterfaceList()));
    return symbolProds;
  }

  public List<ASTCDType> getSymbolDefiningSuperProds() {
    List<ASTCDType> symbolProds = new ArrayList<>();
    for (CDDefinitionSymbol cdDefinitionSymbol : getSuperCDsTransitive()) {
      for (CDTypeSymbol type : cdDefinitionSymbol.getTypes()) {
        if (type.isPresentAstNode() && type.getAstNode().getModifierOpt().isPresent()
            && hasSymbolStereotype(type.getAstNode().getModifierOpt().get())) {
          symbolProds.add(type.getAstNode());
        }
      }
    }
    return symbolProds;
  }

  public List<ASTCDType> getSymbolDefiningProds(List<? extends ASTCDType> astcdClasses) {
    return astcdClasses.stream()
        .filter(c -> c.getModifierOpt().isPresent())
        .filter(c -> hasSymbolStereotype(c.getModifierOpt().get()))
        .filter(c -> !getSymbolTypeValue(c.getModifierOpt().get()).isPresent())
        .collect(Collectors.toList());
  }

  /**
   * returns classes that get their symbol property from a symbol interface
   * e.g. interface symbol Foo = Name; Bla implements Foo = Name
   * -> than Bla has inherited symbol property
   *
   * @param astcdClasses
   * @return returns a Map of <the class that inherits the property, the symbol interface full name from which it inherits it>
   */
  public Map<ASTCDClass, String> getInheritedSymbolPropertyClasses(List<ASTCDClass> astcdClasses) {
    Map<ASTCDClass, String> inheritedSymbolProds = new HashMap<>();
    for (ASTCDClass astcdClass : astcdClasses) {
      // classes with inherited symbol property
      if (astcdClass.isPresentModifier() && hasInheritedSymbolStereotype(astcdClass.getModifier())) {
        List<String> stereotypeValues = getStereotypeValues(astcdClass.getModifier(), MC2CDStereotypes.INHERITED_SYMBOL);
        if (stereotypeValues.size() == 1) {
          inheritedSymbolProds.put(astcdClass, stereotypeValues.get(0));
        }
      }
    }
    return inheritedSymbolProds;
  }

  public List<ASTCDType> getNoSymbolAndScopeDefiningClasses(List<ASTCDClass> astcdClasses) {
    return astcdClasses.stream()
        .filter(ASTCDClassTOP::isPresentModifier)
        .filter(c -> !hasSymbolStereotype(c.getModifier()))
        .filter(c -> !hasScopeStereotype(c.getModifier()))
        .filter(c -> !hasInheritedSymbolStereotype(c.getModifier()))
        .collect(Collectors.toList());
  }

  public List<ASTCDType> getOnlyScopeClasses(ASTCDDefinition astcdDefinition) {
    List<ASTCDType> symbolProds = astcdDefinition.getCDClassList().stream()
        .filter(ASTCDClassTOP::isPresentModifier)
        .filter(c -> hasScopeStereotype(c.getModifier()))
        .filter(c -> !hasSymbolStereotype(c.getModifier()))
        .filter(c -> !hasInheritedSymbolStereotype(c.getModifier()))
        .collect(Collectors.toList());

    symbolProds.addAll(astcdDefinition.getCDInterfaceList().stream()
        .filter(ASTCDInterface::isPresentModifier)
        .filter(c -> hasScopeStereotype(c.getModifier()))
        .collect(Collectors.toList()));
    return symbolProds;
  }


  public boolean hasProd(ASTCDDefinition astcdDefinition) {
    // is true if it has any class productions or any interface productions that are not the language interface
    return !astcdDefinition.isEmptyCDClasss() ||
        (!astcdDefinition.isEmptyCDInterfaces() &&
            !(astcdDefinition.sizeCDInterfaces() == 1
                && astcdDefinition.getCDInterface(0).getName().equals(getSimleLanguageInterfaceName())));
  }

  public String removeASTPrefix(ASTCDType clazz) {
    // normal symbol name calculation from
    return removeASTPrefix(clazz.getName());
  }

  public String removeASTPrefix(String clazzName) {
    // normal symbol name calculation from
    if (clazzName.startsWith(AST_PREFIX)) {
      return clazzName.substring(AST_PREFIX.length());
    } else {
      return clazzName;
    }
  }

  public Optional<String> getStartProd(ASTCDDefinition astcdDefinition) {
    if (astcdDefinition.isPresentModifier() && hasStartProdStereotype(astcdDefinition.getModifier())) {
      return getStartProdValue(astcdDefinition.getModifier());
    }
    for (ASTCDClass prod : astcdDefinition.getCDClassList()) {
      if (hasStereotype(prod.getModifier(), MC2CDStereotypes.START_PROD)) {
        return Optional.of(getCDSymbol().getPackageName() + "." + getCDName() + "." + prod.getName());
      }
    }
    for (ASTCDInterface prod : astcdDefinition.getCDInterfaceList()) {
      if (hasStereotype(prod.getModifier(), MC2CDStereotypes.START_PROD)) {
        return Optional.of(getCDSymbol().getPackageName() + "." + getCDName() + "." + prod.getName());
      }
    }
    return Optional.empty();
  }

  public Optional<String> getStartProdASTFullName(ASTCDDefinition astcdDefinition) {
    Optional<String> startProd = getStartProd(astcdDefinition);
    if (startProd.isPresent()) {
      String simpleName = Names.getSimpleName(startProd.get());
      simpleName = simpleName.startsWith(AST_PREFIX) ? simpleName : AST_PREFIX + simpleName;
      String startProdAstName = getQualifier(startProd.get()).toLowerCase() + "." + AST_PACKAGE + "." + simpleName;
      return Optional.of(startProdAstName);
    }
    return Optional.empty();
  }

  public boolean hasStartProd(ASTCDDefinition astcdDefinition) {
    if (astcdDefinition.isPresentModifier() && hasStartProdStereotype(astcdDefinition.getModifier())) {
      return true;
    }
    for (ASTCDClass prod : astcdDefinition.getCDClassList()) {
      if (hasStereotype(prod.getModifier(), MC2CDStereotypes.START_PROD)) {
        return true;
      }
    }
    for (ASTCDInterface prod : astcdDefinition.getCDInterfaceList()) {
      if (hasStereotype(prod.getModifier(), MC2CDStereotypes.START_PROD)) {
        return true;
      }
    }
    return false;
  }

  public boolean hasInheritedSymbolStereotype(ASTModifier modifier) {
    return hasStereotype(modifier, MC2CDStereotypes.INHERITED_SYMBOL);
  }

  public boolean hasInheritedScopeStereotype(ASTModifier modifier) {
    return hasStereotype(modifier, MC2CDStereotypes.INHERITED_SCOPE);
  }

  public boolean hasComponentStereotype(ASTModifier modifier) {
    return hasStereotype(modifier, MC2CDStereotypes.COMPONENT);
  }

  public boolean hasStartProdStereotype(ASTModifier modifier) {
    return hasStereotype(modifier, MC2CDStereotypes.START_PROD);
  }

  public Optional<String> getStartProdValue(ASTModifier modifier) {
    List<String> stereotypeValues = getStereotypeValues(modifier, MC2CDStereotypes.START_PROD);
    if (!stereotypeValues.isEmpty()) {
      return Optional.ofNullable(stereotypeValues.get(0));
    }
    return Optional.empty();
  }

  public String determineReturnType(ASTMCType type) {
    if (type instanceof ASTMCPrimitiveType) {
      ASTMCPrimitiveType primitiveType = (ASTMCPrimitiveType) type;
      if (primitiveType.isBoolean()) {
        return "false";
      } else {
        return "0";
      }
    } else {
      return "null";
    }
  }
}

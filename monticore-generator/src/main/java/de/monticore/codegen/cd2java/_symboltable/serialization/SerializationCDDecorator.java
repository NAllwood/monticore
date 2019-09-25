package de.monticore.codegen.cd2java._symboltable.serialization;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4code._ast.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.PACKAGE;
import static de.monticore.codegen.cd2java.CoreTemplates.createPackageHookPoint;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.SERIALIZATION_PACKAGE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.SYMBOL_TABLE_PACKAGE;

public class SerializationCDDecorator extends AbstractDecorator {

  protected final SymbolTableService symbolTableService;

  protected final SymbolDeSerDecorator symbolDeSerDecorator;

  protected final ScopeDeSerDecorator scopeDeSerDecorator;

  protected final SymbolTablePrinterDecorator symbolTablePrinterDecorator;

  public SerializationCDDecorator(final GlobalExtensionManagement glex,
                                  final SymbolTableService symbolTableService,
                                  final SymbolDeSerDecorator symbolDeSerDecorator,
                                  final ScopeDeSerDecorator scopeDeSerDecorator,
                                  final SymbolTablePrinterDecorator symbolTablePrinterDecorator) {
    super(glex);
    this.symbolDeSerDecorator = symbolDeSerDecorator;
    this.scopeDeSerDecorator = scopeDeSerDecorator;
    this.symbolTableService = symbolTableService;
    this.symbolTablePrinterDecorator = symbolTablePrinterDecorator;
  }

  public ASTCDCompilationUnit decorate(ASTCDCompilationUnit astCD, ASTCDCompilationUnit symbolInput) {

    List<String> symbolTablePackage = new ArrayList<>(symbolInput.getPackageList());
    symbolTablePackage.addAll(Arrays.asList(symbolInput.getCDDefinition().getName().toLowerCase(), SYMBOL_TABLE_PACKAGE, SERIALIZATION_PACKAGE));

    ASTCDDefinition serializeCD = CD4CodeMill.cDDefinitionBuilder()
        .setName(symbolInput.getCDDefinition().getName())
        .addAllCDClasss(createSymbolDeSerClasses(symbolInput.getCDDefinition()))
        .addCDClass(createSymbolTablePrinterClass(astCD))
        .build();

    if (symbolTableService.hasStartProd(astCD.getCDDefinition())) {
      serializeCD.addCDClass(createScopeDeSerClass(symbolInput));
    }

    for (ASTCDClass cdClass : serializeCD.getCDClassList()) {
      this.replaceTemplate(PACKAGE, cdClass, createPackageHookPoint(symbolTablePackage));
    }

    for (ASTCDInterface cdInterface : serializeCD.getCDInterfaceList()) {
      this.replaceTemplate(CoreTemplates.PACKAGE, cdInterface, createPackageHookPoint(symbolTablePackage));
    }

    for (ASTCDEnum cdEnum : serializeCD.getCDEnumList()) {
      this.replaceTemplate(CoreTemplates.PACKAGE, cdEnum, createPackageHookPoint(symbolTablePackage));
    }

    return CD4AnalysisMill.cDCompilationUnitBuilder()
        .setPackageList(symbolTablePackage)
        .setCDDefinition(serializeCD)
        .build();
  }

  protected List<ASTCDClass> createSymbolDeSerClasses(ASTCDDefinition astcdDefinition) {
    List<ASTCDClass> symbolDeSerList = astcdDefinition.getCDClassList()
        .stream()
        .map(symbolDeSerDecorator::decorate)
        .collect(Collectors.toList());
    symbolDeSerList.addAll(astcdDefinition.getCDInterfaceList()
        .stream()
        .map(symbolDeSerDecorator::decorate)
        .collect(Collectors.toList()));
    return symbolDeSerList;
  }

  protected ASTCDClass createScopeDeSerClass(ASTCDCompilationUnit symbolCd) {
    return scopeDeSerDecorator.decorate(symbolCd);
  }

  protected ASTCDClass createSymbolTablePrinterClass(ASTCDCompilationUnit symbolCd) {
    return symbolTablePrinterDecorator.decorate(symbolCd);
  }
}
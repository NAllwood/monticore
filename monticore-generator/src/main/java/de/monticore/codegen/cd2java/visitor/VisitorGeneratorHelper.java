/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java.visitor;

import com.google.common.base.Joiner;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.codegen.GeneratorHelper;
import de.se_rwth.commons.JavaNamesHelper;
import de.se_rwth.commons.Names;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VisitorGeneratorHelper extends GeneratorHelper {
  
  public static final String VISITOR = "Visitor";
  public static final String SYMBOL_VISITOR = "Symbol" + VISITOR;
  public static final String SCOPE_VISITOR = "Scope" + VISITOR;
  
  public VisitorGeneratorHelper(ASTCDCompilationUnit topAst, CD4AnalysisGlobalScope symbolTable) {
    super(topAst, symbolTable);
  }
  
  /**
   * @return the superinterfaces for the visitor
   */
  public List<String> getVisitorSuperInterfacesList() {
    List<String> superVisitors = new ArrayList<>();
    for (String superGrammar : getSuperGrammarCds()) {
      String superGrammarName = Names.getSimpleName(superGrammar);
      String visitorType = getVisitorType(superGrammarName);
      String visitorPackage = getPackageName(superGrammar.toLowerCase(), getVisitorPackageSuffix());
      superVisitors.add(visitorPackage + "." + visitorType);
    }
    return superVisitors;
  }
  
  public List<String> getDelegatorVisitorSuperInterfacesList() {
    List<String> superVisitors = new ArrayList<>();
    // delegators are always an inheritance visitor of the own language
    superVisitors.add(getInheritanceVisitorType());
    return superVisitors;
  }
  
  /**
   * @return the superinterfaces for the visitor
   */
  public String getVisitorSuperInterfaces() {
    List<String> superVisitorInterfaces = getVisitorSuperInterfacesList();
    if (superVisitorInterfaces.isEmpty()) {
      return "";
    }
    return " extends " + Joiner.on(", ").join(superVisitorInterfaces);
  }
  
  /**
   * @return the superinterfaces for the delegator visitor
   */
  public String getDelegatorVisitorSuperInterfaces() {
    List<String> superVisitorInterfaces = getDelegatorVisitorSuperInterfacesList();
    if (superVisitorInterfaces.isEmpty()) {
      return "";
    }
    return " implements " + Joiner.on(", ").join(superVisitorInterfaces);
  }
  
  public String getVisitorPackage() {
    return getVisitorPackage(getPackageName());
  }
  
  /**
   * @return type name of the language's inheritance visitor interface
   * @see #getQualifiedVisitorType()
   */
  public String getInheritanceVisitorType() {
    return getInheritanceVisitorType(getCdName());
  }
  
  /**
   * @return type name of the language's delegator visitor interface
   * @see #getQualifiedVisitorType()
   */
  public String getDelegatorVisitorType() {
    return getDelegatorVisitorType(getCdName());
  }
  
  /**
   * @return the inheritance visitors of the super grammars separated by ","
   * starting with ","
   */
  public String getSuperInheritanceVisitorTypes() {
    StringBuilder s = new StringBuilder();
    for (String superGrammar : getSuperGrammarCds()) {
      String superGrammarName = Names.getSimpleName(superGrammar);
      String visitorType = getInheritanceVisitorType(superGrammarName);
      String visitorPackage = getPackageName(superGrammar.toLowerCase(), getVisitorPackageSuffix());
      s.append(", " + visitorPackage + "." + visitorType);
    }
    return s.toString();
  }
  
  /**
   * @return full-qualified name of the language's visitor interface
   * @see #getVisitorType()
   */
  public String getQualifiedVisitorType() {
    return getVisitorPackage() + "." + getVisitorType();
  }
  
  /**
   * @return type name of the language's visitor interface for the parent aware
   * version
   * @see #getQualifiedVisitorType()
   */
  public String getParentAwareVisitorType() {
    final String PARENT_AWARE_VISITOR_CLASS_PREFIX = "ParentAwareVisitor";
    return getCdName() + PARENT_AWARE_VISITOR_CLASS_PREFIX;
  }
  
  /**
   * @return the superinterfaces for the parent aware visitor
   */
  public String getParentAwareVisitorSuperInterfaces() {
    return " implements " + getVisitorType();
  }
  
  /**
   * @return type name of the language's visitor interface
   * @see #getQualifiedVisitorType()
   */
  public String getVisitorType() {
    return getVisitorType(getCdName());
  }
  
  /**
   * @return type name of the language's symbol visitor interface
   * @see #getQualifiedVisitorType()
   */
  public String getSymbolVisitorType() {
    return getSymbolVisitorType(getCdName());
  }
  
  /**
   * @return type name of the language's scope visitor interface
   * @see #getQualifiedVisitorType()
   */
  public String getScopeVisitorType() {
    return getScopeVisitorType(getCdName());
  }
  
  /**
   * @param cDName
   * @return type name of the language's visitor interface
   * @see #getQualifiedVisitorType()
   */
  public static String getVisitorType(String cDName) {
    return cDName + VISITOR;
  }
  
  /**
   * @param cDName
   * @return type name of the language's symbol visitor interface
   * @see #getQualifiedVisitorType()
   */
  public static String getSymbolVisitorType(String cDName) {
    return cDName + SYMBOL_VISITOR;
  }
  
  /**
   * @param cDName
   * @return type name of the language's scope visitor interface
   * @see #getQualifiedVisitorType()
   */
  public static String getScopeVisitorType(String cDName) {
    return cDName + SCOPE_VISITOR;
  }
  
  /**
   * s
   * 
   * @param cDName
   * @param index
   * @param allCDs
   * @return
   */
  public static String getVisitorType(String cDName, int index, List<CDDefinitionSymbol> allCDs) {
    List<String> names = new ArrayList<>();
    allCDs.forEach(a -> names.add(a.getName()));
    if (Collections.frequency(names, cDName) > 1) {
      return getVisitorType(cDName) + index;
    }
    return getVisitorType(cDName);
  }
  
  /**
   * @param name
   * @return name of the language's visitor interface, lowers first char and
   * checks for reserved java-keyword.
   * @see #getVisitorType(String)
   */
  public static String getVisitorName(String name) {
    return JavaNamesHelper.javaAttribute(name);
  }
  
  /**
   * @param cDName
   * @return type name of the language's inheritance visitor interface
   * @see #getQualifiedVisitorType()
   */
  public static String getInheritanceVisitorType(String cDName) {
    return cDName + "InheritanceVisitor";
  }
  
  /**
   * @param cDName
   * @return type name of the language's delegator visitor interface
   * @see #getQualifiedVisitorType()
   */
  public static String getDelegatorVisitorType(String cDName) {
    return cDName + "DelegatorVisitor";
  }
  
  /**
   * @param packageName
   * @param cdName
   * @return full-qualified name of the language's visitor interface
   * @see #getVisitorType()
   */
  public static String getQualifiedVisitorType(String packageName, String cdName) {
    return getPackageName(packageName, getVisitorPackageSuffix()) + "."
        + getVisitorType(cdName);
  }
  
  /**
   * Gets the full-qualified type of the languages visitor interface. For
   * example, input "a.b.c.D" results in output "a.b.c.d._visitor.DVisitor"
   * 
   * @param qualifiedLanguageName
   * @return the languages full-qualified visitor interface
   */
  public static String getQualifiedVisitorType(String qualifiedLanguageName) {
    String packageName = getCdPackage(qualifiedLanguageName);
    String cdName = getCdName(qualifiedLanguageName);
    return getQualifiedVisitorType(packageName, cdName);
  }
  
  /**
   * @param packageName
   * @param cdName
   * @return full-qualified name of the language's scope visitor interface
   * @see #getVisitorType()
   */
  public static String getQualifiedScopeVisitorType(String packageName, String cdName) {
    return getPackageName(packageName, getVisitorPackageSuffix()) + "."
        + getScopeVisitorType(cdName);
  }
  
  /**
   * Gets the full-qualified type of the languages scope visitor interface. For
   * example, input "a.b.c.D" results in output "a.b.c.d._visitor.DScopeVisitor"
   * 
   * @param qualifiedLanguageName
   * @return the languages full-qualified scope visitor interface
   */
  public static String getQualifiedScopeVisitorType(String qualifiedLanguageName) {
    String packageName = getCdPackage(qualifiedLanguageName);
    String cdName = getCdName(qualifiedLanguageName);
    return getQualifiedScopeVisitorType(packageName, cdName);
  }
  
  /**
   * Gets the full-qualified name of the visitor interface with dots replaced by
   * underscores. E.g., input a cd with qualified name "a.b.c.D" the result is
   * "a_b_c_d__visitor_DVisitor".
   * 
   * @param cd the class diagram to get the visitor interface for
   * @return the qualified name of the visitor interface with dots replaced by
   * underscores.
   */
  public String getQualifiedVisitorNameAsJavaName(CDDefinitionSymbol cd) {
    return qualifiedJavaTypeToName(getQualifiedVisitorType(cd));
  }
  
  /**
   * Gets the full-qualified java name of the visitor interface. E.g., input a
   * cd with qualified name "a.b.c.D" the result is "a.b.c.d._visitor.DVisitor".
   * 
   * @param cd the class diagram to get the visitor interface for.
   * @return the full-qualified java name of the visitor interface.
   */
  public String getQualifiedVisitorType(CDDefinitionSymbol cd) {
    return getQualifiedVisitorType(cd.getFullName());
  }
  
}

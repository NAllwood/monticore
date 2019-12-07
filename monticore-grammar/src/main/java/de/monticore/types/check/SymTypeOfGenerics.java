/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symboltable.serialization.JsonConstants;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbolLoader;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * SymTypeOfGenerics stores any kind of TypeConstructor applied
 * to Arguments, such as Map< int,Person >
 * List<Person>, List< Set< List< a >>>.
 * This subsumes all kinds of generic Types from several of the
 * MC-Type grammars.
 */
public class SymTypeOfGenerics extends SymTypeExpression {
  
  /**
   * List of arguments of a type constructor
   */
  protected List<SymTypeExpression> arguments = new LinkedList<>();

  /**
   * Constructor with all parameters that are stored:
   */
  public SymTypeOfGenerics(TypeSymbolLoader typeSymbolLoader) {
    this.typeSymbolLoader = typeSymbolLoader;
  }

  public SymTypeOfGenerics(TypeSymbolLoader typeSymbolLoader, List<SymTypeExpression> arguments) {
    this.typeSymbolLoader = typeSymbolLoader;
    this.arguments = arguments;
  }

  public String getTypeConstructorFullName() {
    return typeSymbolLoader.getName();
  }
  
  /**
   * print: Umwandlung in einen kompakten String
   */
  @Override
  public String print() {
    StringBuffer r = new StringBuffer(getTypeConstructorFullName()).append('<');
    for(int i = 0; i<arguments.size();i++){
      r.append(arguments.get(i).print());
      if(i<arguments.size()-1) { r.append(','); }
    }
    return r.append('>').toString();
  }
  
  /**
   * printAsJson: Umwandlung in einen kompakten Json String
   */
  protected String printAsJson() {
    JsonPrinter jp = new JsonPrinter();
    jp.beginObject();
    // Care: the following String needs to be adapted if the package was renamed
    jp.member(JsonConstants.KIND, "de.monticore.types.check.SymTypeOfGenerics");
    jp.member("typeConstructorFullName", getTypeConstructorFullName());
    jp.beginArray("arguments");
    for(SymTypeExpression exp : getArgumentList()) {
      jp.valueJson(exp.printAsJson());
    }
    jp.endArray();
    jp.endObject();
    return jp.getContent();
  }
  
  
  /**
   * getFullName: get the Qualified Name including Package
   */
  public String getFullName() {
    return getTypeConstructorFullName();
  }
  
  /**
   * getBaseName: get the unqualified Name (no ., no Package)
   */
  public String getBaseName() {
    String[] parts = getTypeConstructorFullName().split("\\.");
    return parts[parts.length - 1];
  }

  @Override
  public boolean isGenericType(){
    return true;
  }
  
  /**
   * This is a deep clone: it clones the whole structure including Symbols and Type-Info,
   * but not the name of the constructor
   * @return
   */
  @Override
  public SymTypeOfGenerics deepClone() {
    return new SymTypeOfGenerics(new TypeSymbolLoader(typeSymbolLoader.getName(), typeSymbolLoader.getEnclosingScope()), getArgumentList());
  }

  // --------------------------------------------------------------------------
  // From here on: Standard functionality to access the list of arguments
  // (was copied from a created class)
  // (and demonstrates that we still can optimize our generators & build processes)
  // --------------------------------------------------------------------------
  

  public  boolean containsArgument (Object element)  {
    return this.getArgumentList().contains(element);
  }

  public  boolean containsAllArguments (Collection<?> collection)  {
    return this.getArgumentList().containsAll(collection);
  }

  public  boolean isEmptyArguments ()  {
    return this.getArgumentList().isEmpty();
  }

  public Iterator<SymTypeExpression> iteratorArguments ()  {
    return this.getArgumentList().iterator();
  }

  public  int sizeArguments ()  {
    return this.getArgumentList().size();
  }

  public  de.monticore.types.check.SymTypeExpression[] toArrayArguments (de.monticore.types.check.SymTypeExpression[] array)  {
    return this.getArgumentList().toArray(array);
  }

  public  Object[] toArrayArguments ()  {
    return this.getArgumentList().toArray();
  }

  public  Spliterator<de.monticore.types.check.SymTypeExpression> spliteratorArguments ()  {
    return this.getArgumentList().spliterator();
  }

  public Stream<SymTypeExpression> streamArguments ()  {
    return this.getArgumentList().stream();
  }

  public  Stream<de.monticore.types.check.SymTypeExpression> parallelStreamArguments ()  {
    return this.getArgumentList().parallelStream();
  }

  public  de.monticore.types.check.SymTypeExpression getArgument (int index)  {
    return this.getArgumentList().get(index);
  }

  public  int indexOfArgument (Object element)  {
    return this.getArgumentList().indexOf(element);
  }

  public  int lastIndexOfArgument (Object element)  {
    return this.getArgumentList().lastIndexOf(element);
  }

  public  boolean equalsArguments (Object o)  {
    return this.getArgumentList().equals(o);
  }

  public  int hashCodeArguments ()  {
    return this.getArgumentList().hashCode();
  }

  public  ListIterator<de.monticore.types.check.SymTypeExpression> listIteratorArguments ()  {
    return this.getArgumentList().listIterator();
  }

  public  ListIterator<de.monticore.types.check.SymTypeExpression> listIteratorArguments (int index)  {
    return this.getArgumentList().listIterator(index);
  }

  public  List<de.monticore.types.check.SymTypeExpression> subListArguments (int start,int end)  {
    return this.getArgumentList().subList(start, end);
  }

  public  List<de.monticore.types.check.SymTypeExpression> getArgumentList ()  {
    return this.arguments;
  }

  public  void clearArguments ()  {
    this.getArgumentList().clear();
  }

  public  boolean addArgument (de.monticore.types.check.SymTypeExpression element)  {
    return this.getArgumentList().add(element);
  }

  public  boolean addAllArguments (Collection<? extends de.monticore.types.check.SymTypeExpression> collection)  {
    return this.getArgumentList().addAll(collection);
  }
  public  boolean removeArgument (Object element)  {
    return this.getArgumentList().remove(element);
  }

  public  boolean removeAllArguments (Collection<?> collection)  {
    return this.getArgumentList().removeAll(collection);
  }
  public  boolean retainAllArguments (Collection<?> collection)  {
    return this.getArgumentList().retainAll(collection);
  }

  public  boolean removeIfArgument (Predicate<? super SymTypeExpression> filter)  {
    return this.getArgumentList().removeIf(filter);
  }

  public  void forEachArguments (Consumer<? super SymTypeExpression> action)  {
    this.getArgumentList().forEach(action);
  }

  public  void addArgument (int index,de.monticore.types.check.SymTypeExpression element)  {
    this.getArgumentList().add(index, element);
  }

  public  boolean addAllArguments (int index,Collection<? extends de.monticore.types.check.SymTypeExpression> collection)  {
    return this.getArgumentList().addAll(index, collection);
  }

  public  de.monticore.types.check.SymTypeExpression removeArgument (int index)  {
    return this.getArgumentList().remove(index);
  }

  public  de.monticore.types.check.SymTypeExpression setArgument (int index,de.monticore.types.check.SymTypeExpression element)  {
    return this.getArgumentList().set(index, element);
  }

  public  void replaceAllArguments (UnaryOperator<SymTypeExpression> operator)  {
    this.getArgumentList().replaceAll(operator);
  }

  public  void sortArguments (Comparator<? super de.monticore.types.check.SymTypeExpression> comparator)  {
    this.getArgumentList().sort(comparator);
  }

  public  void setArgumentList (List<de.monticore.types.check.SymTypeExpression> arguments)  {
    this.arguments = arguments;
  }
}

<#--
***************************************************************************************
Copyright (c) 2015, MontiCore
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software
without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
SUCH DAMAGE.
***************************************************************************************
-->
${tc.signature("ast", "suffix", "methods")}
<#assign genHelper = glex.getGlobalValue("parserHelper")>
<#assign grammar = genHelper.getGrammarSymbol()>
<#assign parserName = grammar.getSimpleName()?cap_first>
<#assign startRule = genHelper.getStartRuleName()>
<#assign qualifiedStartRule = genHelper.getQualifiedStartRuleName()>
 
<#-- Copyright -->
${tc.defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getParserPackage()};

import java.io.IOException;
import java.io.Reader;

import java.util.Optional;
import de.monticore.ast.ASTNode;
import de.monticore.antlr4.MCConcreteParser;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;

public class ${ast.getName()}Parser${suffix} extends MCConcreteParser {

  protected ${parserName}AntlrParser create(String filename) throws IOException, RecognitionException {
    ${parserName}AntlrLexer lexer = new ${parserName}AntlrLexer(new  ANTLRFileStream(filename));
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    ${parserName}AntlrParser parser = new ${parserName}AntlrParser(tokens);
    lexer.setMCParser(parser);  
    parser.setFilename(filename);
    setError(false);
    return parser;
  }
  
  protected ${parserName}AntlrParser create(Reader reader) throws IOException, RecognitionException {
    ${parserName}AntlrLexer lexer = new ${parserName}AntlrLexer(new ANTLRInputStream(reader));
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    ${parserName}AntlrParser parser = new ${parserName}AntlrParser(tokens);
    lexer.setMCParser(parser);  
    parser.setFilename("Reader");
    setError(false);
    return parser;
  }
  
  @Override
  public Optional<${qualifiedStartRule}> parse(Reader reader) throws IOException, RecognitionException {
    return parse${startRule}(reader);
  }
  
  @Override
  public Optional<${qualifiedStartRule}> parse(String fileName) throws IOException, RecognitionException {
    return parse${startRule}(fileName);
  }
 
<#-- generate all methods -->
<#list methods as method>
  <#if genHelper.generateParserForRule(method)>
    ${tc.includeArgs("parser.MCParserMethods", [method])}
  </#if>
</#list>

}
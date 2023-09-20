package test;

import static org.junit.Assert.fail;

import java.io.StringReader;

import lexer.Lexer;

import org.junit.Test;

import parser.Parser;

public class ParserTests {
	private void runtest(String src) {
		runtest(src, true);
	}
	
	private void runFailTest(String src) {
		runtest(src, false);
	}

	private void runtest(String src, boolean succeed) {
		Parser parser = new Parser();
		try {
			parser.parse(new Lexer(new StringReader(src)));
			if(!succeed) {
				fail("Test was supposed to fail, but succeeded");
			}
		} catch (beaver.Parser.Exception e) {
			if(succeed) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		} catch (Throwable e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testEmptyModule() {
		runtest("module Test { }");
		runFailTest("module Test {");
		runFailTest("module Test }");
	}
	
	@Test
	public void testModuleImports() {
		runtest("module Test {"
				+ "import foo;"
				+ "import bar;"
				+ "}");
		
		runFailTest("module Test {"
				+ "import foo"
				+ "}");
	}
	
	@Test
	public void testModuleTypeDeclaration() {
		runtest("module Test {"
				+ "public type float = \"FLOAT\";"
				+ "}");
		
		runtest("module Test {"
				+ "type foo = \"ENUM\";"
				+ "}");
		
		// wrong type
		runFailTest("module Test {"
				+ "int foo = \"INT\";"
				+ "}");
		
		// missing assignment
		runFailTest("module Test {"
				+ "type foo;"
				+ "}");
		
		// wrong order with accessibility
		runFailTest("module Test {"
				+ "type public float = \"FLOAT\";"
				+ "}");
	}
	
	@Test
	public void testModuleFieldDeclaration() {
		runtest("module Test {"
				+ "public boolean booleanfield;"
				+ "int intfield;"
				+ "}");
		
		// wrong order
		runFailTest("module Test {"
				+ "boolean public booleanfield;"
				+ "}");
		
		// no assignment allowed
		runFailTest("module Test {"
				+ "int intfield = 3;"
				+ "}");
	}
	
	@Test
	public void testModuleEmptyFunctionDeclaration() {
		runtest("module Test {"
				+ "public void fun() {}"
				+ "}");
		
		runtest("module Test {"
				+ "void f123() {}"
				+ "}");
		
		// missing parenthesis
		runFailTest("module Test {"
				+ "void fun) {}"
				+ "}");
		
		// missing parenthesis
		runFailTest("module Test {"
				+ "void fun() {"
				+ "}");
		
		// extra parenthesis
		runFailTest("module Test {"
				+ "void fun()) {"
				+ "}");
		
		// no trailing semicolon for function declaration
		runFailTest("module Test {"
				+ "void fun() {};"
				+ "}");
	}
	
	@Test
	public void testModuleParameterFunctionDeclaration() {
		runtest("module Test {"
				+ "public void f(int param) {}"
				+ "}");
		
		// no accessibility specifier for params
		runFailTest("module Test {"
				+ "public void f(public int param) {}"
				+ "}");
		
		runFailTest("module Test {"
				+ "public void f(param) {}"
				+ "}");
	}
	
	@Test
	public void testModuleParameterListFunctionDeclaration() {
		runtest("module Test {"
				+ "public void fun(int param1, boolean param2) {}"
				+ "}");

		// we do not actually check for same param names
		runtest("module Test {"
				+ "public void fun(int param1, int param1) {}"
				+ "}");
		
		// missing comma separator
		runFailTest("module Test {"
				+ "public void fun(int param1 boolean param2) {}"
				+ "}");
		
		
		// missing type
		runFailTest("module Test {"
				+ "public void fun(int param1, param2) {}"
				+ "}");
	}
	
	@Test
	public void testModuleIf() {
		// we do not actually test if function return matches 
		runtest("module Test {"
				+ "public void fun() {"
				+ "  int x;"
				+ "  if(x < 10) {return 10;}"
				+ "  else {return;}"
				+ "}"
				+ "}");
		
		// empty if else
		runtest("module Test {"
				+ "public void fun() {"
				+ "  int x;"
				+ "  if(x < 10) {}"
				+ "  else {}"
				+ "}"
				+ "}");
		
		// no condition
		runFailTest("module Test {"
				+ "public void fun() {"
				+ "  int x;"
				+ "  if() {return 10;}"
				+ "  else {return;}"
				+ "}"
				+ "}");
		
		// if no else
		runtest("module Test {"
				+ "public void fun() {"
				+ "  int x;"
				+ "  if(x < 10) {return 10;}"
				+ "}"
				+ "}");
		
		// we do not actually test if variable is defined
		runtest("module Test {"
				+ "public void fun() {"
				+ "  if(x < 10) {return 10;}"
				+ "}"
				+ "}");
		
		// else, no if
		runFailTest("module Test {"
				+ "public void fun() {"
				+ "  else {return;}"
				+ "}"
				+ "}");
		
		// no else if
		runFailTest("module Test {"
				+ "public void fun() {"
				+ "  int x;"
				+ "  if(x < 10) {return 10;}"
				+ "  else if {return;}"
				+ "  else {return;}"
				+ "}"
				+ "}");
	}
	
	@Test
	public void testModuleWhileFunctionDeclaration() {
		// no test for infinite loop
		runtest("module Test {"
				+ "public void fun() {"
				+ "  int x;"
				+ "  while(x < 15) { x = 10; }"
				+ "}"
				+ "}");
		
		runtest("module Test {"
				+ "public void fun() {"
				+ "  int x;"
				+ "  while(true) {}"
				+ "}"
				+ "}");
		
		// no condition
		runFailTest("module Test {"
				+ "public void fun() {"
				+ "  int x;"
				+ "  while() { x = 10; }"
				+ "}"
				+ "}");
	}
	
	@Test
	public void testModuleArrayDeclarations() {
		runtest("module Test {"
				+ "public void fun() {"
				+ "  a = [1];"
				+ "}"
				+ "}");
		
		runtest("module Test {"
				+ "public void fun() {"
				+ "  a = [1, 2, 3];"
				+ "}"
				+ "}");
		
		runtest("module Test {"
				+ "public void fun() {"
				+ "  a[3] = [123];"
				+ "}"
				+ "}");
		
		runtest("module Test {"
				+ "public void fun() {"
				+ "  a = [1, [2,3,4], 3];"
				+ "}"
				+ "}");
		
		runtest("module Test {"
				+ "public void fun() {"
				+ "  a = [[2,3,4]];"
				+ "}"
				+ "}");
		
		runtest("module Test {"
				+ "public void fun() {"
				+ "  a = [2*3+4];"
				+ "}"
				+ "}");
		
		// array must be nonempty
		runFailTest("module Test {"
				+ "public void fun() {"
				+ "  a = [];"
				+ "}"
				+ "}");
		
		// array must be nonempty
		runFailTest("module Test {"
				+ "public void fun() {"
				+ "  a = 1,2,3];"
				+ "}"
				+ "}");
		
		// too many commas
		runFailTest("module Test {"
				+ "public void fun() {"
				+ "  a = [1,,2];"
				+ "}"
				+ "}");
		
		// missing commas
		runFailTest("module Test {"
				+ "public void fun() {"
				+ "  a = [1 2];"
				+ "}"
				+ "}");
	}
	
	@Test
	public void testModuleArrayAccess() {
		runtest("module Test {"
				+ "public void fun() {"
				+ "  a[3] = 4;"
				+ "}"
				+ "}");
		
		runtest("module Test {"
				+ "public void fun() {"
				+ "  a[3 * 2] = 4;"
				+ "}"
				+ "}");
		
		runtest("module Test {"
				+ "public void fun() {"
				+ "  a[3 * 4] = 4 * (5 + 6);"
				+ "}"
				+ "}");
		
		// no test for integer only array indices
		// this is a runtime error, not a compiler error
		runtest("module Test {"
				+ "public void fun() {"
				+ "  a[1/2] = 4 * (5 + 6);"
				+ "}"
				+ "}");
		
		runtest("module Test {"
				+ "public void fun() {"
				+ "  a[3 == 4] = 4;"
				+ "}"
				+ "}");
		
		// invalid expression
		runFailTest("module Test {"
				+ "public void fun() {"
				+ "  a[1//2] = 4 * (5 + 6);"
				+ "}"
				+ "}");
	}
	
	@Test
	public void testExpressions() {
		runtest("module Test {"
				+ "public void fun() {"
				+ "  a = 1;"
				+ "}"
				+ "}");
		
		runtest("module Test {"
				+ "public void fun() {"
				+ "  a = -1;"
				+ "}"
				+ "}");
		
		runtest("module Test {"
				+ "public void fun() {"
				+ "  a = 1 + 2;"
				+ "}"
				+ "}");
		
		runtest("module Test {"
				+ "public void fun() {"
				+ "  a = 1 + 2 * 3 - (4 + 5) * (67);"
				+ "}"
				+ "}");
		
		runtest("module Test {"
				+ "public void fun() {"
				+ "  a = a % 2;"
				+ "}"
				+ "}");
		
		runtest("module Test {"
				+ "public void fun() {"
				+ "  a = a == b;"
				+ "}"
				+ "}");
		
		runtest("module Test {"
				+ "public void fun() {"
				+ "  a = a >= b;"
				+ "}"
				+ "}");
		
		// wrong lhs and rhs
		runFailTest("module Test {"
				+ "public void fun() {"
				+ "  a >= b = a;"
				+ "}"
				+ "}");
		
		// invalid expression
		runFailTest("module Test {"
				+ "public void fun() {"
				+ "  a = 1 +;"
				+ "}"
				+ "}");
	}
}

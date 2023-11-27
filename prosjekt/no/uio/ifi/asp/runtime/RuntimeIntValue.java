package no.uio.ifi.asp.runtime;

import no.uio.ifi.asp.parser.AspSyntax;


public class RuntimeIntValue extends RuntimeValue {
	long intValue;
	
	public RuntimeIntValue(long v) {
		this.intValue = v;
	}
	
	@Override
	String typeName() {
		return "Integer";
	}
	
	@Override
	public String toString() {
		return Long.toString(this.intValue);
	}
	
	@Override
	public long getIntValue(String what, AspSyntax where) {
		return this.intValue;
	}
	
	@Override
	public boolean getBoolValue(String what, AspSyntax where) {
		return this.intValue != 0;
	}
	
	@Override
	public RuntimeValue evalAdd(RuntimeValue v, AspSyntax where) {
		
		// int + int
		if (v instanceof RuntimeIntValue)
			return new RuntimeIntValue(this.intValue + v.getIntValue("+", where));
		
		// int + float
		else if (v instanceof RuntimeFloatValue)
			return new RuntimeFloatValue((float) (this.intValue + v.getFloatValue("+", where)));
		
		else runtimeError("Type error for +.", where);
		 
		runtimeError("'+' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler
	}
	
	@Override
	public RuntimeValue evalDivide(RuntimeValue v, AspSyntax where) {
		
		// int / int
		if (v instanceof RuntimeIntValue)
			return new RuntimeFloatValue((float) this.intValue / v.getIntValue("/", where));
		
		// int / float
		else if (v instanceof RuntimeFloatValue)
			return new RuntimeFloatValue((float) (this.intValue / v.getFloatValue("/", where)));
		
		else runtimeError("Type error for /.", where);
		
		runtimeError("'/' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
	
	@Override
	public RuntimeValue evalEqual(RuntimeValue v, AspSyntax where) {
		
		// int == int
		if (v instanceof RuntimeIntValue)
			return new RuntimeBoolValue(this.intValue == v.getIntValue("==", where));
		
		// int == float
		else if (v instanceof RuntimeFloatValue)
			return new RuntimeBoolValue(this.intValue == v.getFloatValue("==", where));
		
		// int == none
		else if (v instanceof RuntimeNoneValue)
			return new RuntimeBoolValue(false);
		
		else runtimeError("Type error for ==.", where);
		
		runtimeError("'==' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
	
	@Override
	public RuntimeValue evalGreater(RuntimeValue v, AspSyntax where) {
		
		// int > int
		if (v instanceof RuntimeIntValue)
			return new RuntimeBoolValue(this.intValue > v.getIntValue(">", where));
		
		// int > float
		else if (v instanceof RuntimeFloatValue)
			return new RuntimeBoolValue(this.intValue > v.getFloatValue(">", where));
		
		else runtimeError("Type error for >.", where);
		
		runtimeError("'>' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
	
	@Override
	public RuntimeValue evalGreaterEqual(RuntimeValue v, AspSyntax where) {
		
		// int >= int
		if (v instanceof RuntimeIntValue)
			return new RuntimeBoolValue(this.intValue >= v.getIntValue(">=", where));
			
		// int >= float
		else if (v instanceof RuntimeFloatValue)
			return new RuntimeBoolValue(this.intValue >= v.getFloatValue(">=", where));
		
		else runtimeError("Type error for >=.", where);
		
		runtimeError("'>=' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
	
	@Override
	public RuntimeValue evalIntDivide(RuntimeValue v, AspSyntax where) {
		
		// int // int
		if (v instanceof RuntimeIntValue)
			return new RuntimeIntValue(Math.floorDiv(this.intValue, v.getIntValue("//", where)));
		
		// int // float
		else if (v instanceof RuntimeFloatValue)
			return new RuntimeFloatValue((float) Math.floor(this.intValue / v.getFloatValue("//", where)));
		
		else runtimeError("Type error for //.", where);
		
		runtimeError("'//' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
	
	@Override
	public RuntimeValue evalLess(RuntimeValue v, AspSyntax where) {
		
		// int < int
		if (v instanceof RuntimeIntValue)
			return new RuntimeBoolValue(this.intValue < v.getIntValue("<", where));
			
		// int < float
		else if (v instanceof RuntimeFloatValue)
			return new RuntimeBoolValue(this.intValue < v.getFloatValue("<", where));
		
		else runtimeError("Type error for <.", where);
		
		runtimeError("'<' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
	
	@Override
	public RuntimeValue evalLessEqual(RuntimeValue v, AspSyntax where) {
		
		// int <= int
		if (v instanceof RuntimeIntValue)
			return new RuntimeBoolValue(this.intValue <= v.getIntValue("<=", where));
			
		// int <= float
		else if (v instanceof RuntimeFloatValue)
			return new RuntimeBoolValue(this.intValue <= v.getFloatValue("<=", where));
		
		else runtimeError("Type error for <=.", where);
		
		runtimeError("'<=' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
	
	@Override
	public RuntimeValue evalModulo(RuntimeValue v, AspSyntax where) {
		
		// int % int              floorMod(a, b)
		if (v instanceof RuntimeIntValue)
			return new RuntimeIntValue(Math.floorMod(this.intValue, v.getIntValue("%", where)));
			
		// int % float             a-b * floor(a/b)
		else if (v instanceof RuntimeFloatValue)
			return new RuntimeFloatValue(
				(float) (this.intValue - v.getFloatValue("%", where)
							* Math.floor(this.intValue/v.getFloatValue("%", where)
			)));
		
		else runtimeError("Type error for %.", where);
		
		runtimeError("'%' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
	
	@Override
	public RuntimeValue evalMultiply(RuntimeValue v, AspSyntax where) {
		
		// int * int
		if (v instanceof RuntimeIntValue)
			return new RuntimeIntValue(this.intValue * v.getIntValue("*", where));
			
		// int * float
		else if (v instanceof RuntimeFloatValue)
			return new RuntimeFloatValue((float) (this.intValue * v.getFloatValue("*", where)));
		
		else runtimeError("Type error for *.", where);
		
		runtimeError("'*' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
	
	@Override
	public RuntimeValue evalNegate(AspSyntax where) {
		return new RuntimeIntValue(-this.intValue);
	}
	
	@Override
	public RuntimeValue evalNot(AspSyntax where) {
		return new RuntimeBoolValue(this.intValue==0);
	}
	
	@Override
	public RuntimeValue evalNotEqual(RuntimeValue v, AspSyntax where) {
		
		// int != int
		if (v instanceof RuntimeIntValue)
			return new RuntimeBoolValue(this.intValue != v.getIntValue("!=", where));
			
		// int != float
		else if (v instanceof RuntimeFloatValue)
			return new RuntimeBoolValue(this.intValue != v.getFloatValue("!=", where));
		
		else runtimeError("Type error for !=.", where);
		
		runtimeError("'!=' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
	
	@Override
	public RuntimeValue evalPositive(AspSyntax where) {
		return new RuntimeIntValue(this.intValue);
	}
	
	@Override
	public RuntimeValue evalSubtract(RuntimeValue v, AspSyntax where) {
		
		// int - int
		if (v instanceof RuntimeIntValue)
			return new RuntimeIntValue(this.intValue - v.getIntValue("-", where));
		
		// int - float
		else if (v instanceof RuntimeFloatValue)
			return new RuntimeFloatValue((float) (this.intValue - v.getFloatValue("-", where)));
		
		else runtimeError("Type error for -.", where);
		
		runtimeError("'-' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
}

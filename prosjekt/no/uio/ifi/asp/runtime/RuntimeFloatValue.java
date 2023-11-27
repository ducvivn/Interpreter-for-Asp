package no.uio.ifi.asp.runtime;

import no.uio.ifi.asp.parser.AspSyntax;


public class RuntimeFloatValue extends RuntimeValue {
	double floatValue;
	
	public RuntimeFloatValue(double v) {
		this.floatValue = v;
	}
	
	@Override
	String typeName() {
		return "Float";
	}
	
	@Override
	public String toString() {
		return Double.toString(floatValue);
	}
	
	@Override
	public boolean getBoolValue(String what, AspSyntax where) {
		return floatValue != 0.0;
	}
	
	@Override
	public double getFloatValue(String what, AspSyntax where) {
		return floatValue;
	}
	
	@Override
	public RuntimeValue evalAdd(RuntimeValue v, AspSyntax where) {
		
		// float + float
		if (v instanceof RuntimeFloatValue)
			return new RuntimeFloatValue((float) (this.floatValue + v.getFloatValue("+", where)));
			
		// float + int
		else if (v instanceof RuntimeIntValue)
			return new RuntimeFloatValue((float) (this.floatValue + v.getIntValue("+", where)));
		
		else runtimeError("Type error for +.", where);
		
		runtimeError("'+' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler
	}
	
	@Override
	public RuntimeValue evalDivide(RuntimeValue v, AspSyntax where) {
		
		// float / float
		if (v instanceof RuntimeFloatValue)
			return new RuntimeFloatValue((float) (this.floatValue / v.getFloatValue("/", where)));
			
		// float / int
		else if (v instanceof RuntimeIntValue)
			return new RuntimeFloatValue((float) (this.floatValue / v.getIntValue("/", where)));
		
		else runtimeError("Type error for /.", where);
		
		runtimeError("'/' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
	
	@Override
	public RuntimeValue evalEqual(RuntimeValue v, AspSyntax where) {
		
		// float == float
		if (v instanceof RuntimeFloatValue)
			return new RuntimeBoolValue(this.floatValue == v.getFloatValue("==", where));
		
		// float == int
		else if (v instanceof RuntimeIntValue)
			return new RuntimeBoolValue(this.floatValue == v.getIntValue("==", where));
		
		// float == none
		else if (v instanceof RuntimeNoneValue)
			return new RuntimeBoolValue(false);
		
		else runtimeError("Type error for ==.", where);
		
		runtimeError("'==' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
	
	@Override
	public RuntimeValue evalGreater(RuntimeValue v, AspSyntax where) {
		
		// float > int
		if (v instanceof RuntimeIntValue)
			return new RuntimeBoolValue(this.floatValue > v.getIntValue(">", where));
			
		// float > float
		else if (v instanceof RuntimeFloatValue)
			return new RuntimeBoolValue(this.floatValue > v.getFloatValue(">", where));
		
		else runtimeError("Type error for >.", where);
		
		runtimeError("'>' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
	
	@Override
	public RuntimeValue evalGreaterEqual(RuntimeValue v, AspSyntax where) {
		
		// float >= int
		if (v instanceof RuntimeIntValue)
			return new RuntimeBoolValue(this.floatValue >= v.getIntValue(">=", where));
			
		// float >= float
		else if (v instanceof RuntimeFloatValue)
			return new RuntimeBoolValue(this.floatValue >= v.getFloatValue(">=", where));
		
		else runtimeError("Type error for >=.", where);
		
		runtimeError("'>=' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
	
	@Override
	public RuntimeValue evalIntDivide(RuntimeValue v, AspSyntax where) {
		
		// float // int
		if (v instanceof RuntimeIntValue)
			return new RuntimeFloatValue((float) Math.floor(this.floatValue / v.getIntValue("//", where)));
			
		// float // float
		else if (v instanceof RuntimeFloatValue)
			return new RuntimeFloatValue((float) Math.floor(this.floatValue / v.getFloatValue("//", where)));
		
		else runtimeError("Type error for //.", where);
		
		runtimeError("'//' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
	
	@Override
	public RuntimeValue evalLess(RuntimeValue v, AspSyntax where) {
		
		// float < int
		if (v instanceof RuntimeIntValue)
			return new RuntimeBoolValue(this.floatValue < v.getIntValue("<", where));
			
		// float < float
		else if (v instanceof RuntimeFloatValue)
			return new RuntimeBoolValue(this.floatValue < v.getFloatValue("<", where));
		
		else runtimeError("Type error for <.", where);
		
		runtimeError("'<' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
	
	@Override
	public RuntimeValue evalLessEqual(RuntimeValue v, AspSyntax where) {
		
		// float <= int
		if (v instanceof RuntimeIntValue)
			return new RuntimeBoolValue(this.floatValue <= v.getIntValue("<=", where));
			
		// float <= float
		else if (v instanceof RuntimeFloatValue)
			return new RuntimeBoolValue(this.floatValue <= v.getFloatValue("<=", where));
		
		else runtimeError("Type error for <=.", where);
		
		runtimeError("'<=' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
	
	@Override
	public RuntimeValue evalModulo(RuntimeValue v, AspSyntax where) {
		
		// float % int              a-b * floor(a/b)
		if (v instanceof RuntimeIntValue)
			return new RuntimeFloatValue(
					(float) (this.floatValue - v.getIntValue("%", where)
							     * Math.floor(this.floatValue/v.getIntValue("%", where)
			)));
		
		// float % float             a-b * floor(a/b)
		else if (v instanceof RuntimeFloatValue)
			return new RuntimeFloatValue(
					(float) (this.floatValue - v.getFloatValue("%", where)
							     * Math.floor(this.floatValue/v.getFloatValue("%", where)
			)));
		
		else runtimeError("Type error for %.", where);
		
		runtimeError("'%' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
	
	@Override
	public RuntimeValue evalMultiply(RuntimeValue v, AspSyntax where) {
		
		// float * int
		if (v instanceof RuntimeIntValue)
			return new RuntimeFloatValue((float) (this.floatValue * v.getIntValue("*", where)));
			
		// float * float
		else if (v instanceof RuntimeFloatValue)
			return new RuntimeFloatValue((float) (this.floatValue * v.getFloatValue("*", where)));
		
		else runtimeError("Type error for *.", where);
		
		runtimeError("'*' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
	
	@Override
	public RuntimeValue evalNegate(AspSyntax where) {
		return new RuntimeFloatValue((float) -this.floatValue);
	}
	
	@Override
	public RuntimeValue evalNot(AspSyntax where) {
		return new RuntimeBoolValue(this.floatValue==0.0);
	}
	
	@Override
	public RuntimeValue evalNotEqual(RuntimeValue v, AspSyntax where) {
		
		// float != int
		if (v instanceof RuntimeIntValue)
			return new RuntimeBoolValue(this.floatValue != v.getIntValue("!=", where));
			
		// float != float
		else if (v instanceof RuntimeFloatValue)
			return new RuntimeBoolValue(this.floatValue != v.getFloatValue("!=", where));
		
		else runtimeError("Type error for !=.", where);
		
		runtimeError("'!=' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
	
	@Override
	public RuntimeValue evalPositive(AspSyntax where) {
		return new RuntimeFloatValue((float) this.floatValue);
	}
	
	@Override
	public RuntimeValue evalSubtract(RuntimeValue v, AspSyntax where) {
		
		// float - int
		if (v instanceof RuntimeIntValue)
			return new RuntimeIntValue((long) (this.floatValue - v.getIntValue("-", where)));
			
		// float - float
		else if (v instanceof RuntimeFloatValue)
			return new RuntimeFloatValue((float) (this.floatValue - v.getFloatValue("-", where)));
		
		else runtimeError("Type error for -.", where);
		
		runtimeError("'-' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
}
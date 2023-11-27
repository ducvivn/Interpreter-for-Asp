package no.uio.ifi.asp.runtime;

import no.uio.ifi.asp.parser.AspSyntax;


public class RuntimeStringValue extends RuntimeValue {
	String strValue;
	
	public RuntimeStringValue(String v) {
		this.strValue = v;
	}
	
	@Override
	String typeName() {
		return "String";
	}
	
	@Override
	public String toString() {
		return strValue;
	}
	
	@Override
	public String showInfo() {
		if (strValue.indexOf('\'') >= 0)
			return '"' + strValue + '"';
		else
			return "'" + strValue + "'";
	}
	
	@Override
	public boolean getBoolValue(String what, AspSyntax where) {
		return !strValue.isEmpty();
	}
	
	@Override
	public String getStringValue(String what, AspSyntax where) {
		return strValue;
	}
	
	@Override
	public RuntimeValue evalAdd(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeStringValue)
			return new RuntimeStringValue(this.strValue+v.getStringValue("+", where));
		
		else runtimeError("Type error for +.", where);
		
		runtimeError("'+' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler
	}
	
	@Override
	public RuntimeValue evalEqual(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeStringValue)
			return new RuntimeBoolValue(this.strValue.equals(v.getStringValue("==", where)));
		
		else if (v instanceof RuntimeNoneValue)
			return new RuntimeBoolValue(false);
		
		else runtimeError("Type error for ==.", where);
		
		runtimeError("'==' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
	
	@Override
	public RuntimeValue evalGreater(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeStringValue)
			return new RuntimeBoolValue(this.strValue.compareTo(v.getStringValue(">", where)) > 0);
		
		else runtimeError("Type error for >.", where);
		
		runtimeError("'>' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
	
	@Override
	public RuntimeValue evalGreaterEqual(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeStringValue)
			return new RuntimeBoolValue(this.strValue.compareTo(v.getStringValue(">=", where)) >= 0);
		
		else runtimeError("Type error for >=.", where);
		
		runtimeError("'>=' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
	
	@Override
	public RuntimeValue evalLen(AspSyntax where) {
		return new RuntimeIntValue(this.strValue.length());
	}
	
	@Override
	public RuntimeValue evalLess(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeStringValue)
			return new RuntimeBoolValue(this.strValue.compareTo(v.getStringValue("<", where)) < 0);
		
		else runtimeError("Type error for <.", where);
		
		runtimeError("'<' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
	
	@Override
	public RuntimeValue evalLessEqual(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeStringValue)
			return new RuntimeBoolValue(this.strValue.compareTo(v.getStringValue("<=", where)) <= 0);
		
		else runtimeError("Type error for <=.", where);
		
		runtimeError("'<=' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
	
	@Override
	public RuntimeValue evalMultiply(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeIntValue) {
			long n = v.getIntValue("*", where);
			String newString = new String(new char[(int) n]).replace("\0", this.strValue);
			return new RuntimeStringValue(newString);
		}
		else runtimeError("Type error for *.", where);
		
		runtimeError("'*' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
	
	@Override
	public RuntimeValue evalNot(AspSyntax where) {
		return new RuntimeBoolValue(strValue.isEmpty());
	}
	
	@Override
	public RuntimeValue evalNotEqual(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeStringValue)
			return new RuntimeBoolValue(!(this.strValue.equals(v.getStringValue("==", where))));
		
		// String != None = true
		else if (v instanceof RuntimeNoneValue)
			return new RuntimeBoolValue(true);
		
		else runtimeError("Type error for !=.", where);
		
		runtimeError("'!=' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
	
	@Override
	public RuntimeValue evalSubscription(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeIntValue) {
			long index = v.getIntValue("[]", where);
			char c = this.strValue.charAt((int) index);
			return new RuntimeStringValue(Character.toString(c));
		}
		
		runtimeError("Subscription '[...]' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
}

package no.uio.ifi.asp.runtime;

import no.uio.ifi.asp.parser.AspSyntax;
import java.util.HashMap;


public class RuntimeDictValue extends RuntimeValue {
	
	HashMap<RuntimeStringValue, RuntimeValue> dict;
	
	public RuntimeDictValue(HashMap<RuntimeStringValue, RuntimeValue> dict) {
		this.dict = dict;
	}
	
	@Override
	String typeName() {
		return "Dictionary";
	}
	
	@Override
	public String showInfo() {
		return this.dict.toString();
	}
	
	@Override
	public String toString() {
		return this.dict.toString();
	}
	
	@Override
	public boolean getBoolValue(String what, AspSyntax where) {
		return !this.dict.isEmpty();
	}
	
	@Override
	public RuntimeValue evalEqual(RuntimeValue v, AspSyntax where) {
		
		// dict == none
		if (v instanceof RuntimeNoneValue)
			return new RuntimeBoolValue(false);
		
		else runtimeError("Type error for ==.", where);
		
		runtimeError("'==' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
	
	@Override
	public RuntimeValue evalLen(AspSyntax where) {
		return new RuntimeIntValue(this.dict.size());
	}
	
	@Override
	public RuntimeValue evalNot(AspSyntax where) {
		return new RuntimeBoolValue(this.dict.isEmpty());
	}
	
	@Override
	public RuntimeValue evalNotEqual(RuntimeValue v, AspSyntax where) {
		
		// dict != none
		if (v instanceof RuntimeNoneValue)
			return new RuntimeBoolValue(true);
		
		else runtimeError("Type error for !=.", where);
		
		runtimeError("'!=' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
	
	@Override
	public RuntimeValue evalSubscription(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeStringValue) {
			String compKey = v.getStringValue("[]", where);
			for (RuntimeStringValue key : this.dict.keySet()) {
				String stringKey = key.getStringValue("[]", where);
				
				if (stringKey.equals(compKey)) {
					return this.dict.get(key);
				}
			}
			runtimeError("Dictionary key '"+compKey+"' undefined!", where);
		} else runtimeError("A dictionary index must be a string!", where);
		
		runtimeError("Subscription '[...]' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
	
	@Override
	public void evalAssignElem(RuntimeValue inx, RuntimeValue val, AspSyntax where) {
		if (inx instanceof RuntimeStringValue)
			this.dict.put((RuntimeStringValue) inx, val);
		
		runtimeError("Assigning to an element not allowed for " + typeName() + "!", where);
	}
}

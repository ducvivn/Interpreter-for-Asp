package no.uio.ifi.asp.runtime;

import no.uio.ifi.asp.parser.AspFuncDef;
import no.uio.ifi.asp.parser.AspSyntax;

import java.util.ArrayList;


public class RuntimeFunc extends RuntimeValue {
	ArrayList<RuntimeValue> params;
	AspFuncDef def;
	RuntimeScope defScope;
	String name;
	
	public RuntimeFunc(String name) {
		this.name = name;
	}
	
	public RuntimeFunc(AspFuncDef parFunc, RuntimeScope parScope, String parName, ArrayList<RuntimeValue> parParams) {
		this.def = parFunc;
		this.defScope = parScope;
		this.name = parName;
		this.params = parParams;
	}
	
	@Override
	public RuntimeValue evalFuncCall(ArrayList<RuntimeValue> actualParams, AspSyntax where) {
		
		// (3) sjekker antall aktuelle er samme som formelle
		if (actualParams.size() == this.params.size()) {
			
			// (4) oppretter nytt skop med funksjonens skop
			RuntimeScope newScope = new RuntimeScope(defScope);
			
			// (5) går gjennom parametre og tilordner aktuelle til formelle
			for (int i=0; i < actualParams.size(); i++) {
				RuntimeValue actual = actualParams.get(i);
				String formal = this.params.get(i).getStringValue("evalFuncCall", where);
				
				newScope.assign(formal, actual);
			}
			// (6) forsøker å evaluere funksjonens innmat med nytt scope
			// fra forelesning uke 45 side 32
			try {
				def.func_suite.eval(newScope);
			} catch (RuntimeReturnValue rrv) {
				return rrv.value;
			}
		}
		else runtimeError("Number of arguments invalid", where);
		
		return new RuntimeNoneValue();
	}
	
	@Override
	String typeName() {
		return "function";
	}
}

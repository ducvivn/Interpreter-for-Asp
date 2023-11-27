// © 2021 Dag Langmyhr, Institutt for informatikk, Universitetet i Oslo

package no.uio.ifi.asp.runtime;

import no.uio.ifi.asp.parser.AspSyntax;
import java.util.ArrayList;
import java.util.Scanner;


public class RuntimeLibrary extends RuntimeScope {
	private Scanner keyboard = new Scanner(System.in);
	
	public RuntimeLibrary() {
		
		// float
		assign("float", new RuntimeFunc("float") {
			@Override
			public RuntimeValue evalFuncCall(ArrayList<RuntimeValue> actualParams, AspSyntax where) {
				checkNumParams(actualParams, 1, "float", where);
				RuntimeValue value = actualParams.get(0);
				double returnValue = 0;
				
				// kun lovlige typer
				try {
					if (value instanceof RuntimeIntValue)
						returnValue = (double) value.getIntValue("int", where);
					else if (value instanceof RuntimeFloatValue)
						returnValue = value.getFloatValue("int", where);
					else if (value instanceof RuntimeStringValue)
						returnValue = Double.parseDouble(value.getStringValue("int", where));
					else
						runtimeError("Type error: " + value + " is not a legal float value!", where);
				}
				catch (Exception e) {
					runtimeError( actualParams.get(0).typeName() + " "+ actualParams.get(0).toString() + " is not a legal float value!", where);
				}
				return new RuntimeFloatValue(returnValue);
			}});
		
		// input
		assign("input", new RuntimeFunc("input") {
			@Override
			public RuntimeValue evalFuncCall(ArrayList<RuntimeValue> actualParams, AspSyntax where) {
				checkNumParams(actualParams, 1, "input", where);
				String s = actualParams.get(0).getStringValue("input", where);
				System.out.print(s);
				return new RuntimeStringValue(keyboard.nextLine());
			}});
		
		// int
		assign("int", new RuntimeFunc("int") {
			@Override
			public RuntimeValue evalFuncCall(ArrayList<RuntimeValue> actualParams, AspSyntax where) {
				checkNumParams(actualParams, 1, "int", where);
				RuntimeValue value = actualParams.get(0);
				long returnValue = 0;
				
				// kun lovlige typer
				try {
					if (value instanceof RuntimeIntValue)
						returnValue = value.getIntValue("int", where);
					else if (value instanceof RuntimeFloatValue)
						returnValue = (long) value.getFloatValue("int", where);
					else if (value instanceof RuntimeStringValue)
						returnValue = Long.parseLong(value.getStringValue("int", where));
					else
						runtimeError("Type error: " + value + " is not a legal int value!", where);
				}
				catch (Exception e) {
					runtimeError( actualParams.get(0).typeName() + " "+ actualParams.get(0).toString() + " is not a legal int value!", where);
				}
				return new RuntimeIntValue(returnValue);
			}});
		
		// len (fra kompendiet)
		assign("len", new RuntimeFunc("len") {
			@Override
			public RuntimeValue evalFuncCall(ArrayList<RuntimeValue> actualParams, AspSyntax where) {
				checkNumParams(actualParams, 1, "len", where);
				RuntimeValue value = actualParams.get(0);
				
				// kun lovlige typer
				if (!(value instanceof RuntimeStringValue ||
					  value instanceof RuntimeDictValue ||
					  value instanceof RuntimeListValue))
					runtimeError("illegal type for len", where);
				
				return actualParams.get(0).evalLen(where);
			}});

		// print (fra forelesning uke 45 side 39)
		assign("print", new RuntimeFunc("print") {
			@Override
			public RuntimeValue evalFuncCall(ArrayList<RuntimeValue> actualParams, AspSyntax where) {
				for (int i = 0; i < actualParams.size(); ++i) {
					if (i > 0) System.out.print(" ");
					System.out.print(actualParams.get(i).toString());
				}
				System.out.println();
				return new RuntimeNoneValue();
			}});
		
		// range
		assign("range", new RuntimeFunc("range") {
			@Override
			public RuntimeValue evalFuncCall(ArrayList<RuntimeValue> actualParams, AspSyntax where) {
				checkNumParams(actualParams, 2, "range", where);
				RuntimeValue fromVal = actualParams.get(0);
				RuntimeValue toVal = actualParams.get(1);
				
				// kun lovlige typer
				if (!(fromVal instanceof RuntimeIntValue)) runtimeError("Type error: illegal type " + fromVal + " for range start", where);
				if (!(toVal instanceof RuntimeIntValue)) runtimeError("Type error: illegal type " + toVal + " for range end", where);
				
				int from = (int) actualParams.get(0).getIntValue("range", where);
				int to = (int) actualParams.get(1).getIntValue("range", where);
				RuntimeListValue range =  new RuntimeListValue(new ArrayList<RuntimeValue>());
				
				// lager range-listen
				for(int i = from; i < to; i++)
					range.add(new RuntimeIntValue(i));
				return range;
			
			}});
		
		// str
		assign("str", new RuntimeFunc("str") {
			@Override
			public RuntimeValue evalFuncCall(ArrayList<RuntimeValue> actualParams, AspSyntax where) {
				checkNumParams(actualParams, 1, "str", where);
				return new RuntimeStringValue(actualParams.get(0).toString());
			}});
	}
	
	
	private void checkNumParams(ArrayList<RuntimeValue> actArgs,
								int nCorrect, String id, AspSyntax where) {
		if (actArgs.size() != nCorrect)
			RuntimeValue.runtimeError("Wrong number of parameters to " + id + "!", where);
	}
}

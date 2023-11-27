package no.uio.ifi.asp.runtime;
import no.uio.ifi.asp.parser.AspSyntax;
import java.util.ArrayList;


public class RuntimeListValue extends RuntimeValue {
	ArrayList<RuntimeValue> list;
	
	public RuntimeListValue(ArrayList<RuntimeValue> v) {
		this.list = v;
	}
	
	@Override
	String typeName() {
		return "List";
	}
	
	@Override
	public String showInfo() {
		return this.list.toString();
	}
	
	@Override
	public String toString() {
		return this.list.toString();
	}
	
	public ArrayList<RuntimeValue> getList() {
		return this.list;
	}

	@Override
	public boolean getBoolValue(String what, AspSyntax where) {
		return !list.isEmpty();
	}
	
	@Override
	public RuntimeValue evalEqual(RuntimeValue v, AspSyntax where) {
		
		// list == none
		if (v instanceof RuntimeNoneValue)
			return new RuntimeBoolValue(false);
		
		else runtimeError("Type error for ==.", where);
		
		runtimeError("'==' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
	
	@Override
	public RuntimeValue evalLen(AspSyntax where) {
		return new RuntimeIntValue(this.list.size());
	}
	
	@Override
	public RuntimeValue evalMultiply(RuntimeValue v, AspSyntax where) {
		
		// list * int
		if (v instanceof RuntimeIntValue) {
			ArrayList<RuntimeValue> newList = new ArrayList<>();
			
			for (int i=0; i < v.getIntValue("*", where); i++)
				newList.addAll(this.list);
			
			return new RuntimeListValue(newList);
		}
		else runtimeError("Type error for *.", where);
		
		runtimeError("'*' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
	
	@Override
	public RuntimeValue evalNot(AspSyntax where) {
		return new RuntimeBoolValue(this.list.isEmpty());
	}
	
	@Override
	public RuntimeValue evalNotEqual(RuntimeValue v, AspSyntax where) {
		
		// list != none
		if (v instanceof RuntimeNoneValue)
			return new RuntimeBoolValue(true);
		
		else runtimeError("Type error for +.", where);
		
		runtimeError("'!=' undefined for " + typeName() + "!", where);
		return null;  // Required by the compiler!
	}
	
	@Override
	public RuntimeValue evalSubscription(RuntimeValue v, AspSyntax where) {
		if (v instanceof RuntimeIntValue) {
			int indx = (int) v.getIntValue("[]", where);
			
			// godkjent index
			if (indx >= 0 && indx < this.list.size())
				return this.list.get((int) v.getIntValue("[]", where));
			else
				runtimeError("List index "+indx+" out of range for list-size "+this.list.size()+"!", where);
		}
		else
			runtimeError("Subscription '[...]' undefined for " + typeName() + "!", where);
		
		return null;  // Required by the compiler!
	}
	
	@Override
	public void evalAssignElem(RuntimeValue inx, RuntimeValue val, AspSyntax where) {
		if (inx instanceof RuntimeIntValue) {
			int indx = (int) inx.getIntValue("Assignment", where);
			
			// godkjent index
			if (indx >= 0 && indx < this.list.size())
				this.list.set(indx, val);
			else
				runtimeError("list assignment index out of range", where);
		}
		else runtimeError("Assigning to an element not allowed for " + typeName() + "!", where);
	}
	
	public void add(RuntimeValue v) {
		this.list.add(v);
	}
}

package com.concurnas.compiler;

import java.util.ArrayList;

import com.concurnas.compiler.ast.Assign;
import com.concurnas.compiler.ast.REPLTopLevelComponent;
import com.concurnas.compiler.visitors.WarningVariant;


public class ErrorHolder implements Comparable<ErrorHolder> {
	private final String filename;
	private final int line;
	private final int column;
	private final String message;
	private final WarningVariant wv;
	private final ArrayList<REPLTopLevelComponent> context;

	public ErrorHolder(String filename, int line, int column, String message, WarningVariant wv, ArrayList<REPLTopLevelComponent> context)
	{
		this.filename = filename==null?"":filename;
		this.line = line;
		this.column = column;
		this.message = message;
		this.wv = wv;
		this.context = context;
		/*
		 * if(context != null) { context.setErrors(true); }
		 */
	}
	
	public ErrorHolder(String filename, int line, int column, String message){
		this(filename, line, column, message, null, null);
	}
	
	public ErrorHolder copyWithErPrefix(String prefix) {
		return new ErrorHolder(filename, line, column, "in " + prefix + " - " + message);
	}

	public String getFilename() {
		return filename;
	}

	public int getLine() {
		return line;
	}

	public int getColumn() {
		return column;
	}

	public String getMessage() {
		return message;
	}
	
	public boolean isWarning() {
		return wv != null;
	}
	
	public WarningVariant getWarning(){
		return wv;
	}
	
	public boolean hasContext(){
		return context != null && context.stream().noneMatch(a -> a instanceof Assign);
	}
	
	public boolean getAnyContextHavingErrSupression(){
		return this.context != null && this.context.stream().anyMatch(a -> a.getSupressErrors());
	}
	
	public REPLTopLevelComponent getHeadContext() {
		return this.context == null || this.context.isEmpty()?null:this.context.get(0);
	}
	
	@Override
	public int hashCode(){
		return (this.filename == null? 0: this.filename.hashCode()) + this.line + this.column + this.message.hashCode();
	}

	@Override
	public String toString(){
		return String.format("%s %s", this.filename , toStringNoFileName());
	}
	
	public String toStringNoFileName(){
		return String.format("%sline %s:%s %s", (wv != null)?"WARN ":"", this.line , this.column , this.message);
	}


	@Override
	public int compareTo(ErrorHolder o) {
		//negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
		if(this.isWarning() != o.isWarning()){
			return ((this.isWarning()?1:0) - (o.isWarning()?1:0));
		}
		else if(!this.filename.equals(o.filename)){
			return this.filename.compareTo(o.filename);
		}
		else if(this.line != o.line){
			return this.line - o.line;
		}
		else if(this.column != o.column){
			return this.column - o.column;
		}
		else{
			return this.message.compareTo(o.message);
		}
	}
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof ErrorHolder)
		{
			return 0==compareTo((ErrorHolder)o);
		}
		return false;
	}
	
}

package org.marketcetera.photon.db;

import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class SQLTableSorter extends ViewerSorter {
	int count;
	public final static int ASCENDING = 1;
	public  final static int DEFAULT_DIRECTION = 0;
	public  final static int DESCENDING = -1;
	ResultSetMetaData metaData;
	public SQLTableSorter(int count,ResultSetMetaData metaData){
		this.count=count;
		this.metaData=metaData;
		priorities=new int[count];
		directions = new int[count];
		DEFAULT_DIRECTIONS=new int[count];
		DEFAULT_PRIORITIES=new int[count];
		sqlTypeArray=new int[count];
		for(int i=0;i<count;i++){
			DEFAULT_DIRECTIONS[i]=ASCENDING;
			DEFAULT_PRIORITIES[i]=i;
			try{
				sqlTypeArray[i]=metaData.getColumnType(i+1);
			}catch(Throwable e){
				sqlTypeArray[i]=Types.VARCHAR;
			}
			
		}
		
		resetState();
	}
	protected int[] priorities;
	protected int[] directions;
	protected int[] DEFAULT_DIRECTIONS; 
	protected int[] DEFAULT_PRIORITIES;
	protected int sqlTypeArray[];

	/**
	 * @param column
	 */
	public void setTopPriority(int priority) {
		
		if (priority < 0 || priority >= priorities.length)
			return;
		
		int index = -1;
		for (int i = 0; i < priorities.length; i++) {
			if (priorities[i] == priority) {
				index = i;
				break;
			}
		}
		
		if (index == -1) {
			resetState();
			return;
		}
			
		//shift the array
		for (int i = index; i > 0; i--) {
			priorities[i] = priorities[i - 1];
		}
		priorities[0] = priority;
		directions[priority] = DEFAULT_DIRECTIONS[priority];
	}
	public void resetState() {
		priorities = DEFAULT_PRIORITIES;
		directions = DEFAULT_DIRECTIONS;
	}

	/**
	 * 
	 */
	public int reverseTopPriority() {
		directions[priorities[0]] *= -1;
		return directions[priorities[0]];
	}

	/**
	 * @return int
	 */
	public int getTopPriority() {
		return priorities[0];
	}
	public int compare(Viewer viewer, Object e1, Object e2) {
		return compareColumnValue((SQLTableRow)e1, (SQLTableRow)e2, 0);
	}
	
	private int compareColumnValue(SQLTableRow m1, SQLTableRow m2, int depth) {
		if (depth >= priorities.length)
			return 0;
	
		int columnNumber = priorities[depth];
		int direction = directions[columnNumber];
		int result=0;
		String v1=m1.getValue(columnNumber).toString();
		String v2=m2.getValue(columnNumber).toString();

		switch(sqlTypeArray[columnNumber]){
			case Types.CHAR :
			case Types.VARCHAR :
			case Types.LONGVARCHAR :
			case -9 :
				result = collator.compare(m1.getValue(columnNumber), m2.getValue(columnNumber));
				break;	
			case Types.DECIMAL:
			case Types.NUMERIC:
			case Types.DOUBLE:
			case Types.FLOAT:
			case Types.REAL:
				double d1=0;
				double d2=0;
				try{
					d1=Double.parseDouble(v1);
				}
				catch(Exception e){
				}
				try{
					d2=Double.parseDouble(v2);
				}catch(Exception e){
				}
				if(d1==d2)
					result=0;
				else if(d1>d2)
					result=1;
				else
					result=-1;
				break;
			case Types.INTEGER:
			case Types.SMALLINT:
			case Types.TINYINT:
			case Types.BIGINT :
				v1=m1.getValue(columnNumber).toString();
				v2=m2.getValue(columnNumber).toString();
				
				long l1=0;
				long l2=0;
				try{
					l1=Long.parseLong(v1);
				}
				catch(Exception e){
				}
				try{
					l2=Long.parseLong(v2);
				}catch(Exception e){
				}
				if(l1==l2)
					result=0;
				else if(l1>l2)
					result=1;
				else
					result=-1;
				break;
			case Types.DATE :
				try{
					Date dt1=(Date)m1.getInternalValue(columnNumber);
					Date dt2=(Date)m2.getInternalValue(columnNumber);
					if(dt1==null && dt2==null)
						result=0;
					if(dt2==null)
						result=1;
					else if (dt1==null)
						result=-1;
					else
						result=dt1.compareTo(dt2);
				}catch(Exception e){
					
				}
				
				break;
			case Types.TIMESTAMP:
				try{
					Timestamp t1=(Timestamp)m1.getInternalValue(columnNumber);
					Timestamp t2=(Timestamp)m2.getInternalValue(columnNumber);
					if(t1==null && t2==null)
						result=0;
					if(t2==null)
						result=1;
					else if (t1==null)
						result=-1;
					else
						result=t1.compareTo(t2);
				}catch(Exception e){
					
				}
				break;
			case Types.TIME:
				try{
					Time t11=(Time)m1.getInternalValue(columnNumber);
					Time t22=(Time)m2.getInternalValue(columnNumber);
					if(t11==null && t22==null)
						result=0;
					if(t22==null)
						result=1;
					else if (t11==null)
						result=-1;
					else
						result=t11.compareTo(t22);
				}catch(Exception e){
					
				}
				break;
		}
		
		if (result == 0)
			return compareColumnValue(m1, m2, depth + 1);
		return result * direction;
	}

}
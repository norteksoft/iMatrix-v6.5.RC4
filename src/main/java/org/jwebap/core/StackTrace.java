package org.jwebap.core;

import java.util.ArrayList;

/**
 * 可进行堆栈跟踪的轨迹
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.6
 * @date  2009-12-8
 */
public class StackTrace extends Trace
{

    protected Exception traceException;

    public StackTrace()
    {
        traceException = new Exception();
    }

    public StackTrace(Trace parent) {
		super(parent);
	}
    
    public void setStack()
    {
        traceException = new Exception();
    }
	
    public StackTraceElement[] getStackTraces()
    {
        if(traceException != null)
            return traceException.getStackTrace();
        else
            return new StackTraceElement[0];
    }

    public String getStackTracesDetail()
    {
        return getStackTracesDetail(0, getStackTraces().length);
    }

    public String getStackTracesDetail(int end)
    {
        return getStackTracesDetail(0, end);
    }

    public String getStackTracesDetail(int begin, int end)
    {
        String log = "";
        StackTraceElement traces[] = getStackTraces();
        for(int i = begin; i < traces.length && i < end; i++)
            log = log + traces[i].toString() + "\n";

        return log;
    }
}

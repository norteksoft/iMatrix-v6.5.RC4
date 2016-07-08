package org.jwebap.core;


public class StatistableTrace extends StackTrace
{	
	
    public static class InnerKey
        implements TraceKey
    {
    	private final static ThreadLocal threadKeys = new ThreadLocal();
        private Object _invokeKey;
        private Object _threadKey;

        public Object getInvokeKey()
        {
            return _invokeKey;
        }
        
        public static void  clearThreadKey(){
        	threadKeys.set(null);
        }
        
        //生成线程ID
        public Object genThreadKey()
        {
            if(threadKeys.get() == null)
            {
                String name = Thread.currentThread().getName() + "#" + String.valueOf(hashCode());
                threadKeys.set(name);
            }
            return threadKeys.get();
        }

        public Object getThreadKey()
        {
            return _threadKey;
        }
        
        public InnerKey()
        {
            _invokeKey = null;
            _threadKey=genThreadKey();
        }
        
        public InnerKey(Object invokeKey)
        {
            _invokeKey = invokeKey;
            _threadKey=genThreadKey();
            
        }
    }


    Object _key;

    public StatistableTrace(Trace stackTrace)
    {
        super(stackTrace);
        _key = null;
    }

    public StatistableTrace()
    {
        _key = null;
    }

    public Object getKey()
    {
        return _key;
    }

    public void setKey(Object key)
    {
        _key = key;
    }
}

package com.oilpalm3f.gradingapp.common;

//Exception
public class OilPalmException  extends Exception
{

    private static final long serialVersionUID = 1997753363232807009L;

    public OilPalmException()
    {
    }

    public OilPalmException(String message)
    {
        super(message);
    }

    public OilPalmException(Throwable cause)
    {
        super(cause);
    }

    public OilPalmException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public OilPalmException(String message, Throwable cause,
                            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}


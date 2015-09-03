package com.camcars;

import java.util.*;
import java.io.*;

/**
 * <p>Title: Proverbs Sort</p>
 * <p>Description: Classification  of Bible Proverbs</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author Serge LONLA
 * @version 1.0
 */

public class PSVerse  {
    
   
    public static final PSString POINT           = PSString.with(".");
    public static final PSString BOOK_SEPARATOR  = PSString.with(" ");
    public static final PSString CHAP_SEPARATOR  = PSString.with(":");
    public static final PSString VERSE_SEPARATOR = PSString.with(",");
    public static final PSString RANGE_SEPARATOR = PSString.with("-");
    public static final PSString REF_SEPARATOR   = PSString.with(";");
    public static final String __ALL_SEPARATORS  = " .:,-;";
    public static final PSString ALL_SEPARATORS  = PSString.with( __ALL_SEPARATORS );

    
}
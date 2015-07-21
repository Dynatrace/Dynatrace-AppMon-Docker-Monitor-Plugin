package com.dynatrace.docker.collector.helper;

import java.io.ByteArrayOutputStream;


//import org.apache.commons.lang3.ArrayUtils;

public class Pipe {

    public void pipe(Process process, ByteArrayOutputStream out, ByteArrayOutputStream err, boolean unlimited) {
        new PipeOut().pipe(process.getInputStream(), out, unlimited);
        new PipeError().pipe(process.getErrorStream(), err);
//        pipe(System.in, process.getOutputStream());
    }

}

/*
 * ProcessorFactory.java
 * Copright (C) 2001 Shakasta Sslytherin and Noiram Voker.
 * Created on 5. August 2001, 12:32
 */

package net.sourceforge.fraglets.aotools.codec;

import com.jclark.xml.sax.CommentDriver;
import com.jclark.xsl.sax.XSLProcessorImpl;
import com.jclark.xsl.sax.OutputMethodHandlerImpl;
import com.jclark.xsl.sax.OutputStreamDestination;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author  kre
 * @version 
 */
public class ProcessorFactory {

    /** Creates no ProcessorFactory */
    private ProcessorFactory() {
    }

    public static XSLProcessorImpl createProcessor(URL style, OutputStream out) throws IOException, SAXException {
        InputStream xsl = style.openStream();
        try {
            return createProcessor(xsl, out);
        } finally {
            xsl.close();
        }
    }
    
    public static XSLProcessorImpl createProcessor(InputStream style, OutputStream out) throws IOException, SAXException {
        XSLProcessorImpl processor = new XSLProcessorImpl();
        processor.setParser(new CommentDriver());
        
        OutputStreamDestination destination = new OutputStreamDestination(out) {
            public OutputStream getOutputStream(String contentType, String encoding) {
                setEncoding(encoding);
                return super.getOutputStream(contentType, encoding);
            }
        };
        
        OutputMethodHandlerImpl handler = new OutputMethodHandlerImpl(processor);
        handler.setDestination(destination);
        processor.setOutputMethodHandler(handler);
        
        processor.loadStylesheet(new InputSource(style));
        
        return processor;
    }
    
}

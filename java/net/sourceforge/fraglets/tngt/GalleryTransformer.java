/*
 * GalleryTransformer.java
 *
 * Created on October 5, 2002, 6:46 PM
 */

package net.sourceforge.fraglets.tngt;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileFilter;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.io.PrintWriter;
import com.jclark.xsl.sax.XSLProcessor;
import com.jclark.xsl.sax.XSLProcessorImpl;
import com.jclark.xsl.sax.OutputMethodHandlerImpl;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import com.jclark.xml.sax.CommentDriver;
import org.xml.sax.ErrorHandler;
import com.jclark.xsl.sax.Destination;
import com.jclark.xsl.sax.FileDestination;
import org.xml.sax.InputSource;

/**
 *
 * @author  marion@users.sourceforge.net
 */
public class GalleryTransformer {
    
    /** Creates a new instance of GalleryTransformer */
    public GalleryTransformer() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Listing listing = new Listing(args[0]);
            XSLProcessor processor = new XSLProcessorImpl();
            processor.setParser(new CommentDriver());
            processor.setErrorHandler(new ErrorHandler() {
                public void warning(SAXParseException ex) {
                    ex.printStackTrace();
                }
                public void error(SAXParseException ex) {
                    ex.printStackTrace();
                }
                public void fatalError(SAXParseException ex) throws SAXException {
                    throw ex;
                }
            });
            OutputMethodHandlerImpl handler = new OutputMethodHandlerImpl(processor);
            processor.setOutputMethodHandler(handler);
            processor.loadStylesheet(new InputSource(new File(args[1]).toURL().toExternalForm()));
            Destination out = new FileDestination(args[2]);
            handler.setDestination(out);
            processor.parse(new InputSource(listing.getInputStream()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static class Listing extends Thread implements Comparator, FileFilter {
        
        private PipedInputStream in;
        
        private PipedOutputStream out;
        
        private File base;
        
        public Listing(String base) throws IOException {
            this.out = new PipedOutputStream();
            this.in = new PipedInputStream(this.out);
            this.base = new File(base);
        }
        
        public InputStream getInputStream() {
            this.start();
            Thread.yield();
            return in;
        }
        
        public void run() {
            PrintWriter pw = new PrintWriter(out);
            File list[] = base.listFiles(this);
            Arrays.sort(list, this);
            pw.println("<listing>");
            for (int i = 0; i < list.length; i++) {
                pw.print("<image name=\"");
                pw.print(list[i].getName());
                pw.println("\"/>");
            }
            pw.println("</listing>");
            pw.close();
        }
        
        public boolean accept(File file) {
            return file.isFile() && file.getName().endsWith(".jpg");
        }
        
        public int compare(Object obj0, Object obj1) {
            return ((File)obj0).getName().compareTo(((File)obj1).getName());
        }
        
    }
}

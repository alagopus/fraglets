/*
 * NanoListCodec.java
 * Copright (C) 2001 Shakasta Sslytherin and Noiram Voker.
 * Created on 4. August 2001, 17:08
 */

package net.sourceforge.fraglets.aotools.codec;

import net.sourceforge.fraglets.aotools.model.BaseNanoCluster;
import net.sourceforge.fraglets.aotools.model.BaseNanoList;
import net.sourceforge.fraglets.aotools.model.Body;
import com.jclark.xml.output.UTF8XMLWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

/**
 *
 * @author  kre
 * @version 
 */
public class NanoListEncoder {
    /** The UTF8 writer. */
    UTF8XMLWriter out;
    
    /** Creates new NanoListEncoder */
    public NanoListEncoder(OutputStream out) {
        this.out = new UTF8XMLWriter(out,
            UTF8XMLWriter.MINIMIZE_EMPTY_ELEMENTS);
    }
    
    public void encodeBaseNanoList(BaseNanoList list) throws IOException {
        out.startElement(NanoListTags.BASE_NANO_LIST);
        out.write('\n');
        Iterator i = list.getAllNanos();
        while(i.hasNext()) {
            encodeBaseNanoCluster((BaseNanoCluster)i.next());
        }
        out.endElement(NanoListTags.BASE_NANO_LIST);
        out.write('\n');
    }
    
    public void encodeBaseNanoCluster(BaseNanoCluster nano) throws IOException {
        out.write("  ");
        out.startElement(NanoListTags.BASE_NANO_CLUSTER);
        out.attribute(NanoListTags.NAME, nano.getName());
        out.write('\n');
        encodeProperty(NanoListTags.BODY_LOC, Body.SLOT_NAMES[nano.getBodyLoc()]);
        encodeProperty(NanoListTags.SKILL, nano.getSkill());
        encodeProperty(NanoListTags.TYPE, BaseNanoCluster.NANO_TYPES[nano.getType()]);
        out.write("  ");
        out.endElement(NanoListTags.BASE_NANO_CLUSTER);
        out.write('\n');
    }

    public void encodeProperty(String name, String value) throws IOException {
        out.write("    ");
        out.startElement(NanoListTags.PROPERTY);
        out.attribute(NanoListTags.NAME, name);
        out.write(value);
        out.endElement(NanoListTags.PROPERTY);
        out.write('\n');
    }
    
    public void flush() throws IOException {
        out.flush();
    }

    public static void main(String args[]) {
        try {
            NanoListEncoder encoder;
            if (args.length > 0) {
                encoder = new NanoListEncoder(new java.io.FileOutputStream(args[0]));
            } else {
                encoder = new NanoListEncoder(System.out);
            }
            encoder.encodeBaseNanoList(BaseNanoList.getBaseNanoList());
            encoder.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

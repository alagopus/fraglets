/*
 * NanoListCodec.java
 * Copright (C) 2001 Shakasta Sslytherin and Noiram Voker.
 * Created on 4. August 2001, 17:08
 */

package net.sourceforge.fraglets.aotools.codec;

import com.jclark.xsl.sax.XSLProcessorImpl;
import com.jclark.xml.output.UTF8XMLWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;
import net.sourceforge.fraglets.aotools.model.BaseNanoCluster;
import net.sourceforge.fraglets.aotools.model.BaseNanoList;
import net.sourceforge.fraglets.aotools.model.Body;
import org.xml.sax.InputSource;

/** XML encoder for nano lists.
 *
 * @author kre
 * @version $Revision: 1.1 $
 */
public class NanoListPresenter {    
    /** The UTF8 writer. */
    UTF8XMLWriter out;
    
    /** Creates new NanoListPresenter
     * @param out the output stream for XML output
     */
    public NanoListPresenter(OutputStream out) {
        this.out = new UTF8XMLWriter(out,
            UTF8XMLWriter.MINIMIZE_EMPTY_ELEMENTS);
    }
    
    /** Encode a base nano list.
     * @param list the list to encode
     * @throws IOException on IO failures
     */    
    public void encodeBaseNanoList(BaseNanoList list) throws IOException {
        out.processingInstruction("xml-stylesheet",
            "href=\"PresentNanoList.xsl\" type=\"text/xsl\"");
        out.write('\n');
        out.startElement(NanoListTags.PRESENT_NANO_LIST);
        out.write('\n');
        Iterator i = list.getAllNanos();
        String currentName = null;
        String currentSkill = null;
        int currentSlot[] = new int[BaseNanoCluster.NANO_TYPES.length];
        Arrays.fill(currentSlot, -1);
        while(i.hasNext()) {
            BaseNanoCluster nextCluster = (BaseNanoCluster)i.next();
            String name = nextCluster.getName();
            if (currentName != null && !currentName.equals(name)) {
                encodeBaseNanoCluster(currentName, currentSkill, currentSlot);
                currentName = null;
                currentSkill = null;
                Arrays.fill(currentSlot, -1);
            }
            currentName = name;
            String skill = nextCluster.getSkill();
            if (currentSkill == null) {
                currentSkill = skill;
            } else if (!currentSkill.equals(skill)) {
                throw new IllegalArgumentException
                    ("mismatched skill: "+nextCluster);
            }
            int type = nextCluster.getType();
            int slot = nextCluster.getBodyLoc();
            if (currentSlot[type] == -1) {
                currentSlot[type] = slot;
            } else if (currentSlot[type] == slot) {
                System.err.println
                    ("warning: duplicate nano type: "+nextCluster);
            } else {
                throw new IllegalArgumentException
                    ("mismatched nano type: "+nextCluster);
            }
        }
        if (currentName != null) {
            encodeBaseNanoCluster(currentName, currentSkill, currentSlot);
        }
        out.endElement(NanoListTags.PRESENT_NANO_LIST);
        out.write('\n');
    }
    
    /** Encode a base nano.
     * @param nano the nano to encode
     * @throws IOException on IO failures
     */    
    public void encodeBaseNanoCluster(String name, String skill, int slot[]) throws IOException {
        out.write("  ");
        out.startElement(NanoListTags.PRESENT_NANO_CLUSTER);
        out.attribute(NanoListTags.NAME, name);
        out.attribute(NanoListTags.SKILL, skill);
        out.write('\n');
        for (int i = 0; i < slot.length; i++) {
            String typeName = BaseNanoCluster.NANO_TYPES[i];
            if (!typeName.equalsIgnoreCase("unknown") && slot[i] != -1) {
                String slotName = Body.SLOT_NAMES[slot[i]];
                encodeBaseNanoType(typeName, slotName);
            }
        }
        out.write("  ");
        out.endElement(NanoListTags.PRESENT_NANO_CLUSTER);
        out.write('\n');
    }

    /** Encode one nano type.
     * @param name name of the property
     * @param value value of the property
     * @throws IOException on IO failures
     */    
    public void encodeBaseNanoType(String name, String slot) throws IOException {
        out.write("    ");
        out.startElement(NanoListTags.PRESENT_NANO_TYPE);
        out.attribute(NanoListTags.NAME, name);
        out.write(slot);
        out.endElement(NanoListTags.PRESENT_NANO_TYPE);
        out.write('\n');
    }
    
    /** Flush the output stream.
     * @throws IOException on IO failures
     */    
    public void flush() throws IOException {
        out.flush();
    }

    /** Testing method.
     * @param args provide exactly onr file name for output
     */    
    public static void main(String args[]) {
        try {
            NanoListPresenter encoder;
            if (args.length > 0) {
                encoder = new NanoListPresenter(new java.io.FileOutputStream(args[0]));
            } else {
                encoder = new NanoListPresenter(System.out);
            }
            encoder.encodeBaseNanoList(BaseNanoList.getBaseNanoList());
            encoder.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

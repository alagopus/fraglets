/*
 * AvatarTest.java
 * JUnit based test
 *
 * Created on 12. Juli 2002, 07:56
 */

package net.sourceforge.fraglets.yaelp.model;

import junit.framework.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Properties;
import java.util.Map;
import java.util.StringTokenizer;
import java.io.IOException;
import java.util.Iterator;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;

/**
 * JUnit test for Avatar.
 *
 * <p>This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * <p>This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 * @author marion@users.sourceforge.net
 */
public class AvatarTest extends TestCase {
    
    public AvatarTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(AvatarTest.class);
        suite.addTest(GuildTest.suite());
        suite.addTest(TimestampEntryTest.suite());
        suite.addTest(ClassTest.suite());
        suite.addTest(CultureTest.suite());
        suite.addTest(ZoneTest.suite());
        
        return suite;
    }
    
    /** Test of getProperty method, of class net.sourceforge.fraglets.yaelp.Avatar. */
    public void testGetProperty() {
        long timestamp = System.currentTimeMillis();
        Avatar testling = new Avatar(timestamp);
        Avatar.Class clazz = Avatar.Class.create("Rogue");
        testling.setClazz(clazz);
        Avatar.Culture culture = Avatar.Culture.create("Halfling");
        testling.setCulture(culture);
        Avatar.Guild guild = Avatar.Guild.create("Bug Busters");
        testling.setGuild(guild, timestamp);
        int level = 24;
        testling.setLevel(level);
        String name = "Testling";
        testling.setName(name);
        // timestamp set in constructor
        Avatar.Zone zone = Avatar.Zone.create("nexus");
        testling.setZone(zone);
        testling.setProperty("purpose", "testing", timestamp);
        assertTrue("property count > 0", testling.getPropertyCount() > 0);
        assertSame("builtin property", clazz.getName(), testling.getProperty("class"));
        assertSame("builtin property", culture.getName(), testling.getProperty("culture"));
        assertSame("builtin property", guild.getName(), testling.getProperty("guild"));
        assertEquals("builtin property", String.valueOf(level), testling.getProperty("level"));
        assertSame("builtin property", name, testling.getProperty("name"));
        assertEquals("builtin property", new java.sql.Date(timestamp).toString(), testling.getProperty("timestamp"));
        assertSame("builtin property", zone.getName(), testling.getProperty("zone"));
        assertSame("custom property", "testing", testling.getProperty("purpose"));
    }
    
    /** Test of setProperty method, of class net.sourceforge.fraglets.yaelp.Avatar. */
    public void testSetProperty() {
        Avatar testling = new Avatar(0);
        long timestamp = System.currentTimeMillis();
        Avatar.Class clazz = Avatar.Class.create("Rogue");
        testling.setProperty("class", clazz.getName(), timestamp);
        Avatar.Culture culture = Avatar.Culture.create("Halfling");
        testling.setProperty("culture", culture.getName(), timestamp);
        Avatar.Guild guild = Avatar.Guild.create("Bug Busters");
        testling.setProperty("guild", guild.getName(), timestamp);
        try {
            testling.setProperty("level", "2k", timestamp);
            fail("setting invalid level should throw NumberFormatException");
        } catch (NumberFormatException ex) {
            // expected
        }
        int level = 24;
        testling.setProperty("level", "24", timestamp);
        String name = "Testling";
        try {
            testling.setProperty("name", name, timestamp);
            fail("setting name should throw IllegalStateException");
        } catch (IllegalStateException ex) {
            testling.setName(name);
        }
        // timestamp set in constructor
        Avatar.Zone zone = Avatar.Zone.create("nexus");
        testling.setProperty("zone", zone.getName(), timestamp);
        testling.setProperty("purpose", "initial", 1);
        testling.setProperty("purpose", "testing", timestamp);
        testling.setProperty("purpose", "outdated", 0);
        assertTrue("property count > 0", testling.getPropertyCount() > 0);
        assertSame("builtin property", clazz, testling.getClazz());
        assertSame("builtin property", culture, testling.getCulture());
        assertSame("builtin property", guild, testling.getGuild());
        assertEquals("builtin property", level, testling.getLevel());
        assertSame("builtin property", name, testling.getName());
        assertEquals("builtin property", timestamp, testling.getTimestamp());
        assertSame("builtin property", zone, testling.getZone());
        assertSame("custom property", "testing", testling.getProperty("purpose"));
    }
    
    /** Test of getProperties method, of class net.sourceforge.fraglets.yaelp.Avatar. */
    public void testGetProperties() {
        long timestamp = System.currentTimeMillis();
        Avatar testling = new Avatar(timestamp);
        testling.setProperty("class", "Rogue", timestamp);
        testling.setProperty("culture", "Halfling", timestamp);
        testling.setProperty("guild", "Bug Busters", timestamp);
        testling.setProperty("level", "24", timestamp);
        testling.setName("Testling");
        testling.setProperty("zone", "nexus", timestamp);
        assertTrue("all properties builtin", !testling.getProperties().hasNext());
        testling.setProperty("purpose", "testing", timestamp);
        Iterator i = testling.getProperties();
        assertTrue("custom property", i.hasNext());
        Map.Entry entry = (Map.Entry)i.next();
        assertEquals("property key", "purpose", entry.getKey());
        assertEquals("property value", "testing", entry.getValue().toString());
        assertTrue("last custom property", !i.hasNext());
    }
    
    /** Test of getPropertyCount method, of class net.sourceforge.fraglets.yaelp.Avatar. */
    public void testGetPropertyCount() {
        long timestamp = System.currentTimeMillis();
        Avatar testling = new Avatar(timestamp);
        testling.setProperty("class", "Rogue", timestamp);
        testling.setProperty("culture", "Halfling", timestamp);
        testling.setProperty("guild", "Bug Busters", timestamp);
        testling.setProperty("level", "24", timestamp);
        testling.setName("Testling");
        testling.setProperty("zone", "nexus", timestamp);
        assertEquals("all properties builtin", 0, testling.getPropertyCount());
        testling.setProperty("purpose", "testing", timestamp);
        assertEquals("one custom property", 1, testling.getPropertyCount());
    }
    
    /** Test of getLevel method, of class net.sourceforge.fraglets.yaelp.Avatar. */
    public void testGetLevel() {
        long timestamp = System.currentTimeMillis();
        Avatar testling = new Avatar(timestamp);
        int level = 24;
        testling.setLevel(level);
        assertEquals("level", level, testling.getLevel());
    }
    
    /** Test of setLevel method, of class net.sourceforge.fraglets.yaelp.Avatar. */
    public void testSetLevel() {
        testGetLevel();
    }
    
    /** Test of getName method, of class net.sourceforge.fraglets.yaelp.Avatar. */
    public void testGetName() {
        long timestamp = System.currentTimeMillis();
        Avatar testling = new Avatar(timestamp);
        String name = "Testling";
        testling.setName(name);
        assertSame("name", name, testling.getName());
    }
    
    /** Test of setName method, of class net.sourceforge.fraglets.yaelp.Avatar. */
    public void testSetName() {
        testGetName();
    }
    
    /** Test of getClazz method, of class net.sourceforge.fraglets.yaelp.Avatar. */
    public void testGetClazz() {
        long timestamp = System.currentTimeMillis();
        Avatar testling = new Avatar(timestamp);
        Avatar.Class clazz = Avatar.Class.create("Rogue");
        testling.setClazz(clazz);
        assertSame("clazz", clazz, testling.getClazz());
    }
    
    /** Test of setClazz method, of class net.sourceforge.fraglets.yaelp.Avatar. */
    public void testSetClazz() {
        testGetClazz();
    }
    
    /** Test of getGuild method, of class net.sourceforge.fraglets.yaelp.Avatar. */
    public void testGetGuild() {
        long timestamp = System.currentTimeMillis();
        Avatar testling = new Avatar(timestamp);
        Avatar.Guild guild = Avatar.Guild.create("Bug Busters");
        testling.setGuild(guild, timestamp);
        assertSame("guild", guild, testling.getGuild());
        Avatar.Guild other = Avatar.Guild.create("Other Bug Busters");
        testling.setGuild(other, 0L);
        assertTrue("guild", other != testling.getGuild());
    }
    
    /** Test of setGuild method, of class net.sourceforge.fraglets.yaelp.Avatar. */
    public void testSetGuild() {
        testGetGuild();
    }
    
    /** Test of getZone method, of class net.sourceforge.fraglets.yaelp.Avatar. */
    public void testGetZone() {
        long timestamp = System.currentTimeMillis();
        Avatar testling = new Avatar(timestamp);
        Avatar.Zone zone = Avatar.Zone.create("nexus");
        testling.setZone(zone);
        assertSame("zone", zone, testling.getZone());
    }
    
    /** Test of setZone method, of class net.sourceforge.fraglets.yaelp.Avatar. */
    public void testSetZone() {
        testGetZone();
    }
    
    /** Test of getCulture method, of class net.sourceforge.fraglets.yaelp.Avatar. */
    public void testGetCulture() {
        long timestamp = System.currentTimeMillis();
        Avatar testling = new Avatar(timestamp);
        Avatar.Culture culture = Avatar.Culture.create("Halfling");
        testling.setCulture(culture);
        assertSame("culture", culture, testling.getCulture());
    }
    
    /** Test of setCulture method, of class net.sourceforge.fraglets.yaelp.Avatar. */
    public void testSetCulture() {
        testGetCulture();
    }
    
    /** Test of fireNewInstance method, of class net.sourceforge.fraglets.yaelp.Avatar. */
    public void testFireNewInstance() {
        PropertyEventRecorder recorder = new PropertyEventRecorder();
        Avatar.CHANGE.addPropertyChangeListener(recorder);
        try {
            Object instance = new String("instance");
            Avatar.fireNewInstance(instance);
            long timestamp = System.currentTimeMillis();
            Avatar.Class clazz = Avatar.Class.create("class."+timestamp);
            Avatar.Culture culture = Avatar.Culture.create("culture."+timestamp);
            Avatar.Guild guild = Avatar.Guild.create("guild."+timestamp);
            Avatar.Zone zone = Avatar.Zone.create("zone."+timestamp);
            PropertyChangeEvent event = null;
            int count = recorder.size();
            assertTrue("event count", count >= 5);
            int index = 0;
            while (index < count && (event = recorder.getPropertyChangeEvent(index++)).getNewValue() != instance);
            assertSame("new value", instance, event.getNewValue());
            assertEquals("event name", instance.getClass().getName(), event.getPropertyName());
            instance = clazz;
            while (index < count && (event = recorder.getPropertyChangeEvent(index++)).getNewValue() != instance);
            assertSame("new value", instance, event.getNewValue());
            assertEquals("event name", instance.getClass().getName(), event.getPropertyName());
            instance = culture;
            while (index < count && (event = recorder.getPropertyChangeEvent(index++)).getNewValue() != instance);
            assertSame("new value", instance, event.getNewValue());
            assertEquals("event name", instance.getClass().getName(), event.getPropertyName());
            instance = guild;
            while (index < count && (event = recorder.getPropertyChangeEvent(index++)).getNewValue() != instance);
            assertSame("new value", instance, event.getNewValue());
            assertEquals("event name", instance.getClass().getName(), event.getPropertyName());
            instance = zone;
            while (index < count && (event = recorder.getPropertyChangeEvent(index++)).getNewValue() != instance);
            assertSame("new value", instance, event.getNewValue());
            assertEquals("event name", instance.getClass().getName(), event.getPropertyName());
        } finally {
            Avatar.CHANGE.removePropertyChangeListener(recorder);
        }
    }
    
    /** Test of fireNewProperty method, of class net.sourceforge.fraglets.yaelp.Avatar. */
    public void testFireNewProperty() {
        PropertyEventRecorder recorder = new PropertyEventRecorder();
        Avatar.CHANGE.addPropertyChangeListener(recorder);
        try {
            long timestamp = System.currentTimeMillis();
            Avatar testling = new Avatar(timestamp);
            testling.setProperty("purpose", "initial", 1);
            testling.setProperty("purpose", "testing", timestamp);
            testling.setProperty("purpose", "outdated", 0);
            Avatar.CHANGE.removePropertyChangeListener(recorder);
            PropertyChangeEvent event = null;
            int count = recorder.size();
            assertTrue("event count", count >= 2);
            int index = 0;
            while (index < count && (event = recorder.getPropertyChangeEvent(index++)).getOldValue() != testling);
            assertSame("new value", "purpose", event.getNewValue());
            assertEquals("event name", "Avatar.property", event.getPropertyName());
            while (index < count && (event = recorder.getPropertyChangeEvent(index++)).getOldValue() != testling);
            assertSame("new value", "purpose", event.getNewValue());
            assertEquals("event name", "Avatar.property", event.getPropertyName());
            while (index < count && (event = recorder.getPropertyChangeEvent(index++)).getOldValue() != testling);
            assertEquals("event not fired", index, count);
        } finally {
            Avatar.CHANGE.removePropertyChangeListener(recorder);
        }
    }
    
    /** Test of toString method, of class net.sourceforge.fraglets.yaelp.Avatar. */
    public void testToString() {
        long timestamp = System.currentTimeMillis();
        Avatar testling = new Avatar(timestamp);
        testling.setName("Testling");
        assertTrue("toString contains name", testling.toString().indexOf("Testling") >= 0);
    }
    
    /** Test of getTimestamp method, of class net.sourceforge.fraglets.yaelp.Avatar. */
    public void testGetTimestamp() {
        long timestamp = System.currentTimeMillis();
        Avatar testling = new Avatar(0L);
        assertEquals("initial timestamp", 0L, testling.getTimestamp());
        testling.setTimestamp(timestamp);
        assertEquals("new timestamp", timestamp, testling.getTimestamp());
    }
    
    /** Test of setTimestamp method, of class net.sourceforge.fraglets.yaelp.Avatar. */
    public void testSetTimestamp() {
        testGetTimestamp();
    }
    
    /** Test of equals method, of class net.sourceforge.fraglets.yaelp.Avatar. */
    public void testEquals() {
        long timestamp = System.currentTimeMillis();
        Avatar testling = new Avatar(timestamp);
        assertTrue("equals self", testling.equals(testling));
        Avatar other = new Avatar(timestamp);
        assertTrue("differs other", !testling.equals(other));
    }
    
    /** Test of hashCode method, of class net.sourceforge.fraglets.yaelp.Avatar. */
    public void testHashCode() {
        long timestamp = System.currentTimeMillis();
        Avatar testling = new Avatar(timestamp);
        assertEquals("equals self", testling.hashCode(), testling.hashCode());
        testling.setName("Testling");
        assertEquals("equals self", testling.hashCode(), testling.hashCode());
        
        Avatar samples[] = new Avatar[100];
        for (int i = 0; i < samples.length; i++) {
            samples[i] = new Avatar(timestamp);
            samples[i].setName(randomName());
        }
        for (int i = 0; i < samples.length; i++) {
            for (int j = 0; j < samples.length; j++) {
                if (samples[i].hashCode() != samples[j].hashCode()) {
                    assertTrue("not equal when hash code differs", !samples[i].equals(samples[j]));
                }
            }
        }
    }
    
    /** Test of normalizeName method, of class net.sourceforge.fraglets.yaelp.Avatar. */
    public void testNormalizeName() {
        String sample = "Wonderful";
        String result = sample;
        assertSame("unchanged name", sample, Avatar.normalizeName(sample));
        sample = "CorrEct";
        result = "Correct";
        assertEquals("caps in tail", result, Avatar.normalizeName(sample));
        sample = "correct";
        result = "Correct";
        assertEquals("lowercase", result, Avatar.normalizeName(sample));
        sample = "CORRECT";
        result = "Correct";
        assertEquals("all caps", result, Avatar.normalizeName(sample));
        sample = "c";
        result = "C";
        assertEquals("single character", result, Avatar.normalizeName(sample));
        sample = "";
        result = "";
        assertEquals("empty name", result, Avatar.normalizeName(sample));
        sample = null;
        result = null;
        assertSame("null name", result, Avatar.normalizeName(sample));
        for (int i = 0; i < 100; i++) {
            sample = randomString(randomNumber(1, 20));
            result = Avatar.normalizeName(sample);
            assertTrue("first character uppercase", Character.isUpperCase(result.charAt(0)));
            String rest = result.substring(1);
            assertEquals("rest characters lowercase", rest, rest.toLowerCase());
        }
    }
    
    public static class GuildTest extends TestCase {
        
        public GuildTest(java.lang.String testName) {
            super(testName);
        }
        
        public static void main(java.lang.String[] args) {
            junit.textui.TestRunner.run(suite());
        }
        
        public static Test suite() {
            TestSuite suite = new TestSuite(GuildTest.class);
            
            return suite;
        }
        
        /** Test of create method, of class net.sourceforge.fraglets.yaelp.Avatar.Guild. */
        public void testCreate() {
            String name = "Bug Busters";
            Avatar.Guild guild1 = Avatar.Guild.create(name);
            Avatar.Guild guild2 = Avatar.Guild.create(name);
            assertNotNull("created instance", guild1);
            assertNotNull("created instance", guild2);
            assertSame("identical", guild1, guild2);
            Avatar.Guild guild3 = Avatar.Guild.create("Other "+name);
            assertTrue("different instance", guild1 != guild3);
        }
        
        /** Test of getValues method, of class net.sourceforge.fraglets.yaelp.Avatar.Guild. */
        public void testGetValues() {
            Avatar.Guild samples[] = new Avatar.Guild[20];
            for (int i = 0; i < samples.length; i++) {
                samples[i] = Avatar.Guild.create(randomName());
            }
            Collection values = Avatar.Guild.getValues();
            for (int i = 0; i < samples.length; i++) {
                assertTrue("values contain sample", values.contains(samples[i]));
            }
        }
        
        /** Test of getComparator method, of class net.sourceforge.fraglets.yaelp.Avatar.Guild. */
        public void testGetComparator() {
            Avatar.Guild samples[] = new Avatar.Guild[20];
            for (int i = 0; i < samples.length; i++) {
                samples[i] = Avatar.Guild.create(randomName());
            }
            Arrays.sort(samples, Avatar.Guild.getComparator());
            for (int i = 1; i < samples.length; i++) {
                assertTrue("names sorted", samples[i-1].getName().compareTo(samples[i].getName()) <= 0);
            }
        }
        
        /** Test of getName method, of class net.sourceforge.fraglets.yaelp.Avatar.Guild. */
        public void testGetName() {
            String name = "Bug Busters";
            Avatar.Guild guild = Avatar.Guild.create(name);
            assertEquals("name equal", name, guild.getName());
        }
        
        /** Test of toString method, of class net.sourceforge.fraglets.yaelp.Avatar.Guild. */
        public void testToString() {
            String name = "Bug Busters";
            Avatar.Guild guild = Avatar.Guild.create(name);
            assertTrue("toString contains name", guild.toString().indexOf(name) >= 0);
        }
        
        /** Test of equals method, of class net.sourceforge.fraglets.yaelp.Avatar.Guild. */
        public void testEquals() {
            String name = "Bug Busters";
            Avatar.Guild guild1 = Avatar.Guild.create(name);
            assertTrue("equals self", guild1.equals(guild1));
            Avatar.Guild guild2 = Avatar.Guild.create("Other "+name);
            assertTrue("differs other", !guild1.equals(guild2));
        }
        
        /** Test of hashCode method, of class net.sourceforge.fraglets.yaelp.Avatar.Guild. */
        public void testHashCode() {
            String name = "Bug Busters";
            Avatar.Guild guild = Avatar.Guild.create(name);
            assertEquals("equals self", guild.hashCode(), guild.hashCode());

            Avatar.Guild samples[] = new Avatar.Guild[20];
            for (int i = 0; i < samples.length; i++) {
                samples[i] = Avatar.Guild.create(randomName());
            }
            for (int i = 0; i < samples.length; i++) {
                for (int j = 0; j < samples.length; j++) {
                    if (samples[i].hashCode() != samples[j].hashCode()) {
                        assertTrue("not equal when hash code differs", !samples[i].equals(samples[j]));
                    }
                }
            }
        }
        
        /** Test of compareTo method, of class net.sourceforge.fraglets.yaelp.Avatar.Guild. */
        public void testCompareTo() {
            Avatar.Guild samples[] = new Avatar.Guild[20];
            for (int i = 0; i < samples.length; i++) {
                samples[i] = Avatar.Guild.create(randomName());
            }
            for (int i = 0; i < samples.length; i++) {
                for (int j = 0; j < samples.length; j++) {
                    if (samples[i].hashCode() != samples[j].hashCode()) {
                        int diff1 = samples[i].compareTo(samples[j]);
                        int diff2 = samples[i].getName().compareTo(samples[j].getName());
                        assertEquals("comparision", diff1 < 0, diff2 < 0);
                        assertEquals("comparision", diff1 > 0, diff2 > 0);
                    }
                }
            }
        }
        
    }
    
    public static class TimestampEntryTest extends TestCase {
        
        public TimestampEntryTest(java.lang.String testName) {
            super(testName);
        }
        
        public static void main(java.lang.String[] args) {
            junit.textui.TestRunner.run(suite());
        }
        
        public static Test suite() {
            TestSuite suite = new TestSuite(TimestampEntryTest.class);
            
            return suite;
        }
        
        /** Test of toString method, of class net.sourceforge.fraglets.yaelp.Avatar.TimestampEntry. */
        public void testToString() {
            long timestamp = System.currentTimeMillis();
            Avatar.TimestampEntry samples[] = new Avatar.TimestampEntry[100];
            samples[0] = new Avatar.TimestampEntry(null, timestamp);
            for (int i = 1; i < samples.length; i++) {
                samples[i] = new Avatar.TimestampEntry(randomString(randomNumber(0, 200)), timestamp);
            }
            for (int i = 0; i < samples.length; i++) {
                assertSame("toString identical to value", samples[i].value, samples[i].toString());
            }
        }
        
    }
    
    public static class ClassTest extends TestCase {
        
        public ClassTest(java.lang.String testName) {
            super(testName);
        }
        
        public static void main(java.lang.String[] args) {
            junit.textui.TestRunner.run(suite());
        }
        
        public static Test suite() {
            TestSuite suite = new TestSuite(ClassTest.class);
            
            return suite;
        }
        
        /** Test of create method, of class net.sourceforge.fraglets.yaelp.Avatar.Class. */
        public void testCreate() {
            String name = "Rogue";
            Avatar.Class clazz1 = Avatar.Class.create(name);
            Avatar.Class clazz2 = Avatar.Class.create(name);
            assertNotNull("created instance", clazz1);
            assertNotNull("created instance", clazz2);
            assertSame("identical", clazz1, clazz2);
            Avatar.Class clazz3 = Avatar.Class.create("Ranger");
            assertTrue("different instance", clazz1 != clazz3);
        }
        
        /** Test of getValues method, of class net.sourceforge.fraglets.yaelp.Avatar.Class. */
        public void testGetValues() {
            Avatar.Class samples[] = new Avatar.Class[20];
            for (int i = 0; i < samples.length; i++) {
                samples[i] = Avatar.Class.create(randomName());
            }
            Collection values = Avatar.Class.getValues();
            for (int i = 0; i < samples.length; i++) {
                assertTrue("values contain sample", values.contains(samples[i]));
            }
        }
        
        /** Test of getComparator method, of class net.sourceforge.fraglets.yaelp.Avatar.Class. */
        public void testGetComparator() {
            Avatar.Class samples[] = new Avatar.Class[20];
            for (int i = 0; i < samples.length; i++) {
                samples[i] = Avatar.Class.create(randomName());
            }
            Arrays.sort(samples, Avatar.Class.getComparator());
            for (int i = 1; i < samples.length; i++) {
                assertTrue("names sorted", samples[i-1].getName().compareTo(samples[i].getName()) <= 0);
            }
        }
        
        /** Test of getName method, of class net.sourceforge.fraglets.yaelp.Avatar.Class. */
        public void testGetName() {
            String name = "Rogue";
            Avatar.Class clazz = Avatar.Class.create(name);
            assertEquals("name equal", name, clazz.getName());
        }
        
        /** Test of toString method, of class net.sourceforge.fraglets.yaelp.Avatar.Class. */
        public void testToString() {
            String name = "Rogue";
            Avatar.Class clazz = Avatar.Class.create(name);
            assertTrue("toString contains name", clazz.toString().indexOf(name) >= 0);
        }
        
        /** Test of equals method, of class net.sourceforge.fraglets.yaelp.Avatar.Class. */
        public void testEquals() {
            Avatar.Class clazz1 = Avatar.Class.create("Rogue");
            assertTrue("equals self", clazz1.equals(clazz1));
            Avatar.Class clazz2 = Avatar.Class.create("Ranger");
            assertTrue("differs other", !clazz1.equals(clazz2));
        }
        
        /** Test of hashCode method, of class net.sourceforge.fraglets.yaelp.Avatar.Class. */
        public void testHashCode() {
            Avatar.Class clazz = Avatar.Class.create("Rogue");
            assertEquals("equals self", clazz.hashCode(), clazz.hashCode());

            Avatar.Class samples[] = new Avatar.Class[20];
            for (int i = 0; i < samples.length; i++) {
                samples[i] = Avatar.Class.create(randomName());
            }
            for (int i = 0; i < samples.length; i++) {
                for (int j = 0; j < samples.length; j++) {
                    if (samples[i].hashCode() != samples[j].hashCode()) {
                        assertTrue("not equal when hash code differs", !samples[i].equals(samples[j]));
                    }
                }
            }
        }
        
        /** Test of compareTo method, of class net.sourceforge.fraglets.yaelp.Avatar.Class. */
        public void testCompareTo() {
            Avatar.Class samples[] = new Avatar.Class[20];
            for (int i = 0; i < samples.length; i++) {
                samples[i] = Avatar.Class.create(randomName());
            }
            for (int i = 0; i < samples.length; i++) {
                for (int j = 0; j < samples.length; j++) {
                    if (samples[i].hashCode() != samples[j].hashCode()) {
                        int diff1 = samples[i].compareTo(samples[j]);
                        int diff2 = samples[i].getName().compareTo(samples[j].getName());
                        assertEquals("comparision", diff1 < 0, diff2 < 0);
                        assertEquals("comparision", diff1 > 0, diff2 > 0);
                    }
                }
            }
        }
        
        /** Test of canonicalName method, of class net.sourceforge.fraglets.yaelp.Avatar.Class. */
        public void testCanonicalName() {
            assertEquals("canonical name", "Bard", Avatar.Class.canonicalName("Minstrel"));
            assertEquals("canonical name", "Magician", Avatar.Class.canonicalName("Magician"));
            assertEquals("canonical name", "Cleric", Avatar.Class.canonicalName("Templar"));
            assertEquals("canonical name", "Monk", Avatar.Class.canonicalName("Grandmaster"));
            assertEquals("canonical name", "Beastlord", Avatar.Class.canonicalName("Savage Lord"));
            assertEquals("canonical name", "unknown", Avatar.Class.canonicalName("unknown"));
        }
        
    }
    
    public static class CultureTest extends TestCase {
        
        public CultureTest(java.lang.String testName) {
            super(testName);
        }
        
        public static void main(java.lang.String[] args) {
            junit.textui.TestRunner.run(suite());
        }
        
        public static Test suite() {
            TestSuite suite = new TestSuite(CultureTest.class);
            
            return suite;
        }
        
        /** Test of create method, of class net.sourceforge.fraglets.yaelp.Avatar.Culture. */
        public void testCreate() {
            String name = "Halfling";
            Avatar.Culture culture1 = Avatar.Culture.create(name);
            Avatar.Culture culture2 = Avatar.Culture.create(name);
            assertNotNull("created instance", culture1);
            assertNotNull("created instance", culture2);
            assertSame("identical", culture1, culture2);
            Avatar.Culture culture3 = Avatar.Culture.create("Gnome");
            assertTrue("different instance", culture1 != culture3);
        }
        
        /** Test of getValues method, of class net.sourceforge.fraglets.yaelp.Avatar.Culture. */
        public void testGetValues() {
            Avatar.Culture samples[] = new Avatar.Culture[20];
            for (int i = 0; i < samples.length; i++) {
                samples[i] = Avatar.Culture.create(randomName());
            }
            Collection values = Avatar.Culture.getValues();
            for (int i = 0; i < samples.length; i++) {
                assertTrue("values contain sample", values.contains(samples[i]));
            }
        }
        
        /** Test of getComparator method, of class net.sourceforge.fraglets.yaelp.Avatar.Culture. */
        public void testGetComparator() {
            Avatar.Culture samples[] = new Avatar.Culture[20];
            for (int i = 0; i < samples.length; i++) {
                samples[i] = Avatar.Culture.create(randomName());
            }
            Arrays.sort(samples, Avatar.Culture.getComparator());
            for (int i = 1; i < samples.length; i++) {
                assertTrue("names sorted", samples[i-1].getName().compareTo(samples[i].getName()) <= 0);
            }
        }
        
        /** Test of getName method, of class net.sourceforge.fraglets.yaelp.Avatar.Culture. */
        public void testGetName() {
            String name = "Halfling";
            Avatar.Culture culture = Avatar.Culture.create(name);
            assertEquals("name equal", name, culture.getName());
        }
        
        /** Test of toString method, of class net.sourceforge.fraglets.yaelp.Avatar.Culture. */
        public void testToString() {
            String name = "Halfling";
            Avatar.Culture culture = Avatar.Culture.create(name);
            assertTrue("toString contains name", culture.toString().indexOf(name) >= 0);
        }
        
        /** Test of equals method, of class net.sourceforge.fraglets.yaelp.Avatar.Culture. */
        public void testEquals() {
            Avatar.Culture culture1 = Avatar.Culture.create("Halfling");
            assertTrue("equals self", culture1.equals(culture1));
            Avatar.Culture culture2 = Avatar.Culture.create("Gnome");
            assertTrue("differs other", !culture1.equals(culture2));
        }
        
        /** Test of hashCode method, of class net.sourceforge.fraglets.yaelp.Avatar.Culture. */
        public void testHashCode() {
            Avatar.Culture culture = Avatar.Culture.create("Halfling");
            assertEquals("equals self", culture.hashCode(), culture.hashCode());

            Avatar.Culture samples[] = new Avatar.Culture[20];
            for (int i = 0; i < samples.length; i++) {
                samples[i] = Avatar.Culture.create(randomName());
            }
            for (int i = 0; i < samples.length; i++) {
                for (int j = 0; j < samples.length; j++) {
                    if (samples[i].hashCode() != samples[j].hashCode()) {
                        assertTrue("not equal when hash code differs", !samples[i].equals(samples[j]));
                    }
                }
            }
        }
        
        /** Test of compareTo method, of class net.sourceforge.fraglets.yaelp.Avatar.Culture. */
        public void testCompareTo() {
            Avatar.Culture samples[] = new Avatar.Culture[20];
            for (int i = 0; i < samples.length; i++) {
                samples[i] = Avatar.Culture.create(randomName());
            }
            for (int i = 0; i < samples.length; i++) {
                for (int j = 0; j < samples.length; j++) {
                    if (samples[i].hashCode() != samples[j].hashCode()) {
                        int diff1 = samples[i].compareTo(samples[j]);
                        int diff2 = samples[i].getName().compareTo(samples[j].getName());
                        assertEquals("comparision", diff1 < 0, diff2 < 0);
                        assertEquals("comparision", diff1 > 0, diff2 > 0);
                    }
                }
            }
        }
        
    }
    
    public static class ZoneTest extends TestCase {
        
        public ZoneTest(java.lang.String testName) {
            super(testName);
        }
        
        public static void main(java.lang.String[] args) {
            junit.textui.TestRunner.run(suite());
        }
        
        public static Test suite() {
            TestSuite suite = new TestSuite(ZoneTest.class);
            
            return suite;
        }
        
        /** Test of create method, of class net.sourceforge.fraglets.yaelp.Avatar.Zone. */
        public void testCreate() {
            String name = "nexus";
            Avatar.Zone zone1 = Avatar.Zone.create(name);
            Avatar.Zone zone2 = Avatar.Zone.create(name);
            assertNotNull("created instance", zone1);
            assertNotNull("created instance", zone2);
            assertSame("identical", zone1, zone2);
            Avatar.Zone zone3 = Avatar.Zone.create("hole");
            assertTrue("different instance", zone1 != zone3);
        }
        
        /** Test of getValues method, of class net.sourceforge.fraglets.yaelp.Avatar.Zone. */
        public void testGetValues() {
            Avatar.Zone samples[] = new Avatar.Zone[20];
            for (int i = 0; i < samples.length; i++) {
                samples[i] = Avatar.Zone.create(randomName().toLowerCase());
            }
            Collection values = Avatar.Zone.getValues();
            for (int i = 0; i < samples.length; i++) {
                assertTrue("values contain sample", values.contains(samples[i]));
            }
        }
        
        /** Test of getComparator method, of class net.sourceforge.fraglets.yaelp.Avatar.Zone. */
        public void testGetComparator() {
            Avatar.Zone samples[] = new Avatar.Zone[20];
            for (int i = 0; i < samples.length; i++) {
                samples[i] = Avatar.Zone.create(randomName().toLowerCase());
            }
            Arrays.sort(samples, Avatar.Zone.getComparator());
            for (int i = 1; i < samples.length; i++) {
                assertTrue("names sorted", samples[i-1].getName().compareTo(samples[i].getName()) <= 0);
            }
        }
        
        /** Test of getName method, of class net.sourceforge.fraglets.yaelp.Avatar.Zone. */
        public void testGetName() {
            String name = "nexus";
            Avatar.Zone zone = Avatar.Zone.create(name);
            assertEquals("name equal", name, zone.getName());
        }
        
        /** Test of toString method, of class net.sourceforge.fraglets.yaelp.Avatar.Zone. */
        public void testToString() {
            String name = "nexus";
            Avatar.Zone zone = Avatar.Zone.create(name);
            assertTrue("toString contains name", zone.toString().indexOf(name) >= 0);
        }
        
        /** Test of equals method, of class net.sourceforge.fraglets.yaelp.Avatar.Zone. */
        public void testEquals() {
            Avatar.Zone zone1 = Avatar.Zone.create("nexus");
            assertTrue("equals self", zone1.equals(zone1));
            Avatar.Zone zone2 = Avatar.Zone.create("hole");
            assertTrue("differs other", !zone1.equals(zone2));
        }
        
        /** Test of hashCode method, of class net.sourceforge.fraglets.yaelp.Avatar.Zone. */
        public void testHashCode() {
            Avatar.Zone zone = Avatar.Zone.create("nexus");
            assertEquals("equals self", zone.hashCode(), zone.hashCode());

            Avatar.Zone samples[] = new Avatar.Zone[20];
            for (int i = 0; i < samples.length; i++) {
                samples[i] = Avatar.Zone.create(randomName().toLowerCase());
            }
            for (int i = 0; i < samples.length; i++) {
                for (int j = 0; j < samples.length; j++) {
                    if (samples[i].hashCode() != samples[j].hashCode()) {
                        assertTrue("not equal when hash code differs", !samples[i].equals(samples[j]));
                    }
                }
            }
        }
        
        /** Test of compareTo method, of class net.sourceforge.fraglets.yaelp.Avatar.Zone. */
        public void testCompareTo() {
            Avatar.Zone samples[] = new Avatar.Zone[20];
            for (int i = 0; i < samples.length; i++) {
                samples[i] = Avatar.Zone.create(randomName());
            }
            for (int i = 0; i < samples.length; i++) {
                for (int j = 0; j < samples.length; j++) {
                    if (samples[i].hashCode() != samples[j].hashCode()) {
                        int diff1 = samples[i].compareTo(samples[j]);
                        int diff2 = samples[i].getName().compareTo(samples[j].getName());
                        assertEquals("comparision", diff1 < 0, diff2 < 0);
                        assertEquals("comparision", diff1 > 0, diff2 > 0);
                    }
                }
            }
        }
        
    }
    
    // Add test methods here, they have to start with 'test' name.
    // for example:
    // public void testHello() {}
    
    public static class PropertyEventRecorder extends ArrayList
        implements PropertyChangeListener {
        public PropertyChangeEvent getPropertyChangeEvent(int index) {
            return (PropertyChangeEvent)get(index);
        }
        public void propertyChange(PropertyChangeEvent ev) {
            add(ev);
        }
    }
    
    private static Random random = new Random(8726934721L);
    
    protected static int randomNumber(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }
    
    private static final String candidates =
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    
    protected static String randomString(int length) {
        StringBuffer buffer = new StringBuffer(length);
        while (--length >= 0) {
            buffer.append(candidates.charAt
                (randomNumber(0, candidates.length() - 1)));
        }
        return buffer.toString();
    }
    
    protected static String randomName() {
        return Avatar.normalizeName(randomString(randomNumber(0, 20)));
    }
}

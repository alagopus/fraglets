/*
 * DOMContext.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on Apr 14, 2003 by marion@users.sourceforge.net
 */
package net.sourceforge.fraglets.zeig.jndi;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.CompoundName;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NotContextException;
import javax.naming.spi.NamingManager;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.sourceforge.fraglets.zeig.dom.DocumentImpl;
import net.sourceforge.fraglets.zeig.dom.NamedNodeMapImpl;
import net.sourceforge.fraglets.zeig.jdbc.ConnectionFactory;
import net.sourceforge.fraglets.zeig.model.NodeFactory;
import net.sourceforge.fraglets.zeig.model.PlainTextFactory;
import net.sourceforge.fraglets.zeig.model.SAXFactory;
import net.sourceforge.fraglets.zeig.model.VersionFactory;

/**
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.1 $
 */
public class DOMContext implements Context {
    /** Context option. */
    public static final String VERSION_COMMENT =
        "net.sourceforge.fraglets.zeig.jndi.versionComment";
    
    private Properties environment;
    private NameParser nameParser;
    private DOMContext parent;
    private Document binding;
    private HashMap context;
    private String atom;
    
    public DOMContext(Hashtable defaults) throws NamingException {
        init(defaults);
        this.nameParser = new SimpleNameParser(environment);
        this.binding = new DocumentImpl(getRoot());
    }
    
    protected DOMContext(DOMContext blueprint) {
        this.environment = new Properties(blueprint.environment);
        this.nameParser = blueprint.nameParser;
        this.binding = blueprint.binding;
        this.parent = blueprint.parent;
        this.atom = blueprint.atom;
    }
    
    protected DOMContext(DOMContext parent, String name, Document binding) {
        this.environment = new Properties(parent.environment);
        this.nameParser = parent.nameParser;
        this.binding = binding;
        this.parent = parent;
        this.atom = name;
    }
    
    protected void init(Hashtable defaults) {
        environment = new Properties();
        environment.putAll(defaults);
    }

    /**
     * @see javax.naming.Context#lookup(javax.naming.Name)
     */
    public Object lookup(Name name) throws NamingException {
        if (name.isEmpty()) {
            // request for copy
            return new DOMContext(this);
        }

        Name nm = getComponents(name);
        String atom = nm.get(0);
        Element in = lookupElement(atom);
        
        if (nm.size() == 1) {
            if (in == null) {
                throw new NameNotFoundException(name.toString());
            }

            try {
                return NamingManager.getObjectInstance
                    (in, new CompositeName().add(atom), this, environment);
            } catch (Exception ex) {
                throw namingException("getObjectInstance", ex);
            }
        } else {
            return getSubContext(in, atom).lookup(nm.getSuffix(1));
        }
    }
    
    protected Element lookupElement(String atom) {
        return binding.getElementById(atom);
    }

    /**
     * @see javax.naming.Context#lookup(java.lang.String)
     */
    public Object lookup(String name) throws NamingException {
        return lookup(new CompositeName(name));
    }

    /**
     * @see javax.naming.Context#bind(javax.naming.Name, java.lang.Object)
     */
    public void bind(Name name, Object obj) throws NamingException {
        if (name.isEmpty()) {
            throw new InvalidNameException("empty name");
        }

        // Extract components that belong to this namespace
        Name nm = getComponents(name);
        String atom = nm.get(0);
        Element in = lookupElement(atom);

        if (nm.size() == 1) {
            // Atomic name: Find object in internal data structure
            if (in != null) {
                throw new NameAlreadyBoundException(name.toString());
            }

            bind(obj, in, atom);
        } else {
            getSubContext(in, atom).bind(nm.getSuffix(1), obj);
        }
    }
    
    /**
     * @see javax.naming.Context#bind(java.lang.String, java.lang.Object)
     */
    public void bind(String name, Object obj) throws NamingException {
        bind(new CompositeName(name), obj);
    }

    /**
     * @see javax.naming.Context#rebind(javax.naming.Name, java.lang.Object)
     */
    public void rebind(Name name, Object obj) throws NamingException {
        if (name.isEmpty()) {
            throw new InvalidNameException("empty name");
        }

        // Extract components that belong to this namespace
        Name nm = getComponents(name);
        String atom = nm.get(0);
        Element in = lookupElement(atom);

        if (nm.size() == 1) {
            bind(obj, in, atom);
        } else {
            getSubContext(in, atom).bind(nm.getSuffix(1), obj);
        }
    }

    /**
     * @see javax.naming.Context#rebind(java.lang.String, java.lang.Object)
     */
    public void rebind(String name, Object obj) throws NamingException {
        rebind(new CompositeName(name), obj);
    }

    /**
     * @see javax.naming.Context#unbind(javax.naming.Name)
     */
    public void unbind(Name name) throws NamingException {
        if (name.isEmpty()) {
            throw new InvalidNameException("empty name");
        }

        // Extract components that belong to this namespace
        Name nm = getComponents(name);
        String atom = nm.get(0);
        Element in = lookupElement(atom);

        if (nm.size() == 1) {
            binding.removeChild(in);
            // just in case it's a context
            context.remove(atom);
        } else {
            getSubContext(in, atom).unbind(nm.getSuffix(1));
        }
    }

    /**
     * @see javax.naming.Context#unbind(java.lang.String)
     */
    public void unbind(String name) throws NamingException {
        unbind(new CompositeName(name));
    }

    /**
     * @see javax.naming.Context#rename(javax.naming.Name, javax.naming.Name)
     */
    public void rename(Name oldName, Name newName) throws NamingException {
        if (oldName.isEmpty() && newName.isEmpty()) {
            throw new InvalidNameException("empty name");
        }

        // Just bind and unbind, in that order. Note that this creates a new
        // version element, just like in CVS.
        Object obj = lookup(oldName);
        bind(newName, obj);
        unbind(oldName);
    }

    /**
     * @see javax.naming.Context#rename(java.lang.String, java.lang.String)
     */
    public void rename(String oldName, String newName) throws NamingException {
        rename(new CompositeName(oldName), new CompositeName(newName));
    }

    /**
     * @see javax.naming.Context#list(javax.naming.Name)
     */
    public NamingEnumeration list(Name name) throws NamingException {
        if (name.isEmpty()) {
            // listing this context
            return new DOMNames(binding);
        } 

        // Perhaps 'name' names a context
        Object target = lookup(name);
        if (target instanceof Context) {
            return ((Context)target).list("");
        } else {
            throw new NotContextException(name.toString());
        }
    }

    /**
     * @see javax.naming.Context#list(java.lang.String)
     */
    public NamingEnumeration list(String name) throws NamingException {
        return list(new CompositeName(name));
    }

    /**
     * @see javax.naming.Context#listBindings(javax.naming.Name)
     */
    public NamingEnumeration listBindings(Name name) throws NamingException {
        if (name.isEmpty()) {
            // listing this context
            return new DOMBindings(binding, this);
        } 

        // Perhaps 'name' names a context
        Object target = lookup(name);
        if (target instanceof Context) {
            return ((Context)target).list("");
        } else {
            throw new NotContextException(name.toString());
        }
    }

    /**
     * @see javax.naming.Context#listBindings(java.lang.String)
     */
    public NamingEnumeration listBindings(String name) throws NamingException {
        return listBindings(new CompositeName(name));
    }

    /**
     * @see javax.naming.Context#destroySubcontext(javax.naming.Name)
     */
    public void destroySubcontext(Name name) throws NamingException {
        unbind(name);
    }

    /**
     * @see javax.naming.Context#destroySubcontext(java.lang.String)
     */
    public void destroySubcontext(String name) throws NamingException {
        destroySubcontext(new CompositeName(name));
    }

    /**
     * @see javax.naming.Context#createSubcontext(javax.naming.Name)
     */
    public Context createSubcontext(Name name) throws NamingException {
        if (name.isEmpty()) {
            throw new InvalidNameException("empty name");
        }

        // Extract components that belong to this namespace
        Name nm = getComponents(name);
        String atom = nm.get(0);
        Element in = lookupElement(atom);

        if (nm.size() == 1) {
            bind(new DocumentImpl(getEmpty()), in, atom);
            return getSubContext(lookupElement(atom), atom);
        } else {
            return getSubContext(in, atom).createSubcontext(nm.getSuffix(1));
        }
    }

    /**
     * @see javax.naming.Context#createSubcontext(java.lang.String)
     */
    public Context createSubcontext(String name) throws NamingException {
        return createSubcontext(new CompositeName(name));
    }

    /**
     * @see javax.naming.Context#lookupLink(javax.naming.Name)
     */
    public Object lookupLink(Name name) throws NamingException {
        return lookup(name);
    }

    /**
     * @see javax.naming.Context#lookupLink(java.lang.String)
     */
    public Object lookupLink(String name) throws NamingException {
        return lookupLink(new CompositeName(name));
    }

    /**
     * @see javax.naming.Context#getNameParser(javax.naming.Name)
     */
    public NameParser getNameParser(Name name) throws NamingException {
        return nameParser;
    }

    /**
     * @see javax.naming.Context#getNameParser(java.lang.String)
     */
    public NameParser getNameParser(String name) throws NamingException {
        return getNameParser(new CompositeName(name));
    }

    /**
     * @see javax.naming.Context#composeName(javax.naming.Name, javax.naming.Name)
     */
    public Name composeName(Name name, Name prefix) throws NamingException {
        Name result = (Name)prefix.clone();
        return result.addAll(name);
    }

    /**
     * @see javax.naming.Context#composeName(java.lang.String, java.lang.String)
     */
    public String composeName(String name, String prefix)
        throws NamingException {
            return composeName(new CompositeName(name),
                new CompositeName(prefix)).toString();
    }

    /**
     * @see javax.naming.Context#addToEnvironment(java.lang.String, java.lang.Object)
     */
    public Object addToEnvironment(String propName, Object propVal)
        throws NamingException {
        return environment.put(propName, propVal);
    }

    /**
     * @see javax.naming.Context#removeFromEnvironment(java.lang.String)
     */
    public Object removeFromEnvironment(String propName)
        throws NamingException {
        return environment.remove(propName);
    }

    /**
     * @see javax.naming.Context#getEnvironment()
     */
    public Hashtable getEnvironment() throws NamingException {
        return new Properties(environment);
    }

    /**
     * @see javax.naming.Context#close()
     */
    public void close() throws NamingException {
        // easy
    }

    /**
     * @see javax.naming.Context#getNameInNamespace()
     */
    public String getNameInNamespace() throws NamingException {
        if (parent == null) {
            return "";
        }
        
        DOMContext up = this;
        Name name = nameParser.parse("");
        while (up != null && up.atom != null) {
            name.add(0, up.atom);
        }
        
        return name.toString();
    }
    
    protected void bind(Object obj, Element old, String atom) throws NamingException {
        // Call getStateToBind for using any state factories
        obj = NamingManager.getStateToBind(obj,
            new CompositeName().add(atom), this, environment);
        if (obj != old) {
            binding.appendChild((Node)obj);
        }
    }

    protected DOMContext getSubContext(Element in, String atom) throws NumberFormatException, DOMException, NamingException {
        DOMContext sub = (DOMContext)context.get(atom);
        if (sub == null) {
            int id = getLatest(in);
            sub = new DOMContext(this, atom, new DocumentImpl(id));
            context.put(atom, sub);
        }
        return sub;
    }

    protected Name getComponents(Name name) throws NamingException {
        if (name instanceof CompositeName) {
            if (name.size() > 1) {
                throw new InvalidNameException(name.toString() +
                    " has more components than namespace can handle");
            }

            // Turn component that belongs to you into a compound name
            return nameParser.parse(name.get(0));
        } else {
            // Already parsed
            return name;
        }
    }
    
    protected Document getBinding() {
        return binding;
    }
    
    protected int getRoot() throws NamingException {
        try {
            try {
                // lookup root
                return VersionFactory.getInstance().getValue(0);
            } catch (NoSuchElementException ex) {
                int root = getEmpty();
                int comment = PlainTextFactory.getInstance()
                    .getPlainText("naming root");
                VersionFactory.getInstance().addVersion(0, root, comment);
                return root;
            }
        } catch (NamingException ex) {
            throw ex;
        } catch (Exception ex) {
            throw namingException(ex);
        }
    }
    
    private static int emptyId;
    
    public static int getEmpty() throws NamingException {
        if (emptyId != 0) {
            return emptyId;
        }
        
        try  {
            SAXFactory sf = new SAXFactory(ConnectionFactory.getInstance());
            return emptyId = sf.parse("<context/>");
        } catch (Exception ex) {
            throw namingException(ex);
        }
    }
    
    public static int getLatest(Element in) throws NamingException {
        try {
            return VersionFactory.getInstance().getValue(getVe(in));
        } catch (SQLException ex) {
            throw namingException(ex);
        }
    }
    
    private static int veTag;
    
    public static int getVe(Element el) throws NamingException {
        if (veTag == 0) {
            try {
                veTag = NodeFactory.getInstance().getName("", "ve");
            } catch (SQLException ex) {
                throw namingException(ex);
            }
        }
        
        return Integer.parseInt
            (((NamedNodeMapImpl)el.getAttributes())
                .getNamedItem(veTag).getNodeValue());
    }
    
    private static int idTag;
    
    public static int getId() throws NamingException {
        if (idTag == 0) {
            try {
                idTag = NodeFactory.getInstance().getName("", "id");
            } catch (SQLException ex) {
                throw namingException(ex);
            }
        }
        return idTag;
    }
    
    public static NamingException namingException(String message, Throwable t) {
        NamingException result = new NamingException(message);
        result.setRootCause(t);
        return result;
    }
    
    public static NamingException namingException(Throwable t) {
        NamingException result = new NamingException();
        result.setRootCause(t);
        return result;
    }
    
    public static class DOMNames implements NamingEnumeration {
        private int idTag;
        private int index;
        private NodeList nl;
        
        public DOMNames(Node node) throws NamingException {
            this.nl = node.getChildNodes();
            this.index = 0;
            this.idTag = getId();
        }

        /**
         * @see javax.naming.NamingEnumeration#next()
         */
        public Object next() throws NamingException {
            if (index < nl.getLength()) {
                NamedNodeMapImpl nn = (NamedNodeMapImpl)
                    nl.item(index++).getAttributes();
                String name = nn.getNamedItem(idTag)
                    .getNodeValue();
                return new NameClassPair(name, Document.class.getName());
            } else {
                throw new NoSuchElementException();
            }
        }

        /**
         * @see javax.naming.NamingEnumeration#hasMore()
         */
        public boolean hasMore() {
            return index < nl.getLength();
        }

        /**
         * @see javax.naming.NamingEnumeration#close()
         */
        public void close() {
            // easy
        }

        /**
         * @see java.util.Enumeration#hasMoreElements()
         */
        public boolean hasMoreElements() {
            return hasMore();
        }

        /**
         * @see java.util.Enumeration#nextElement()
         */
        public Object nextElement() {
            try {
                return next();
            } catch (NamingException ex) {
                throw new NoSuchElementException(ex.toString());
            }
        }
    }
    
    public static class DOMBindings extends DOMNames {
        private DOMContext ctx;
        
        /**
         * @param node
         * @throws NamingException
         */
        public DOMBindings(Node node, DOMContext ctx) throws NamingException {
            super(node);
            this.ctx = ctx;
        }
        
        /**
         * @see javax.naming.NamingEnumeration#next()
         */
        public Object next() throws NamingException {
            String name = ((NameClassPair)super.next()).getName();
            return new Binding(name, ctx.lookup(name));
        }

    }
    
    public static class SimpleNameParser implements NameParser {
        
        public static final String SYNTAX_PREFIX =
            "net.sourceforge.fraglets.zeig.jndi.syntax.";
        
        public static final int LENGTH_PREFIX =
            SYNTAX_PREFIX.length() - "jndi.syntax.".length();
        
        private Properties syntax;
        
        public SimpleNameParser(Properties defaults) {
            init(defaults);
        }
        
        protected void init(Properties defaults) {
            syntax = new Properties();
            for (Iterator i = defaults.entrySet().iterator(); i.hasNext();) {
                Map.Entry element = (Map.Entry)i.next();
                String key = element.getKey().toString();
                if (key.startsWith(SYNTAX_PREFIX)) {
                    syntax.setProperty(key.substring(LENGTH_PREFIX),
                        element.getValue().toString());
                }
            }
        }
        
        /**
         * @see javax.naming.NameParser#parse(java.lang.String)
         */
        public Name parse(String name) throws NamingException {
            return new CompoundName(name, syntax);
        }
    }
}

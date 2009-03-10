/*
 * ZeigDatabaseNode.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.sourceforge.fraglets.zeig.eclipse.views;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingException;

import net.sourceforge.fraglets.zeig.eclipse.BrowserPlugin;
import net.sourceforge.fraglets.zeig.eclipse.CorePlugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;

/**
 * @author marion@users.souceforge.net
 * @version $Revision: 1.3 $
 */
public class ZeigDatabaseNode extends ZeigContainerNode implements IProject {
    private Hashtable env;

    /**
     * @param env
     * @throws NamingException
     */
    protected ZeigDatabaseNode(Hashtable env) throws NamingException {
        this((String)env.get(Context.PROVIDER_URL));
        this.env = env;
        this.ctx = CorePlugin.getDefault().newContext(env);
    }

    /**
     * @param env
     * @throws NamingException
     */
    protected ZeigDatabaseNode(String name) {
        super(name);
        this.path = Path.ROOT;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#build(int, java.lang.String, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void build(int kind, String builderName, Map args, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        BrowserPlugin.warn("build(int kind, String builderName, Map args, IProgressMonitor monitor)", null);
        throw new UnsupportedOperationException("build(int kind, String builderName, Map args, IProgressMonitor monitor)");
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#build(int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void build(int kind, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        BrowserPlugin.warn("build(int kind, IProgressMonitor monitor)", null);
        throw new UnsupportedOperationException("build(int kind, IProgressMonitor monitor)");
    }

    /**
     * @see org.eclipse.core.resources.IProject#close(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void close(IProgressMonitor monitor) throws CoreException {
        monitor.beginTask("closing "+getName(), 1);
        try {
            children = null;
            ctx.close();
            monitor.worked(1);
            ctx = null;
        } catch (NamingException ex) {
            throw coreException("closing project", ex);
        } finally {
            monitor.done();
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#create(org.eclipse.core.resources.IProjectDescription, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void create(IProjectDescription description, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        BrowserPlugin.warn("create(IProjectDescription description, IProgressMonitor monitor)", null);
        throw new UnsupportedOperationException("create(IProjectDescription description, IProgressMonitor monitor)");
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#create(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void create(IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        BrowserPlugin.warn("create(IProgressMonitor monitor)", null);
        throw new UnsupportedOperationException("create(IProgressMonitor monitor)");
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#getDescription()
     */
    public IProjectDescription getDescription() throws CoreException {
        // TODO Auto-generated method stub
        BrowserPlugin.warn("getDescription()", null);
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#getNature(java.lang.String)
     */
    public IProjectNature getNature(String natureId) throws CoreException {
        // TODO Auto-generated method stub
        BrowserPlugin.warn("getNature(String natureId)", null);
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#getPluginWorkingLocation(org.eclipse.core.runtime.IPluginDescriptor)
     */
    public IPath getPluginWorkingLocation(IPluginDescriptor plugin) {
        // TODO Auto-generated method stub
        BrowserPlugin.warn("getPluginWorkingLocation(IPluginDescriptor plugin)", null);
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#getReferencedProjects()
     */
    public IProject[] getReferencedProjects() throws CoreException {
        // TODO Auto-generated method stub
        BrowserPlugin.warn("getReferencedProjects()", null);
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#getReferencingProjects()
     */
    public IProject[] getReferencingProjects() {
        // TODO Auto-generated method stub
        BrowserPlugin.warn("getReferencingProjects()", null);
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#hasNature(java.lang.String)
     */
    public boolean hasNature(String natureId) throws CoreException {
        // TODO Auto-generated method stub
        BrowserPlugin.warn("hasNature(String natureId)", null);
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#isNatureEnabled(java.lang.String)
     */
    public boolean isNatureEnabled(String natureId) throws CoreException {
        // TODO Auto-generated method stub
        BrowserPlugin.warn("isNatureEnabled(String natureId)", null);
        return false;
    }

    /**
     * @see org.eclipse.core.resources.IProject#isOpen()
     */
    public boolean isOpen() {
        return ctx != null;
    }

    /**
     * @see org.eclipse.core.resources.IProject#move(org.eclipse.core.resources.IProjectDescription, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void move(IProjectDescription description, boolean force, IProgressMonitor monitor) throws CoreException {
        move(description, (force ? FORCE : IResource.NONE), monitor);
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#open(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void open(IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        BrowserPlugin.warn("open(IProgressMonitor monitor)", null);
        if (!isOpen()) {
            monitor = BrowserPlugin.monitor(monitor);
            monitor.beginTask("Opening "+getName(), 1);
            try {
                ctx = CorePlugin.getDefault().newContext(env);
                monitor.worked(1);
                children = null;
                getViewer().refresh();
            } catch (NamingException ex) {
                throw coreException("opening "+getName(), ex);
            } finally {
                monitor.done();
            }
        }
    }

    /**
     * @see org.eclipse.core.resources.IProject#setDescription(org.eclipse.core.resources.IProjectDescription, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void setDescription(IProjectDescription description, IProgressMonitor monitor) throws CoreException {
        setDescription(description, KEEP_HISTORY, monitor);
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#setDescription(org.eclipse.core.resources.IProjectDescription, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void setDescription(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        BrowserPlugin.warn("setDescription(IProjectDescription description, int updateFlags, IProgressMonitor monitor)", null);
    }

    /**
     * @see org.eclipse.core.resources.IResource#getType()
     */
    public int getType() {
        return PROJECT;
    }

    /**
     * @see net.sourceforge.fraglets.zeig.eclipse.views.ZeigNode#getRootNode()
     */
    public ZeigRootNode getRootNode() {
        return (ZeigRootNode)getParent();
    }

    /**
     * @see net.sourceforge.fraglets.zeig.eclipse.views.ZeigContainerNode#validate()
     */
    public void validate() {
        if (ctx != null) {
            super.validate();
        } else {
            children = new ArrayList();
        }
    }

    /**
     * @see org.eclipse.core.resources.IResource#getSessionProperty(org.eclipse.core.runtime.QualifiedName)
     */
    public Object getSessionProperty(QualifiedName key) throws CoreException {
        return getProperty(key.toString());
    }
    
    public Object getProperty(Object key) {
        return env.get(key);
    }

    /**
     * @see org.eclipse.core.resources.IResource#setSessionProperty(org.eclipse.core.runtime.QualifiedName, java.lang.Object)
     */
    public void setSessionProperty(QualifiedName key, Object value)
        throws CoreException {
        setProperty(key.toString(), value);
    }
    
    public Object setProperty(Object key, Object value) {
        if (env == null) {
            env = new Hashtable();
            // TODO find a better location
            env.put(Context.PROVIDER_URL, getName());
            env.put(Context.URL_PKG_PREFIXES, "net.sourceforge.fraglets");
        }
        return env.put(key, value);
    }

    /**
     * @see org.eclipse.core.resources.IResource#isAccessible()
     */
    public boolean isAccessible() {
        return isOpen() && super.isAccessible();
    }

}

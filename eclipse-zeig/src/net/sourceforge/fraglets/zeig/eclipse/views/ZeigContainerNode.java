/*
 * ZeigContainerNode.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on May 29, 2003 by unknown
 */
package net.sourceforge.fraglets.zeig.eclipse.views;

import java.util.ArrayList;
import java.util.Iterator;

import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import net.sourceforge.fraglets.zeig.eclipse.CorePlugin;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.TreeViewer;

/**
 * @author unknown
 */
class ZeigContainerNode extends ZeigNode implements IFolder {
    protected ArrayList children;
    protected IPath path;
    protected Context ctx;
    
    public ZeigContainerNode(String name) {
        super(name);
    }
    public Context getContext() throws NamingException {
        if (ctx != null) {
            return ctx;
        } else {
            Object obj = CorePlugin.getDefault()
                .lookup(getParentNode().getContext(), getName());
            if (obj instanceof Context) {
                return ctx = (Context)obj;
            } else {
                throw new ClassCastException(obj.getClass().getName());
            }
        }
    }
    public boolean isValid() {
        return children != null;
    }
    public void validate() {
        try {
            children = new ArrayList();
            NamingEnumeration e =  CorePlugin.getDefault()
                .list(getContext(), "");
            while (e.hasMore()) {
                ZeigNode child;
                NameClassPair ncp = (NameClassPair)e.next();
                try {
                    if (Context.class.isAssignableFrom(Class
                        .forName(ncp.getClassName()))) {
                        child = new ZeigContainerNode(ncp.getName());
                        children.add(child);
                        child.setParent(this);
                        continue;
                    }
                } catch (ClassNotFoundException ex) {
                }
                child = new ZeigElementNode(ncp.getName());
                children.add(child);
                child.setParent(this);
                continue;
            }
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        } catch (NamingException ex) {
            ex.printStackTrace();
        }
    }
    public void addChild(ZeigNode child) {
        children.add(child);
        child.setParent(this);
        if (getViewer() instanceof TreeViewer) {
            System.out.println("notify add child "+child+" to "+this);
            ((TreeViewer)getViewer()).add(this, child);
        }
    }
    public void removeChild(ZeigNode child) {
        children.remove(child);
        child.setParent(null);
        if (getViewer() instanceof TreeViewer) {
            System.out.println("notify remove child "+child+" from "+this);
            ((TreeViewer)getViewer()).remove(child);
        }
    }
    public ZeigNode[] getChildren() {
        if (!isValid()) {
            validate();
        }
        return (ZeigNode[])children.toArray(
            new ZeigNode[children.size()]);
    }
    public boolean hasChildren() {
        if (!isValid()) {
            validate();
        }
        return children.size() > 0;
    }
    
    /**
     * @see org.eclipse.core.resources.IResource#copy(org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void copy(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException {
        try {
            Context ctx;
            if (destination.isAbsolute()) {
                ctx = getRootNode().getContext();
            } else {
                ctx = getParentNode().getContext();
            }
            CorePlugin.getDefault().createSubContext(ctx, destination.toString());
            monitor.worked(1);
            if (hasChildren()) {
                for (Iterator i = children.iterator(); i.hasNext();) {
                    ZeigNode element = (ZeigNode)i.next();
                    element.copy(destination.append(element.getName()), updateFlags, monitor);
                }
            }
        } catch (NamingException ex) {
            throw coreException("copy failed", ex);
        }
        
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IContainer#exists(org.eclipse.core.runtime.IPath)
     */
    public boolean exists(IPath path) {
        // TODO Auto-generated method stub
        return false;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IContainer#findMember(java.lang.String)
     */
    public IResource findMember(String name) {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IContainer#findMember(java.lang.String, boolean)
     */
    public IResource findMember(String name, boolean includePhantoms) {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IContainer#findMember(org.eclipse.core.runtime.IPath)
     */
    public IResource findMember(IPath path) {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IContainer#findMember(org.eclipse.core.runtime.IPath, boolean)
     */
    public IResource findMember(IPath path, boolean includePhantoms) {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IContainer#getFile(org.eclipse.core.runtime.IPath)
     */
    public IFile getFile(IPath path) {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IContainer#getFolder(org.eclipse.core.runtime.IPath)
     */
    public IFolder getFolder(IPath path) {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IContainer#members()
     */
    public IResource[] members() throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IContainer#members(boolean)
     */
    public IResource[] members(boolean includePhantoms) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IContainer#members(int)
     */
    public IResource[] members(int memberFlags) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IContainer#findDeletedMembersWithHistory(int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public IFile[] findDeletedMembersWithHistory(int depth, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.eclipse.core.resources.IResource#getFullPath()
     */
    public IPath getFullPath() {
        if (path != null) {
            return path;
        } else {
            return path = super.getFullPath();
        }
    }
    /**
     * @see org.eclipse.core.resources.IResource#getType()
     */
    public int getType() {
        return FOLDER;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFolder#create(boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void create(boolean force, boolean local, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFolder#create(int, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void create(int updateFlags, boolean local, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFolder#createLink(org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void createLink(IPath localLocation, int updateFlags, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFolder#delete(boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void delete(boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFolder#getFile(java.lang.String)
     */
    public IFile getFile(String name) {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFolder#getFolder(java.lang.String)
     */
    public IFolder getFolder(String name) {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFolder#move(org.eclipse.core.runtime.IPath, boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void move(IPath destination, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
    }

}
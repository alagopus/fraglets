/*
 * ZeigNode.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on May 29, 2003 by unknown
 */
package net.sourceforge.fraglets.zeig.eclipse.views;


import javax.naming.NamingException;

import net.sourceforge.fraglets.zeig.eclipse.BrowserPlugin;
import net.sourceforge.fraglets.zeig.eclipse.CorePlugin;

import org.apache.xml.utils.WrappedRuntimeException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author unknown
 */
/*
 * The content provider class is responsible for
 * providing objects to the view. It can wrap
 * existing objects in adapters or simply return
 * objects as-is. These objects may be sensitive
 * to the current input of the view, or ignore
 * it and always show the same content 
 * (like Task List, for example).
 */

abstract class ZeigNode extends PlatformObject implements IResource {
    private String name;
    private ZeigContainerNode parent;

    public ZeigNode(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setParent(ZeigContainerNode parent) {
        this.parent = parent;
    }
    public IContainer getParent() {
        return parent;
    }
    public ZeigContainerNode getParentNode() {
        return parent;
    }
    public String toString() {
        return getName();
    }
    public ZeigRootNode getRootNode() {
        ZeigNode scan = this;
        while (scan != null) {
            if (scan instanceof ZeigRootNode) {
                return (ZeigRootNode)scan;
            }
        }
        throw new IllegalStateException("no root node");
    }
    public Viewer getViewer() {
        return getRootNode().getViewer();
    }
    /**
     * not implemented
     * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceProxyVisitor, int)
     */
    public void accept(IResourceProxyVisitor visitor, int memberFlags) throws CoreException {
        // TODO not implemented
        throw new UnsupportedOperationException();
    }
    /**
     * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceVisitor)
     */
    public void accept(IResourceVisitor visitor) throws CoreException {
        accept(visitor, NONE, DEPTH_INFINITE);
    }
    /**
     * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceVisitor, int, boolean)
     */
    public void accept(IResourceVisitor visitor, int depth, boolean includePhantoms) throws CoreException {
        accept(visitor, depth, includePhantoms ? IContainer.INCLUDE_PHANTOMS : IResource.NONE);
    }
    /**
     * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceVisitor, int, int)
     */
    public void accept(IResourceVisitor visitor, int depth, int memberFlags) throws CoreException {
        if (!((memberFlags & IContainer.INCLUDE_PHANTOMS) == 0 && isPhantom()) &&
            !((memberFlags & IContainer.INCLUDE_TEAM_PRIVATE_MEMBERS) == 0 && isTeamPrivateMember())) {
            visitor.visit(this);
        }
    }
    /**
     * not implemented
     * @see org.eclipse.core.resources.IResource#clearHistory(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void clearHistory(IProgressMonitor monitor) throws CoreException {
        // TODO not implemented
    }
    /**
     * @see org.eclipse.core.resources.IResource#copy(org.eclipse.core.runtime.IPath, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void copy(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {
        copy(destination, (force ? FORCE : NONE), monitor);
    }
    /**
     * @see org.eclipse.core.resources.IResource#copy(org.eclipse.core.resources.IProjectDescription, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void copy(IProjectDescription description, boolean force, IProgressMonitor monitor) throws CoreException {
        copy(description, (force ? FORCE : IResource.NONE), monitor);
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#copy(org.eclipse.core.resources.IProjectDescription, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void copy(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        BrowserPlugin.log("copy(IProjectDescription description, int updateFlags, IProgressMonitor monitor)", null);
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#createMarker(java.lang.String)
     */
    public IMarker createMarker(String type) throws CoreException {
        // TODO Auto-generated method stub
        BrowserPlugin.log("createMarker(String type)", null);
        return null;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#delete(boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void delete(boolean force, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        BrowserPlugin.log("delete(boolean force, IProgressMonitor monitor)", null);
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#delete(int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void delete(int updateFlags, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        BrowserPlugin.log("delete(int updateFlags, IProgressMonitor monitor)", null);
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#deleteMarkers(java.lang.String, boolean, int)
     */
    public void deleteMarkers(String type, boolean includeSubtypes, int depth) throws CoreException {
        // TODO Auto-generated method stub
        BrowserPlugin.log("deleteMarkers(String type, boolean includeSubtypes, int depth)", null);
    }
    /**
     * @see org.eclipse.core.resources.IResource#exists()
     */
    public boolean exists() {
        try {
            return null != CorePlugin.getDefault()
                .lookup(getParentNode().getContext(), getName());
        } catch (NamingException ex) {
            throw new WrappedRuntimeException(ex);
        }
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#findMarker(long)
     */
    public IMarker findMarker(long id) throws CoreException {
        // TODO Auto-generated method stub
        BrowserPlugin.log("findMarker(long id)", null);
        return null;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#findMarkers(java.lang.String, boolean, int)
     */
    public IMarker[] findMarkers(String type, boolean includeSubtypes, int depth) throws CoreException {
        // TODO Auto-generated method stub
        BrowserPlugin.log("findMarkers(String type, boolean includeSubtypes, int depth)", null);
        return null;
    }
    /**
     * @see org.eclipse.core.resources.IResource#getFileExtension()
     */
    public String getFileExtension() {
        int dot = getName().lastIndexOf('.') + 1;
        return dot > 0 ? getName().substring(dot) : null;
    }
    /**
     * @see org.eclipse.core.resources.IResource#getFullPath()
     */
    public IPath getFullPath() {
        return getParent().getFullPath().append(getName());
    }
    /**
     * @see org.eclipse.core.resources.IResource#getLocation()
     */
    public IPath getLocation() {
        // not inside workspace
        BrowserPlugin.log("getLocation()", null);
        return null;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getMarker(long)
     */
    public IMarker getMarker(long id) {
        // TODO Auto-generated method stub
        BrowserPlugin.log("getMarker(long id)", null);
        return null;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getModificationStamp()
     */
    public long getModificationStamp() {
        // TODO Auto-generated method stub
        BrowserPlugin.log("getModificationStamp()", null);
        return 0;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getPersistentProperty(org.eclipse.core.runtime.QualifiedName)
     */
    public String getPersistentProperty(QualifiedName key) throws CoreException {
        // TODO Auto-generated method stub
        BrowserPlugin.log("getPersistentProperty(QualifiedName key)", null);
        return null;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getProject()
     */
    public IProject getProject() {
        // TODO Auto-generated method stub
        BrowserPlugin.log("getProject()", null);
        return getWorkspace().getRoot().getProject(getFullPath().segment(0));
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getProjectRelativePath()
     */
    public IPath getProjectRelativePath() {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getRawLocation()
     */
    public IPath getRawLocation() {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getSessionProperty(org.eclipse.core.runtime.QualifiedName)
     */
    public Object getSessionProperty(QualifiedName key) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }
    /**
     * @see org.eclipse.core.resources.IResource#getWorkspace()
     */
    public IWorkspace getWorkspace() {
        BrowserPlugin.log("getProject()", null);
        return ResourcesPlugin.getWorkspace();
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isAccessible()
     */
    public boolean isAccessible() {
        // TODO Auto-generated method stub
        return false;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isDerived()
     */
    public boolean isDerived() {
        // TODO Auto-generated method stub
        return false;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isLocal(int)
     */
    public boolean isLocal(int depth) {
        // TODO Auto-generated method stub
        return false;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isLinked()
     */
    public boolean isLinked() {
        // TODO Auto-generated method stub
        return false;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isPhantom()
     */
    public boolean isPhantom() {
        // TODO Auto-generated method stub
        return false;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isReadOnly()
     */
    public boolean isReadOnly() {
        // TODO Auto-generated method stub
        return false;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isSynchronized(int)
     */
    public boolean isSynchronized(int depth) {
        // TODO Auto-generated method stub
        return false;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isTeamPrivateMember()
     */
    public boolean isTeamPrivateMember() {
        // TODO Auto-generated method stub
        return false;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#move(org.eclipse.core.runtime.IPath, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void move(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#move(org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void move(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#move(org.eclipse.core.resources.IProjectDescription, boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void move(IProjectDescription description, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#move(org.eclipse.core.resources.IProjectDescription, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void move(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#refreshLocal(int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void refreshLocal(int depth, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setDerived(boolean)
     */
    public void setDerived(boolean isDerived) throws CoreException {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setLocal(boolean, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void setLocal(boolean flag, int depth, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setPersistentProperty(org.eclipse.core.runtime.QualifiedName, java.lang.String)
     */
    public void setPersistentProperty(QualifiedName key, String value) throws CoreException {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setReadOnly(boolean)
     */
    public void setReadOnly(boolean readOnly) {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setSessionProperty(org.eclipse.core.runtime.QualifiedName, java.lang.Object)
     */
    public void setSessionProperty(QualifiedName key, Object value) throws CoreException {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setTeamPrivateMember(boolean)
     */
    public void setTeamPrivateMember(boolean isTeamPrivate) throws CoreException {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#touch(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void touch(IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
    }
    
    protected CoreException coreException(String message, Exception ex) {
        return new CoreException(new Status(IStatus.ERROR,
            BrowserPlugin.ID, IStatus.OK/*TODO*/, message, ex));
    }
}
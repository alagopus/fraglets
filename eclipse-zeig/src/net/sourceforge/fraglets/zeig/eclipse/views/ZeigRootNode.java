/*
 * ZeigRootNode.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on May 29, 2003 by unknown
 */
package net.sourceforge.fraglets.zeig.eclipse.views;

import java.util.ArrayList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author unknown
 */
public class ZeigRootNode extends ZeigContainerNode implements IWorkspaceRoot {
    ZeigContentProvider viewContentProvider;
    /**
     * 
     */
    public ZeigRootNode() {
        super("");
        children = new ArrayList();
    }
    /**
     * @return
     */
    public ZeigContentProvider getViewContentProvider() {
        return viewContentProvider;
    }

    /**
     * @param provider
     */
    public void setViewContentProvider(ZeigContentProvider provider) {
        viewContentProvider = provider;
    }

    /**
     * @see org.eclipse.core.resources.IResource#getFullPath()
     */
    public IPath getFullPath() {
        return Path.ROOT;
    }

    /**
     * @see net.sourceforge.fraglets.zeig.eclipse.views.ZeigNode#getViewer()
     */
    public Viewer getViewer() {
        return getViewContentProvider().getViewer();
    }

    /**
     * @see org.eclipse.core.resources.IResource#getType()
     */
    public int getType() {
        return ROOT;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IWorkspaceRoot#delete(boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void delete(boolean deleteContent, boolean force, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IWorkspaceRoot#findContainersForLocation(org.eclipse.core.runtime.IPath)
     */
    public IContainer[] findContainersForLocation(IPath location) {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IWorkspaceRoot#findFilesForLocation(org.eclipse.core.runtime.IPath)
     */
    public IFile[] findFilesForLocation(IPath location) {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IWorkspaceRoot#getContainerForLocation(org.eclipse.core.runtime.IPath)
     */
    public IContainer getContainerForLocation(IPath location) {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IWorkspaceRoot#getFileForLocation(org.eclipse.core.runtime.IPath)
     */
    public IFile getFileForLocation(IPath location) {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IWorkspaceRoot#getProject(java.lang.String)
     */
    public IProject getProject(String name) {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IWorkspaceRoot#getProjects()
     */
    public IProject[] getProjects() {
        // TODO Auto-generated method stub
        return null;
    }

}
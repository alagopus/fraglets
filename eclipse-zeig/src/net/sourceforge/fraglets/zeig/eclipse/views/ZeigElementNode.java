/*
 * ZeigElementNode.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on May 29, 2003 by unknown
 */
package net.sourceforge.fraglets.zeig.eclipse.views;

import java.io.InputStream;

import javax.naming.Context;
import javax.naming.NamingException;

import net.sourceforge.fraglets.zeig.eclipse.CorePlugin;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author unknown
 */
public class ZeigElementNode extends ZeigNode implements IFile {

    /**
     * @param name
     */
    public ZeigElementNode(String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#appendContents(java.io.InputStream, boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void appendContents(InputStream source, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#appendContents(java.io.InputStream, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void appendContents(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
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
            CorePlugin.getDefault().bind(ctx, destination.toString(), ((IFile)this).getContents());
            monitor.worked(1);
        } catch (NamingException ex) {
            throw coreException("copy failed", ex);
        }
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#create(java.io.InputStream, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void create(InputStream source, boolean force, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#create(java.io.InputStream, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void create(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#createLink(org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void createLink(IPath localLocation, int updateFlags, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#delete(boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void delete(boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.eclipse.core.resources.IFile#getContents()
     */
    public InputStream getContents() throws CoreException {
        try {
            return CorePlugin.getDefault()
                .lookupStream(getParentNode().getContext(), getName());
        } catch (Exception ex) {
            throw coreException("copying "+getName(), ex);
        }
    }

    /**
     * @see org.eclipse.core.resources.IFile#getContents(boolean)
     */
    public InputStream getContents(boolean force) throws CoreException {
        return getContents();
    }

    /**
     * @see org.eclipse.core.resources.IFile#getEncoding()
     */
    public int getEncoding() throws CoreException {
        return ENCODING_UTF_8;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#getHistory(org.eclipse.core.runtime.IProgressMonitor)
     */
    public IFileState[] getHistory(IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#move(org.eclipse.core.runtime.IPath, boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void move(IPath destination, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#setContents(java.io.InputStream, boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void setContents(InputStream source, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#setContents(org.eclipse.core.resources.IFileState, boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void setContents(IFileState source, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#setContents(java.io.InputStream, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void setContents(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#setContents(org.eclipse.core.resources.IFileState, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void setContents(IFileState source, int updateFlags, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.eclipse.core.resources.IResource#getType()
     */
    public int getType() {
        return FILE;
    }

}

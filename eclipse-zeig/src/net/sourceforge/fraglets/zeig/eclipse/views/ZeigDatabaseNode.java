/*
 * ZeigDatabaseNode.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on May 29, 2003 by unknown
 */
package net.sourceforge.fraglets.zeig.eclipse.views;

import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingException;

import net.sourceforge.fraglets.zeig.eclipse.CorePlugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

/**
 * @author unknown
 */
public class ZeigDatabaseNode extends ZeigContainerNode implements IProject {

    /**
     * @param env
     * @throws NamingException
     */
    protected ZeigDatabaseNode(Hashtable env) throws NamingException {
        super((String)env.get(Context.PROVIDER_URL));
        this.ctx = CorePlugin.getDefault().newContext(env);
        this.path = Path.ROOT;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#build(int, java.lang.String, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void build(int kind, String builderName, Map args, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#build(int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void build(int kind, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#close(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void close(IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#create(org.eclipse.core.resources.IProjectDescription, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void create(IProjectDescription description, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#create(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void create(IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#getDescription()
     */
    public IProjectDescription getDescription() throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#getNature(java.lang.String)
     */
    public IProjectNature getNature(String natureId) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#getPluginWorkingLocation(org.eclipse.core.runtime.IPluginDescriptor)
     */
    public IPath getPluginWorkingLocation(IPluginDescriptor plugin) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#getReferencedProjects()
     */
    public IProject[] getReferencedProjects() throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#getReferencingProjects()
     */
    public IProject[] getReferencingProjects() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#hasNature(java.lang.String)
     */
    public boolean hasNature(String natureId) throws CoreException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#isNatureEnabled(java.lang.String)
     */
    public boolean isNatureEnabled(String natureId) throws CoreException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#isOpen()
     */
    public boolean isOpen() {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#move(org.eclipse.core.resources.IProjectDescription, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void move(IProjectDescription description, boolean force, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#open(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void open(IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#setDescription(org.eclipse.core.resources.IProjectDescription, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void setDescription(IProjectDescription description, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#setDescription(org.eclipse.core.resources.IProjectDescription, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void setDescription(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.eclipse.core.resources.IResource#getType()
     */
    public int getType() {
        return PROJECT;
    }

}

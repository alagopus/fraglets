/*
 * ZeigContentProvider.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
 * Created on May 29, 2003 by unknown
 */
package net.sourceforge.fraglets.zeig.eclipse.views;


import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author unknown
 */
class ZeigContentProvider
    implements IStructuredContentProvider, ITreeContentProvider {
    private ZeigRootNode invisibleRoot;
    private Viewer viewer;

    public void inputChanged(Viewer v, Object oldInput, Object newInput) {
        this.viewer = v;
    }
    public void dispose() {
    }
    public Object[] getElements(Object parent) {
        if (parent.equals(ResourcesPlugin.getWorkspace())) {
            if (invisibleRoot == null)
                initialize();
            return getChildren(invisibleRoot);
        }
        return getChildren(parent);
    }
    public Object getParent(Object child) {
        if (child instanceof ZeigNode) {
            return ((ZeigNode)child).getParent();
        }
        return null;
    }
    public Object[] getChildren(Object parent) {
        if (parent instanceof ZeigContainerNode) {
            return ((ZeigContainerNode)parent).getChildren();
        }
        return new Object[0];
    }
    public boolean hasChildren(Object parent) {
        if (parent instanceof ZeigContainerNode)
            return ((ZeigContainerNode)parent).hasChildren();
        return false;
    }
    /*
     * We will set up a dummy model to initialize tree heararchy.
     * In a real code, you will connect to a real model and
     * expose its hierarchy.
     */
    private void initialize() {
        invisibleRoot = new ZeigRootNode();
        invisibleRoot.setViewContentProvider(this);
    }
    /**
     * @param root
     */
    protected void addRoot(ZeigContainerNode root) {
        invisibleRoot.addChild(root);
        viewer.refresh();
    }
    /**
     * @return
     */
    public Viewer getViewer() {
        return viewer;
    }

}
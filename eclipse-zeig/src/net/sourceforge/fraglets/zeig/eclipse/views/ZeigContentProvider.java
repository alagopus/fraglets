/*
 * ZeigContentProvider.java -
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 *
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


import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author marion@users.souceforge.net
 * @version $Revision: 1.3 $
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
    }
    /**
     * @return
     */
    public Viewer getViewer() {
        return viewer;
    }

}
/*
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

import javax.naming.Context;

import net.sourceforge.fraglets.zeig.eclipse.BrowserPlugin;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.OpenFileAction;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

/**
 * @author marion@users.souceforge.net
 * @version $Revision: 1.3 $
 */
public class ZeigView extends ViewPart {
    private TreeViewer viewer;
    private DrillDownAdapter drillDownAdapter;
    private Action connectAction;
    private Action openAction;
    private Action action2;
    private OpenFileAction doubleClickAction;

    class ViewLabelProvider extends LabelProvider {

        public String getText(Object obj) {
            return obj.toString();
        }
        public Image getImage(Object obj) {
            String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
            if (obj instanceof ZeigContainerNode)
                imageKey = ISharedImages.IMG_OBJ_FOLDER;
            return PlatformUI.getWorkbench().getSharedImages().getImage(
                imageKey);
        }
    }
    class NameSorter extends ViewerSorter {
    }

    /**
     * The constructor.
     */
    public ZeigView() {
    }

    /**
     * This is a callback that will allow us
     * to create the viewer and initialize it.
     */
    public void createPartControl(Composite parent) {
        viewer =
            new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        ZeigContentProvider vcp = new ZeigContentProvider();
        drillDownAdapter = new DrillDownAdapter(viewer);
        viewer.setContentProvider(vcp);
        viewer.setLabelProvider(new ViewLabelProvider());
//        viewer.setLabelProvider(
//        new DecoratingLabelProvider(
//            new WorkbenchLabelProvider(),
//            BrowserPlugin.getDefault().getWorkbench().getDecoratorManager().getLabelDecorator()));
        viewer.setSorter(new NameSorter());
        viewer.setInput(ResourcesPlugin.getWorkspace());
        makeActions(vcp);
        hookContextMenu();
        hookDoubleClickAction();
        contributeToActionBars();
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                ZeigView.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalPullDown(IMenuManager manager) {
        manager.add(connectAction);
        manager.add(new Separator());
        manager.add(action2);
    }

    private void fillContextMenu(IMenuManager manager) {
        manager.add(connectAction);
        manager.add(openAction);
        manager.add(action2);
        manager.add(new Separator());
        drillDownAdapter.addNavigationActions(manager);
        // Other plug-ins can contribute there actions here
        manager.add(new Separator("Additions"));
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(connectAction);
        manager.add(action2);
        manager.add(new Separator());
        drillDownAdapter.addNavigationActions(manager);
    }

    private void makeActions(final ZeigContentProvider vcp) {
        connectAction = new Action() {
            public void run() {
                IDialogSettings settings = getSection("connectAction");
                InputDialog id = new InputDialog(getShell(), "Connect",
                    "Provider URL: ", settings.get(Context.PROVIDER_URL), null);
                id.setBlockOnOpen(true);
                if (id.open() == InputDialog.CANCEL) {
                    return;
                }
                String url = id.getValue();
                settings.put(Context.PROVIDER_URL, url);
                vcp.addRoot(new ZeigDatabaseNode(url));
            }
        };
        connectAction.setText("Connect database...");
        connectAction.setToolTipText("Connect a zeig XML database");
        connectAction.setImageDescriptor(
            PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
                ISharedImages.IMG_TOOL_NEW_WIZARD));
                
        openAction = new Action() {
            public void run() {
                IStructuredSelection selection = (IStructuredSelection)
                    viewer.getSelection();
                if (selection.getFirstElement() instanceof ZeigDatabaseNode) {
                    ZeigDatabaseNode node = (ZeigDatabaseNode)selection
                        .getFirstElement();
                    String url = node.getName();
                    IDialogSettings settings = getSection("openAction");
                    InputDialog id = new InputDialog(getShell(), "Open",
                        "Principal: ", settings.get(Context.SECURITY_PRINCIPAL), null);
                    id.setBlockOnOpen(true);
                    if (id.open() == InputDialog.CANCEL) {
                        return;
                    }
                    String principal = id.getValue();
                    String credentials = null;
                    if (settings.get(Context.SECURITY_PRINCIPAL) != null) {
                        if (principal.equals(settings.get(Context.SECURITY_PRINCIPAL))) {
                            credentials = settings.get(Context.SECURITY_CREDENTIALS);
                        } else {
                            settings.put(Context.SECURITY_CREDENTIALS, (String)null);
                        }
                    }
                    settings.put(Context.SECURITY_PRINCIPAL, principal);
                    node.setProperty(Context.SECURITY_PRINCIPAL, principal);
                    node.setProperty(Context.SECURITY_AUTHENTICATION, "simple");
                    if (credentials != null) {
                        node.setProperty(Context.SECURITY_CREDENTIALS, credentials);
                    } else {
                        try {
                            // try without credentials
                            node.open(null);
                            return; // fine
                        } catch (CoreException ex) {
                            // we probably need credentials
                            id = new InputDialog(getShell(), "Open",
                                "Credentials: ", null, null);
                            id.setBlockOnOpen(true);
                            if (id.open() == InputDialog.CANCEL) {
                                return;
                            }
                            credentials = id.getValue();
                            node.setProperty(Context.SECURITY_CREDENTIALS, credentials);
                        }
                    }
                    try {
                        // try without credentials
                        node.open(null);
                        settings.put(Context.SECURITY_CREDENTIALS, credentials);
                        return; // fine
                    } catch (CoreException ex) {
                        BrowserPlugin.error("open", ex);
                    }
                }
            }
        };
        openAction.setText("Open database...");
        openAction.setToolTipText("Open a zeig XML database");

        action2 = new Action() {
            public void run() {
                showMessage("Action 2 executed");
            }
        };
        action2.setText("Action 2");
        action2.setToolTipText("Action 2 tooltip");
        action2.setImageDescriptor(
            PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
                ISharedImages.IMG_OBJS_TASK_TSK));
        
        doubleClickAction = new OpenFileAction(getSite().getPage());
    }

    private void hookDoubleClickAction() {
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                IStructuredSelection selection = (IStructuredSelection)
                    viewer.getSelection();
                if (selection.getFirstElement() instanceof ZeigDatabaseNode) {
                    openAction.run();
                } else {
                    doubleClickAction.selectionChanged(selection);
                    doubleClickAction.run();
                }
            }
        });
    }
    
    private void showMessage(String message) {
        MessageDialog.openInformation(
            viewer.getControl().getShell(),
            "Zeig Editing",
            message);
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus() {
        viewer.getControl().setFocus();
    }
    
    protected IDialogSettings getSection(String name) {
        IDialogSettings settings = BrowserPlugin.getDefault()
            .getDialogSettings();
        IDialogSettings result = settings.getSection(getClass().getName());
        if (result == null) {
            result = new DialogSettings(getClass().getName());
            settings.addSection(result);
        }
        settings = result;
        result = settings.getSection(name);
        if (result == null) {
            result = new DialogSettings(name);
            settings.addSection(result);
        }
        return result;
    }
    
    protected Shell getShell() {
        return BrowserPlugin.getDefault().getWorkbench()
            .getActiveWorkbenchWindow().getShell();
    }
}
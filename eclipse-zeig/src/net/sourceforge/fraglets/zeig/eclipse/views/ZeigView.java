/*
 * Copyright (C) 2003 Klaus Rennecke, all rights reserved.
 */
package net.sourceforge.fraglets.zeig.eclipse.views;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
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
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.OpenFileAction;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class ZeigView extends ViewPart {
    private TreeViewer viewer;
    private DrillDownAdapter drillDownAdapter;
    private Action connectAction;
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
                if (MessageDialog.openConfirm(viewer.getControl().getShell(),
                    "Zeig Editing", "Connect database?")) {
                    try {
                        String url = "zeig://simpel@tukan/";
                        Hashtable env = new Hashtable();
                        env.put(Context.PROVIDER_URL, url);
                        env.put(Context.URL_PKG_PREFIXES, "net.sourceforge.fraglets");
//                        env.put(Context.INITIAL_CONTEXT_FACTORY, "net.sourceforge.fraglets.zeig.jndi.DOMContextFactory");
                        env.put(Context.SECURITY_AUTHENTICATION, "simple");
                        env.put(Context.SECURITY_PRINCIPAL, "simpel");
                        env.put(Context.SECURITY_CREDENTIALS, "simpel");
//                        env.put(Context.OBJECT_FACTORIES, "net.sourceforge.fraglets.zeig.jndi.DOMObjectFactory");
//                        env.put(Context.STATE_FACTORIES, "net.sourceforge.fraglets.zeig.jndi.DOMStateFactory");
                        vcp.addRoot(new ZeigDatabaseNode(env));
                    } catch (Error ex) {
                        ex.printStackTrace();
                        throw ex;
                    } catch (RuntimeException ex) {
                        ex.printStackTrace();
                        throw ex;
                    } catch (NamingException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };
        connectAction.setImageDescriptor(
            PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
                ISharedImages.IMG_TOOL_NEW_WIZARD));
        connectAction.setText("Connect database...");
        connectAction.setToolTipText("Connect to a zeig XML database");
        connectAction.setImageDescriptor(
            PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
                ISharedImages.IMG_TOOL_NEW_WIZARD));

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
                doubleClickAction.selectionChanged
                    ((IStructuredSelection)viewer.getSelection());
                doubleClickAction.run();
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
}
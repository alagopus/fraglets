/*
 * IntValueEditorBeanInfo.java - BeanInfo for IntValueEditor.
 * Copyright (C) 2000 Klaus Rennecke.
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
 * SOFTWARE.  */

package net.sourceforge.shelf.beans;

import java.beans.*;
import javax.swing.event.ChangeListener;

/** <p>BeanInfo for IntValueEditor.
 *
 * @author marion@users.sourceforge.net
 * @version $Revision: 1.2 $
 */
public class IntValueEditorBeanInfo extends SimpleBeanInfo {
            
  // Property identifiers //GEN-FIRST:Properties
  private static final int PROPERTY_components = 0;
  private static final int PROPERTY_focusCycleRoot = 1;
  private static final int PROPERTY_locale = 2;
  private static final int PROPERTY_inputMethodRequests = 3;
  private static final int PROPERTY_maximum = 4;
  private static final int PROPERTY_y = 5;
  private static final int PROPERTY_toolTipText = 6;
  private static final int PROPERTY_x = 7;
  private static final int PROPERTY_optimizedDrawingEnabled = 8;
  private static final int PROPERTY_value = 9;
  private static final int PROPERTY_paintingTile = 10;
  private static final int PROPERTY_lightweight = 11;
  private static final int PROPERTY_componentCount = 12;
  private static final int PROPERTY_label = 13;
  private static final int PROPERTY_extent = 14;
  private static final int PROPERTY_alignmentY = 15;
  private static final int PROPERTY_alignmentX = 16;
  private static final int PROPERTY_registeredKeyStrokes = 17;
  private static final int PROPERTY_minimumSize = 18;
  private static final int PROPERTY_width = 19;
  private static final int PROPERTY_UIClassID = 20;
  private static final int PROPERTY_displayable = 21;
  private static final int PROPERTY_showing = 22;
  private static final int PROPERTY_graphics = 23;
  private static final int PROPERTY_dropTarget = 24;
  private static final int PROPERTY_verifyInputWhenFocusTarget = 25;
  private static final int PROPERTY_bounds = 26;
  private static final int PROPERTY_background = 27;
  private static final int PROPERTY_rootPane = 28;
  private static final int PROPERTY_treeLock = 29;
  private static final int PROPERTY_topLevelAncestor = 30;
  private static final int PROPERTY_autoscrolls = 31;
  private static final int PROPERTY_accessibleContext = 32;
  private static final int PROPERTY_actionMap = 33;
  private static final int PROPERTY_nextFocusableComponent = 34;
  private static final int PROPERTY_foreground = 35;
  private static final int PROPERTY_insets = 36;
  private static final int PROPERTY_inputVerifier = 37;
  private static final int PROPERTY_inputContext = 38;
  private static final int PROPERTY_toolkit = 39;
  private static final int PROPERTY_componentOrientation = 40;
  private static final int PROPERTY_validateRoot = 41;
  private static final int PROPERTY_border = 42;
  private static final int PROPERTY_height = 43;
  private static final int PROPERTY_peer = 44;
  private static final int PROPERTY_class = 45;
  private static final int PROPERTY_font = 46;
  private static final int PROPERTY_parent = 47;
  private static final int PROPERTY_name = 48;
  private static final int PROPERTY_enabled = 49;
  private static final int PROPERTY_valueIsAdjusting = 50;
  private static final int PROPERTY_valid = 51;
  private static final int PROPERTY_doubleBuffered = 52;
  private static final int PROPERTY_requestFocusEnabled = 53;
  private static final int PROPERTY_minimum = 54;
  private static final int PROPERTY_managingFocus = 55;
  private static final int PROPERTY_graphicsConfiguration = 56;
  private static final int PROPERTY_visibleRect = 57;
  private static final int PROPERTY_maximumSize = 58;
  private static final int PROPERTY_locationOnScreen = 59;
  private static final int PROPERTY_preferredSize = 60;
  private static final int PROPERTY_opaque = 61;
  private static final int PROPERTY_visible = 62;
  private static final int PROPERTY_layout = 63;
  private static final int PROPERTY_colorModel = 64;
  private static final int PROPERTY_debugGraphicsOptions = 65;
  private static final int PROPERTY_focusTraversable = 66;
  private static final int PROPERTY_cursor = 67;
  private static final int PROPERTY_component = 68;

  // Property array 
  private static PropertyDescriptor[] properties = new PropertyDescriptor[69];

  static {
    try {
      properties[PROPERTY_components] = new PropertyDescriptor ( "components", IntValueEditor.class, "getComponents", null );
      properties[PROPERTY_focusCycleRoot] = new PropertyDescriptor ( "focusCycleRoot", IntValueEditor.class, "isFocusCycleRoot", null );
      properties[PROPERTY_locale] = new PropertyDescriptor ( "locale", IntValueEditor.class, "getLocale", "setLocale" );
      properties[PROPERTY_inputMethodRequests] = new PropertyDescriptor ( "inputMethodRequests", IntValueEditor.class, "getInputMethodRequests", null );
      properties[PROPERTY_maximum] = new PropertyDescriptor ( "maximum", IntValueEditor.class, "getMaximum", "setMaximum" );
      properties[PROPERTY_y] = new PropertyDescriptor ( "y", IntValueEditor.class, "getY", null );
      properties[PROPERTY_toolTipText] = new PropertyDescriptor ( "toolTipText", IntValueEditor.class, "getToolTipText", "setToolTipText" );
      properties[PROPERTY_x] = new PropertyDescriptor ( "x", IntValueEditor.class, "getX", null );
      properties[PROPERTY_optimizedDrawingEnabled] = new PropertyDescriptor ( "optimizedDrawingEnabled", IntValueEditor.class, "isOptimizedDrawingEnabled", null );
      properties[PROPERTY_value] = new PropertyDescriptor ( "value", IntValueEditor.class, "getValue", "setValue" );
      properties[PROPERTY_paintingTile] = new PropertyDescriptor ( "paintingTile", IntValueEditor.class, "isPaintingTile", null );
      properties[PROPERTY_lightweight] = new PropertyDescriptor ( "lightweight", IntValueEditor.class, "isLightweight", null );
      properties[PROPERTY_componentCount] = new PropertyDescriptor ( "componentCount", IntValueEditor.class, "getComponentCount", null );
      properties[PROPERTY_label] = new PropertyDescriptor ( "label", IntValueEditor.class, "getLabel", "setLabel" );
      properties[PROPERTY_extent] = new PropertyDescriptor ( "extent", IntValueEditor.class, "getExtent", "setExtent" );
      properties[PROPERTY_alignmentY] = new PropertyDescriptor ( "alignmentY", IntValueEditor.class, "getAlignmentY", "setAlignmentY" );
      properties[PROPERTY_alignmentX] = new PropertyDescriptor ( "alignmentX", IntValueEditor.class, "getAlignmentX", "setAlignmentX" );
      properties[PROPERTY_registeredKeyStrokes] = new PropertyDescriptor ( "registeredKeyStrokes", IntValueEditor.class, "getRegisteredKeyStrokes", null );
      properties[PROPERTY_minimumSize] = new PropertyDescriptor ( "minimumSize", IntValueEditor.class, "getMinimumSize", "setMinimumSize" );
      properties[PROPERTY_width] = new PropertyDescriptor ( "width", IntValueEditor.class, "getWidth", null );
      properties[PROPERTY_UIClassID] = new PropertyDescriptor ( "UIClassID", IntValueEditor.class, "getUIClassID", null );
      properties[PROPERTY_displayable] = new PropertyDescriptor ( "displayable", IntValueEditor.class, "isDisplayable", null );
      properties[PROPERTY_showing] = new PropertyDescriptor ( "showing", IntValueEditor.class, "isShowing", null );
      properties[PROPERTY_graphics] = new PropertyDescriptor ( "graphics", IntValueEditor.class, "getGraphics", null );
      properties[PROPERTY_dropTarget] = new PropertyDescriptor ( "dropTarget", IntValueEditor.class, "getDropTarget", "setDropTarget" );
      properties[PROPERTY_verifyInputWhenFocusTarget] = new PropertyDescriptor ( "verifyInputWhenFocusTarget", IntValueEditor.class, "getVerifyInputWhenFocusTarget", "setVerifyInputWhenFocusTarget" );
      properties[PROPERTY_bounds] = new PropertyDescriptor ( "bounds", IntValueEditor.class, "getBounds", "setBounds" );
      properties[PROPERTY_background] = new PropertyDescriptor ( "background", IntValueEditor.class, "getBackground", "setBackground" );
      properties[PROPERTY_rootPane] = new PropertyDescriptor ( "rootPane", IntValueEditor.class, "getRootPane", null );
      properties[PROPERTY_treeLock] = new PropertyDescriptor ( "treeLock", IntValueEditor.class, "getTreeLock", null );
      properties[PROPERTY_topLevelAncestor] = new PropertyDescriptor ( "topLevelAncestor", IntValueEditor.class, "getTopLevelAncestor", null );
      properties[PROPERTY_autoscrolls] = new PropertyDescriptor ( "autoscrolls", IntValueEditor.class, "getAutoscrolls", "setAutoscrolls" );
      properties[PROPERTY_accessibleContext] = new PropertyDescriptor ( "accessibleContext", IntValueEditor.class, "getAccessibleContext", null );
      properties[PROPERTY_actionMap] = new PropertyDescriptor ( "actionMap", IntValueEditor.class, "getActionMap", "setActionMap" );
      properties[PROPERTY_nextFocusableComponent] = new PropertyDescriptor ( "nextFocusableComponent", IntValueEditor.class, "getNextFocusableComponent", "setNextFocusableComponent" );
      properties[PROPERTY_foreground] = new PropertyDescriptor ( "foreground", IntValueEditor.class, "getForeground", "setForeground" );
      properties[PROPERTY_insets] = new PropertyDescriptor ( "insets", IntValueEditor.class, "getInsets", null );
      properties[PROPERTY_inputVerifier] = new PropertyDescriptor ( "inputVerifier", IntValueEditor.class, "getInputVerifier", "setInputVerifier" );
      properties[PROPERTY_inputContext] = new PropertyDescriptor ( "inputContext", IntValueEditor.class, "getInputContext", null );
      properties[PROPERTY_toolkit] = new PropertyDescriptor ( "toolkit", IntValueEditor.class, "getToolkit", null );
      properties[PROPERTY_componentOrientation] = new PropertyDescriptor ( "componentOrientation", IntValueEditor.class, "getComponentOrientation", "setComponentOrientation" );
      properties[PROPERTY_validateRoot] = new PropertyDescriptor ( "validateRoot", IntValueEditor.class, "isValidateRoot", null );
      properties[PROPERTY_border] = new PropertyDescriptor ( "border", IntValueEditor.class, "getBorder", "setBorder" );
      properties[PROPERTY_height] = new PropertyDescriptor ( "height", IntValueEditor.class, "getHeight", null );
      properties[PROPERTY_peer] = new PropertyDescriptor ( "peer", IntValueEditor.class, "getPeer", null );
      properties[PROPERTY_class] = new PropertyDescriptor ( "class", IntValueEditor.class, "getClass", null );
      properties[PROPERTY_font] = new PropertyDescriptor ( "font", IntValueEditor.class, "getFont", "setFont" );
      properties[PROPERTY_parent] = new PropertyDescriptor ( "parent", IntValueEditor.class, "getParent", null );
      properties[PROPERTY_name] = new PropertyDescriptor ( "name", IntValueEditor.class, "getName", "setName" );
      properties[PROPERTY_enabled] = new PropertyDescriptor ( "enabled", IntValueEditor.class, "isEnabled", "setEnabled" );
      properties[PROPERTY_valueIsAdjusting] = new PropertyDescriptor ( "valueIsAdjusting", IntValueEditor.class, "getValueIsAdjusting", "setValueIsAdjusting" );
      properties[PROPERTY_valid] = new PropertyDescriptor ( "valid", IntValueEditor.class, "isValid", null );
      properties[PROPERTY_doubleBuffered] = new PropertyDescriptor ( "doubleBuffered", IntValueEditor.class, "isDoubleBuffered", "setDoubleBuffered" );
      properties[PROPERTY_requestFocusEnabled] = new PropertyDescriptor ( "requestFocusEnabled", IntValueEditor.class, "isRequestFocusEnabled", "setRequestFocusEnabled" );
      properties[PROPERTY_minimum] = new PropertyDescriptor ( "minimum", IntValueEditor.class, "getMinimum", "setMinimum" );
      properties[PROPERTY_managingFocus] = new PropertyDescriptor ( "managingFocus", IntValueEditor.class, "isManagingFocus", null );
      properties[PROPERTY_graphicsConfiguration] = new PropertyDescriptor ( "graphicsConfiguration", IntValueEditor.class, "getGraphicsConfiguration", null );
      properties[PROPERTY_visibleRect] = new PropertyDescriptor ( "visibleRect", IntValueEditor.class, "getVisibleRect", null );
      properties[PROPERTY_maximumSize] = new PropertyDescriptor ( "maximumSize", IntValueEditor.class, "getMaximumSize", "setMaximumSize" );
      properties[PROPERTY_locationOnScreen] = new PropertyDescriptor ( "locationOnScreen", IntValueEditor.class, "getLocationOnScreen", null );
      properties[PROPERTY_preferredSize] = new PropertyDescriptor ( "preferredSize", IntValueEditor.class, "getPreferredSize", "setPreferredSize" );
      properties[PROPERTY_opaque] = new PropertyDescriptor ( "opaque", IntValueEditor.class, "isOpaque", "setOpaque" );
      properties[PROPERTY_visible] = new PropertyDescriptor ( "visible", IntValueEditor.class, "isVisible", "setVisible" );
      properties[PROPERTY_layout] = new PropertyDescriptor ( "layout", IntValueEditor.class, "getLayout", "setLayout" );
      properties[PROPERTY_colorModel] = new PropertyDescriptor ( "colorModel", IntValueEditor.class, "getColorModel", null );
      properties[PROPERTY_debugGraphicsOptions] = new PropertyDescriptor ( "debugGraphicsOptions", IntValueEditor.class, "getDebugGraphicsOptions", "setDebugGraphicsOptions" );
      properties[PROPERTY_focusTraversable] = new PropertyDescriptor ( "focusTraversable", IntValueEditor.class, "isFocusTraversable", null );
      properties[PROPERTY_cursor] = new PropertyDescriptor ( "cursor", IntValueEditor.class, "getCursor", "setCursor" );
      properties[PROPERTY_component] = new IndexedPropertyDescriptor ( "component", IntValueEditor.class, null, null, "getComponent", null );
    }
    catch( IntrospectionException e) {}//GEN-HEADEREND:Properties
  
  // Here you can add code for customizing the properties array.  

}//GEN-LAST:Properties

  // EventSet identifiers//GEN-FIRST:Events
  private static final int EVENT_mouseMotionListener = 0;
  private static final int EVENT_ancestorListener = 1;
  private static final int EVENT_inputMethodListener = 2;
  private static final int EVENT_componentListener = 3;
  private static final int EVENT_mouseListener = 4;
  private static final int EVENT_focusListener = 5;
  private static final int EVENT_changeListener = 6;
  private static final int EVENT_propertyChangeListener = 7;
  private static final int EVENT_keyListener = 8;
  private static final int EVENT_containerListener = 9;
  private static final int EVENT_vetoableChangeListener = 10;

  // EventSet array
  private static EventSetDescriptor[] eventSets = new EventSetDescriptor[11];

  static {
    try {
      eventSets[EVENT_mouseMotionListener] = new EventSetDescriptor ( IntValueEditor.class, "mouseMotionListener", java.awt.event.MouseMotionListener.class, new String[0], "addMouseMotionListener", "removeMouseMotionListener" );
      eventSets[EVENT_mouseMotionListener].setInDefaultEventSet ( false );
      eventSets[EVENT_ancestorListener] = new EventSetDescriptor ( IntValueEditor.class, "ancestorListener", javax.swing.event.AncestorListener.class, new String[0], "addAncestorListener", "removeAncestorListener" );
      eventSets[EVENT_ancestorListener].setInDefaultEventSet ( false );
      eventSets[EVENT_inputMethodListener] = new EventSetDescriptor ( IntValueEditor.class, "inputMethodListener", java.awt.event.InputMethodListener.class, new String[0], "addInputMethodListener", "removeInputMethodListener" );
      eventSets[EVENT_inputMethodListener].setInDefaultEventSet ( false );
      eventSets[EVENT_componentListener] = new EventSetDescriptor ( IntValueEditor.class, "componentListener", java.awt.event.ComponentListener.class, new String[0], "addComponentListener", "removeComponentListener" );
      eventSets[EVENT_componentListener].setInDefaultEventSet ( false );
      eventSets[EVENT_mouseListener] = new EventSetDescriptor ( IntValueEditor.class, "mouseListener", java.awt.event.MouseListener.class, new String[0], "addMouseListener", "removeMouseListener" );
      eventSets[EVENT_mouseListener].setInDefaultEventSet ( false );
      eventSets[EVENT_focusListener] = new EventSetDescriptor ( IntValueEditor.class, "focusListener", java.awt.event.FocusListener.class, new String[0], "addFocusListener", "removeFocusListener" );
      eventSets[EVENT_focusListener].setInDefaultEventSet ( false );
      eventSets[EVENT_changeListener] = new EventSetDescriptor ( IntValueEditor.class, "changeListener", ChangeListener.class, new String[0], "addChangeListener", "removeChangeListener" );
      eventSets[EVENT_propertyChangeListener] = new EventSetDescriptor ( IntValueEditor.class, "propertyChangeListener", PropertyChangeListener.class, new String[0], "addPropertyChangeListener", "removePropertyChangeListener" );
      eventSets[EVENT_keyListener] = new EventSetDescriptor ( IntValueEditor.class, "keyListener", java.awt.event.KeyListener.class, new String[0], "addKeyListener", "removeKeyListener" );
      eventSets[EVENT_keyListener].setInDefaultEventSet ( false );
      eventSets[EVENT_containerListener] = new EventSetDescriptor ( IntValueEditor.class, "containerListener", java.awt.event.ContainerListener.class, new String[0], "addContainerListener", "removeContainerListener" );
      eventSets[EVENT_containerListener].setInDefaultEventSet ( false );
      eventSets[EVENT_vetoableChangeListener] = new EventSetDescriptor ( IntValueEditor.class, "vetoableChangeListener", java.beans.VetoableChangeListener.class, new String[0], "addVetoableChangeListener", "removeVetoableChangeListener" );
    }
    catch( IntrospectionException e) {}//GEN-HEADEREND:Events

  // Here you can add code for customizing the event sets array.  

}//GEN-LAST:Events

  private static java.awt.Image iconColor16 = null; //GEN-BEGIN:IconsDef
  private static java.awt.Image iconColor32 = null;
  private static java.awt.Image iconMono16 = null;
  private static java.awt.Image iconMono32 = null; //GEN-END:IconsDef
  private static String iconNameC16 = "IntValueEditor16.gif";//GEN-BEGIN:Icons
  private static String iconNameC32 = "IntValueEditor32.gif";
  private static String iconNameM16 = null;
  private static String iconNameM32 = null;//GEN-END:Icons
                                                 
  private static int defaultPropertyIndex = -1;//GEN-BEGIN:Idx
  private static int defaultEventIndex = -1;//GEN-END:Idx


  /**
   * Gets the beans <code>PropertyDescriptor</code>s.
   * 
   * @return An array of PropertyDescriptors describing the editable
   * properties supported by this bean.  May return null if the
   * information should be obtained by automatic analysis.
   * <p>
   * If a property is indexed, then its entry in the result array will
   * belong to the IndexedPropertyDescriptor subclass of PropertyDescriptor.
   * A client of getPropertyDescriptors can use "instanceof" to check
   * if a given PropertyDescriptor is an IndexedPropertyDescriptor.
   */
  public PropertyDescriptor[] getPropertyDescriptors() {
    return properties;
  }

  /**
   * Gets the beans <code>EventSetDescriptor</code>s.
   * 
   * @return  An array of EventSetDescriptors describing the kinds of 
   * events fired by this bean.  May return null if the information
   * should be obtained by automatic analysis.
   */
  public EventSetDescriptor[] getEventSetDescriptors() {
    return eventSets;
  }

  /**
   * A bean may have a "default" property that is the property that will
   * mostly commonly be initially chosen for update by human's who are 
   * customizing the bean.
   * @return  Index of default property in the PropertyDescriptor array
   * 		returned by getPropertyDescriptors.
   * <P>	Returns -1 if there is no default property.
   */
  public int getDefaultPropertyIndex() {
    return defaultPropertyIndex;
  }

  /**
   * A bean may have a "default" event that is the event that will
   * mostly commonly be used by human's when using the bean. 
   * @return Index of default event in the EventSetDescriptor array
   *		returned by getEventSetDescriptors.
   * <P>	Returns -1 if there is no default event.
   */
  public int getDefaultEventIndex() {
    return defaultPropertyIndex;
  }

  /**
   * This method returns an image object that can be used to
   * represent the bean in toolboxes, toolbars, etc.   Icon images
   * will typically be GIFs, but may in future include other formats.
   * <p>
   * Beans aren't required to provide icons and may return null from
   * this method.
   * <p>
   * There are four possible flavors of icons (16x16 color,
   * 32x32 color, 16x16 mono, 32x32 mono).  If a bean choses to only
   * support a single icon we recommend supporting 16x16 color.
   * <p>
   * We recommend that icons have a "transparent" background
   * so they can be rendered onto an existing background.
   *
   * @param  iconKind  The kind of icon requested.  This should be
   *    one of the constant values ICON_COLOR_16x16, ICON_COLOR_32x32, 
   *    ICON_MONO_16x16, or ICON_MONO_32x32.
   * @return  An image object representing the requested icon.  May
   *    return null if no suitable icon is available.
   */
  public java.awt.Image getIcon(int iconKind) {
    switch ( iconKind ) {
      case ICON_COLOR_16x16:
        if ( iconNameC16 == null )
          return null;
        else {
          if( iconColor16 == null )
            iconColor16 = loadImage( iconNameC16 );
          return iconColor16;
          }
      case ICON_COLOR_32x32:
        if ( iconNameC32 == null )
          return null;
        else {
          if( iconColor32 == null )
            iconColor32 = loadImage( iconNameC32 );
          return iconColor32;
          }
      case ICON_MONO_16x16:
        if ( iconNameM16 == null )
          return null;
        else {
          if( iconMono16 == null )
            iconMono16 = loadImage( iconNameM16 );
          return iconMono16;
          }
      case ICON_MONO_32x32:
        if ( iconNameM32 == null )
          return null;
        else {
          if( iconNameM32 == null )
            iconMono32 = loadImage( iconNameM32 );
          return iconMono32;
          }
    }
    return null;
  }

}

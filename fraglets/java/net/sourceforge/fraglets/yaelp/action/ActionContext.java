/*
 * ActionContext.java
 * Copyright (C) 2001, 2002 Klaus Rennecke.
 * Created on November 6, 2002, 10:21 AM
 */

package net.sourceforge.fraglets.yaelp.action;

import java.net.URL;
import net.sourceforge.fraglets.yaelp.model.AvatarFilter;

/**
 * Action context -- the interface of the application to the actions.
 *
 * <p>This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * <p>This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 * @author  marion@users.sourceforge.net
 */
public interface ActionContext {
    public URL getResource(String localName);
    public String getResourceString(String key);
    public String getApplicationName();
    public String getVersion();
    public Object getCurrentSelection();
    public void appendFilter(String name, AvatarFilter filter);
}

/*
 * Zed Attack Proxy (ZAP) and its related class files.
 * 
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 * 
 * Copyright 2013 The ZAP Development Team
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.zaproxy.zap.extension.zest.menu;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.swing.text.JTextComponent;

import org.parosproxy.paros.Constant;
import org.parosproxy.paros.extension.ExtensionPopupMenuItem;
import org.parosproxy.paros.view.View;
import org.zaproxy.zap.extension.zest.ExtensionZest;
import org.zaproxy.zap.extension.zest.ZestScriptWrapper;
import org.zaproxy.zap.extension.zest.dialogs.ZestDialog;

public class ZestPasteVariablePopupMenu extends ExtensionPopupMenuItem {

	private static final long serialVersionUID = 2282358266003940700L;

	private ExtensionZest extension;
	private JTextComponent lastInvoker = null;
    //private JFrame parentFrame = null;
    private ZestScriptWrapper script = null;
    private List<ExtensionPopupMenuItem> subMenus = new ArrayList<>();

	/**
	 * This method initializes 
	 * 
	 */
	public ZestPasteVariablePopupMenu(ExtensionZest extension) {
		super();
		this.extension = extension;
	}
	
	/**/
    @Override
    public String getParentMenuName() {
    	return Constant.messages.getString("zest.pastevar.popup", true);
    }
    
    @Override
    public boolean isSubMenu() {
    	return true;
    }
    @Override
    public boolean isDummyItem () {
    	return true;
    }

    @Override
    public boolean isEnableForComponent(Component invoker) {
        if (invoker instanceof JTextComponent) {
        	// Need to be optimistic so invoker is initialised
        	setLastInvoker((JTextComponent) invoker);
        } else {
            setLastInvoker(null);
            return false;
        }
    	if (!this.isChildOfZestDialog(invoker)) {
            setLastInvoker(null);
    		return false;
    	}
    	return true;
    }

    private boolean isChildOfZestDialog(Component invoker) {
    	if (invoker instanceof ZestDialog) {
    		script = ((ZestDialog) invoker).getScript();
    		reCreateSubMenu();
    		return true;
    	} else if (invoker.getParent() == null) {
    		return false;
    	} else {
    		return this.isChildOfZestDialog(invoker.getParent());
    	}
	}

    private void reCreateSubMenu() {
    	for (ExtensionPopupMenuItem menu : subMenus) {
			View.getSingleton().getPopupMenu().removeMenu(menu);
			
		}
		subMenus.clear();
		
		if (script != null) {
			TreeSet<String> sortedSet = new TreeSet<String>(script.getZestScript().getVariableNames());
			for (String var : sortedSet) {
	        	ExtensionPopupMenuItem piicm = new ZestPasteVariableMenu(extension, lastInvoker, var);
	        	piicm.setMenuIndex(this.getMenuIndex());
				View.getSingleton().getPopupMenu().addMenu(piicm);
				this.subMenus.add(piicm);
			}
		}
	}

	@Override
    public boolean isSafe() {
    	return true;
    }

    /**
     * @param lastInvoker The lastInvoker to set.
     */
    public void setLastInvoker(JTextComponent lastInvoker) {
System.out.println("PVPM lastInvoker=" + lastInvoker);
        this.lastInvoker = lastInvoker;
    }
}
/*
 * Zed Attack Proxy (ZAP) and its related class files.
 * 
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 * 
 * Copyright 2013 ZAP development team
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
package org.zaproxy.zap.extension.zest.dialogs;

import java.awt.Dimension;
import java.awt.Frame;
import java.net.MalformedURLException;
import java.net.URL;

import org.mozilla.zest.core.v1.ZestRequest;
import org.mozilla.zest.core.v1.ZestScript;
import org.parosproxy.paros.Constant;
import org.zaproxy.zap.extension.zest.ExtensionZest;
import org.zaproxy.zap.view.StandardFieldsDialog;

public class ZestRequestDialog extends StandardFieldsDialog {

	private static final String FIELD_URL = "zest.dialog.request.label.url"; 
	private static final String FIELD_METHOD = "zest.dialog.request.label.method"; 
	private static final String FIELD_HEADERS = "zest.dialog.request.label.headers"; 
	private static final String FIELD_BODY = "zest.dialog.request.label.body"; 

	private static final long serialVersionUID = 1L;

	private ExtensionZest extension = null;
	private ZestScript script =  null;
	private ZestRequest request = null;

	public ZestRequestDialog(ExtensionZest ext, Frame owner, Dimension dim) {
		super(owner, "zest.dialog.request.title", dim);
		this.extension = ext;
	}

	public void init (ZestScript script, ZestRequest req) {
		this.script = script;
		this.request = req;

		this.removeAllFields();
		this.addTextField(FIELD_URL, req.getUrl().toString());
		this.addComboField(FIELD_METHOD, new String[] {"GET", "POST"}, req.getMethod());
		this.addMultilineField(FIELD_HEADERS, req.getHeaders());
		this.addMultilineField(FIELD_BODY, req.getData());
	}

	public void save() {
		try {
			this.request.setUrl(new URL(this.getStringValue(FIELD_URL)));
		} catch (MalformedURLException e) {
			// Should have already been validated
		}
		this.request.setMethod(this.getStringValue(FIELD_METHOD));
		this.request.setHeaders(this.getStringValue(FIELD_HEADERS));
		this.request.setData(this.getStringValue(FIELD_BODY));
		
		this.extension.update(this.script,  this.request);

	}

	@Override
	public String validateFields() {
		try {
			new URL(this.getStringValue(FIELD_URL));
		} catch (MalformedURLException e) {
			return Constant.messages.getString("zest.dialog.request.error.url");
		}
		return null;
	}
	
}
package org.wicketstuff.rest;

import org.apache.wicket.Application;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.resource.ResourceReference;

public class Index extends WebPage {
	
	@Override
	public void renderHead(IHeaderResponse response) {
		ResourceReference jQueryReference = Application.get().getJavaScriptLibrarySettings().getJQueryReference();
		response.render(JavaScriptHeaderItem.forReference(jQueryReference));
	}
}

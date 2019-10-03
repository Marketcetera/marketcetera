package org.marketcetera.webui.ui.views.errors;

import javax.servlet.http.HttpServletResponse;

import org.marketcetera.webui.app.MainView;
import org.marketcetera.webui.ui.exceptions.AccessDeniedException;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.templatemodel.TemplateModel;

@Tag("access-denied-view")
@JsModule("./src/views/errors/access-denied-view.js")
@ParentLayout(MainView.class)
@PageTitle("Access Denied")
@Route
public class AccessDeniedView extends PolymerTemplate<TemplateModel>
        implements HasErrorParameter<AccessDeniedException>
{
    @Override
	public int setErrorParameter(BeforeEnterEvent beforeEnterEvent, ErrorParameter<AccessDeniedException> errorParameter) {
		return HttpServletResponse.SC_FORBIDDEN;
	}
    private static final long serialVersionUID = -4133351246484662155L;
}

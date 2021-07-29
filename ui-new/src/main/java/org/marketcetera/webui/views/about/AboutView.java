package org.marketcetera.webui.views.about;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import org.marketcetera.webui.views.MainLayout;
import javax.annotation.security.PermitAll;

@PageTitle("About")
@Route(value = "about", layout = MainLayout.class)
@PermitAll
public class AboutView extends Div {

    public AboutView() {
        addClassName("about-view");
        add(new Text("Content placeholder"));
    }

}

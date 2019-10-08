package com.marketcetera.colin.testbench.elements.ui;

import com.vaadin.flow.component.board.testbench.BoardElement;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elementsbase.Element;

@Element("dashboard-view")
public class DashboardViewElement extends TestBenchElement implements HasApp {

	public BoardElement getBoard() {
		return $(BoardElement.class).first();
	}

}

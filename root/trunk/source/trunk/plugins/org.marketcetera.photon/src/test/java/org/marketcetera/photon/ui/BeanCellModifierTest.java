package org.marketcetera.photon.ui;

import java.math.BigDecimal;

import junit.framework.TestCase;

public class BeanCellModifierTest extends TestCase {

	class TestBean {
		String prop1;
		int prop2;
		BigDecimal readOnlyProp;
		public TestBean(String prop1, int prop2, BigDecimal readOnlyProp) {
			super();
			this.prop1 = prop1;
			this.prop2 = prop2;
			this.readOnlyProp = readOnlyProp;
		}
		public String getProp1() {
			return prop1;
		}
		public void setProp1(String prop1) {
			this.prop1 = prop1;
		}
		public int getProp2() {
			return prop2;
		}
		public void setProp2(int prop2) {
			this.prop2 = prop2;
		}
		public BigDecimal getReadOnlyProp() {
			return readOnlyProp;
		}
		
	}
	
	public void testCanModify(){
		BeanCellModifier<TestBean> modifier = new BeanCellModifier<TestBean>(
				new IElementChangeListener<TestBean>()
				{
					public void elementChanged(TestBean element) {
					}
				});
		TestBean bean = new TestBean("A", 4, BigDecimal.ONE);
		assertTrue(modifier.canModify(bean, "prop1"));
		assertTrue(modifier.canModify(bean, "prop2"));
		assertTrue(!modifier.canModify(bean, "readOnlyProp"));

	}
}

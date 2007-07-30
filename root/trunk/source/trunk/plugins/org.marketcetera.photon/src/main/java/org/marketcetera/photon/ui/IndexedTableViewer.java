package org.marketcetera.photon.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class IndexedTableViewer extends TableViewer {

	private Table table;
//	private static final String ODD_COLOR_KEY = "ODD_COLOR";
//	private static final String EVEN_COLOR_KEY = "EVEN_COLOR";
//	private Map<Color, Color> colorConversions = new HashMap<Color, Color>();
	
	/**
	 * @param table
	 */
	public IndexedTableViewer(Table table) {
		super(table);
		this.table = table;
	}
	
	public void remove(final int index){
		preservingSelection(new Runnable() {
			public void run() {
				indexedRemove(index);
			}
		});

	}

	protected void indexedRemove(int index) {
		table.remove(index);

		// Workaround for 1GDGN4Q: ITPUI:WIN2000 - TableViewer icons get
		// scrunched
		if (table.getItemCount() == 0) {
			table.removeAll();
		}
	}

//	@Override
//	public void refresh() {
//		super.refresh();
//		updateColors();
//	}
//
//	private void updateColors() {
//		for (int i=0; i < table.getItemCount(); i++){
//			TableItem item = table.getItem(i);
//			Color newBackground;
//			boolean isEven = (i%2 == 0);
//			if (isEven){
//				newBackground = (Color) item.getData(EVEN_COLOR_KEY);
//			} else {
//				newBackground = (Color) item.getData(ODD_COLOR_KEY);
//			}
//			if (newBackground == null){
//				Color oldBackground = item.getBackground();
//				if (colorConversions.containsKey(oldBackground)){
//					newBackground = colorConversions.get(oldBackground);
//				} else {
//					if (isEven){
//						newBackground = colorWithValueFactor(oldBackground, .9f);
//					} else {
//						newBackground = oldBackground;
//					}
//				}
//				if (isEven){
//					item.setData(EVEN_COLOR_KEY, newBackground);
//				} else {
//					item.setData(ODD_COLOR_KEY, newBackground);
//				}
//			}
//			item.setBackground(newBackground);
//		}
//	}
//
//	@Override
//	public void refresh(boolean updateLabels) {
//		super.refresh(updateLabels);
//
//		updateColors();
//	}
//
//	private Color colorWithValueFactor(Color c, float value)
//	{
//	    RGB rgb = c.getRGB();
//	    float[] fs = 
//	      java.awt.Color.RGBtoHSB(rgb.red, rgb.green, rgb.blue, null);
//	    java.awt.Color cc = 
//	      new java.awt.Color(java.awt.Color.HSBtoRGB(fs[0], fs[1], fs[2]*value));
//	    return new Color(table.getDisplay(), cc.getRed(), cc.getGreen(), cc.getBlue());		
//	}
	
	


}

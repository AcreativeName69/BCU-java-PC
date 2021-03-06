package page.info.edit;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.EventObject;

import javax.swing.text.JTextComponent;

import common.CommonStatic;
import common.util.Data;
import common.util.stage.SCDef;
import common.util.stage.SCGroup;
import common.util.unit.AbEnemy;
import common.util.unit.EnemyStore;
import page.MainLocale;
import page.support.AbJTable;
import page.support.EnemyTCR;

class SCGroupEditTable extends AbJTable {

	private static final long serialVersionUID = 1L;

	private static String[] title;

	static {
		redefine();
	}

	protected static void redefine() {
		title = MainLocale.getLoc(1, "t1", "t8");
	}

	protected final SCDef scd;

	protected SCGroupEditTable(SCDef sc) {
		scd = sc;
		setDefaultRenderer(Integer.class, new EnemyTCR());
	}

	@Override
	public boolean editCellAt(int r, int c, EventObject e) {
		boolean result = super.editCellAt(r, c, e);
		Component editor = getEditorComponent();
		if (editor == null || !(editor instanceof JTextComponent))
			return result;
		JTextComponent jtf = ((JTextComponent) editor);
		if (e instanceof KeyEvent)
			jtf.selectAll();
		return result;
	}

	@Override
	public Class<?> getColumnClass(int c) {
		return lnk[c] == 0 ? Integer.class : String.class;
	}

	@Override
	public int getColumnCount() {
		return title.length;
	}

	@Override
	public String getColumnName(int c) {
		return title[lnk[c]];
	}

	@Override
	public synchronized int getRowCount() {
		if (scd == null)
			return 0;
		return scd.smap.size();
	}

	@Override
	public synchronized Object getValueAt(int r, int c) {
		if (scd == null || r < 0 || c < 0 || r >= scd.smap.size() || c > lnk.length)
			return null;
		return get(r, lnk[c]);
	}

	@Override
	public boolean isCellEditable(int r, int c) {
		return lnk[c] != 0;
	}

	@Override
	public synchronized void setValueAt(Object arg0, int r, int c) {
		if (scd == null)
			return;
		c = lnk[c];
		if (c > 0) {
			int[] is = CommonStatic.parseIntsN((String) arg0);
			if (is.length == 0)
				return;
			if (is.length == 1)
				set(r, c, is[0], -1);
			else
				set(r, c, is[0], is[1]);
		}
	}

	protected synchronized void addLine(AbEnemy enemy) {
		if (scd == null)
			return;
		int ind = getSelectedRow();
		int eid = 0;
		if (enemy == null)
			while (scd.smap.containsKey(eid++))
				;
		else
			eid = enemy.getID();
		if (scd.smap.containsKey(eid))
			return;
		scd.smap.put(eid, 0);
		ind++;
		if (ind < 0)
			clearSelection();
		else
			setRowSelectionInterval(ind, ind);
	}

	protected synchronized void remLine() {
		if (scd == null)
			return;
		int ind = getSelectedRow();
		if (ind == -1)
			return;
		scd.smap.remove(scd.getSMap()[ind][0]);
		if (ind >= scd.smap.size())
			ind--;
		if (ind < 0)
			clearSelection();
		else
			setRowSelectionInterval(ind, ind);
	}

	private Object get(int r, int c) {
		int[][] info = scd.getSMap();
		if (r >= info.length)
			return null;
		int[] data = info[r];
		if (c == 0)
			return EnemyStore.getAbEnemy(data[0], true);
		else if (c == 1) {
			int g = data[1];
			SCGroup scg = scd.sub.get(g);
			return scg == null ? g != 0 ? Data.trio(g) + " - invalid" : "" : scg.toString();
		}
		return null;
	}

	private void set(int r, int c, int v, int para) {
		int[][] info = scd.getSMap();
		if (r >= info.length)
			return;
		int[] data = info[r];
		if (v < 0)
			v = 0;
		if (c == 0)
			scd.smap.put(v, scd.smap.remove(data[0]));
		else if (c == 1)
			scd.smap.put(data[0], v);
	}

}

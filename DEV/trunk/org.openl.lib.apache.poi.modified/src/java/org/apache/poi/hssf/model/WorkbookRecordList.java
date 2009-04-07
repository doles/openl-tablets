/* ====================================================================
   Copyright 2002-2004   Apache Software Foundation

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package org.apache.poi.hssf.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.record.Record;

public class WorkbookRecordList {
    private List records = new ArrayList();

    private int protpos = 0; // holds the position of the protect record.
    private int bspos = 0; // holds the position of the last bound sheet.
    private int tabpos = 0; // holds the position of the tabid record
    private int fontpos = 0; // hold the position of the last font record
    private int xfpos = 0; // hold the position of the last extended font
                            // record
    private int backuppos = 0; // holds the position of the backup record.
    private int namepos = 0; // holds the position of last name record
    private int supbookpos = 0; // holds the position of sup book
    private int externsheetPos = 0;// holds the position of the extern sheet
    private int palettepos = -1; // hold the position of the palette, if
                                    // applicable

    public void add(int pos, Record r) {
        records.add(pos, r);
        if (getProtpos() >= pos) {
            setProtpos(protpos + 1);
        }
        if (getBspos() >= pos) {
            setBspos(bspos + 1);
        }
        if (getTabpos() >= pos) {
            setTabpos(tabpos + 1);
        }
        if (getFontpos() >= pos) {
            setFontpos(fontpos + 1);
        }
        if (getXfpos() >= pos) {
            setXfpos(xfpos + 1);
        }
        if (getBackuppos() >= pos) {
            setBackuppos(backuppos + 1);
        }
        if (getNamepos() >= pos) {
            setNamepos(namepos + 1);
        }
        if (getSupbookpos() >= pos) {
            setSupbookpos(supbookpos + 1);
        }
        if ((getPalettepos() != -1) && (getPalettepos() >= pos)) {
            setPalettepos(palettepos + 1);
        }
        if (getExternsheetPos() >= pos) {
            setExternsheetPos(getExternsheetPos() + 1);
        }
    }

    public Record get(int i) {
        return (Record) records.get(i);
    }

    public int getBackuppos() {
        return backuppos;
    }

    public int getBspos() {
        return bspos;
    }

    /**
     * Returns the externsheetPos.
     *
     * @return int
     */
    public int getExternsheetPos() {
        return externsheetPos;
    }

    public int getFontpos() {
        return fontpos;
    }

    /**
     * Returns the namepos.
     *
     * @return int
     */
    public int getNamepos() {
        return namepos;
    }

    public int getPalettepos() {
        return palettepos;
    }

    public int getProtpos() {
        return protpos;
    }

    public List getRecords() {
        return records;
    }

    /**
     * Returns the supbookpos.
     *
     * @return int
     */
    public int getSupbookpos() {
        return supbookpos;
    }

    public int getTabpos() {
        return tabpos;
    }

    public int getXfpos() {
        return xfpos;
    }

    public Iterator iterator() {
        return records.iterator();
    }

    public void remove(int pos) {
        records.remove(pos);
        if (getProtpos() >= pos) {
            setProtpos(protpos - 1);
        }
        if (getBspos() >= pos) {
            setBspos(bspos - 1);
        }
        if (getTabpos() >= pos) {
            setTabpos(tabpos - 1);
        }
        if (getFontpos() >= pos) {
            setFontpos(fontpos - 1);
        }
        if (getXfpos() >= pos) {
            setXfpos(xfpos - 1);
        }
        if (getBackuppos() >= pos) {
            setBackuppos(backuppos - 1);
        }
        if (getNamepos() >= pos) {
            setNamepos(getNamepos() - 1);
        }
        if (getSupbookpos() >= pos) {
            setSupbookpos(getSupbookpos() - 1);
        }
        if ((getPalettepos() != -1) && (getPalettepos() >= pos)) {
            setPalettepos(palettepos - 1);
        }
        if (getExternsheetPos() >= pos) {
            setExternsheetPos(getExternsheetPos() - 1);
        }
    }

    public void setBackuppos(int backuppos) {
        this.backuppos = backuppos;
    }

    public void setBspos(int bspos) {
        this.bspos = bspos;
    }

    /**
     * Sets the externsheetPos.
     *
     * @param externsheetPos The externsheetPos to set
     */
    public void setExternsheetPos(int externsheetPos) {
        this.externsheetPos = externsheetPos;
    }

    public void setFontpos(int fontpos) {
        this.fontpos = fontpos;
    }

    /**
     * Sets the namepos.
     *
     * @param namepos The namepos to set
     */
    public void setNamepos(int namepos) {
        this.namepos = namepos;
    }

    public void setPalettepos(int palettepos) {
        this.palettepos = palettepos;
    }

    public void setProtpos(int protpos) {
        this.protpos = protpos;
    }

    public void setRecords(List records) {
        this.records = records;
    }

    /**
     * Sets the supbookpos.
     *
     * @param supbookpos The supbookpos to set
     */
    public void setSupbookpos(int supbookpos) {
        this.supbookpos = supbookpos;
    }

    public void setTabpos(int tabpos) {
        this.tabpos = tabpos;
    }

    public void setXfpos(int xfpos) {
        this.xfpos = xfpos;
    }

    public int size() {
        return records.size();
    }

}

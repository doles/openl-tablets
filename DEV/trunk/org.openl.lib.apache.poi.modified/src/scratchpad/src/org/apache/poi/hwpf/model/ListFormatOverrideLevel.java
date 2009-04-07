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

package org.apache.poi.hwpf.model;

import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.BitField;
import org.apache.poi.util.LittleEndianConsts;

import java.util.Arrays;

public class ListFormatOverrideLevel {
    private static final int BASE_SIZE = 8;

    private static BitField _ilvl = new BitField(0xf);
    private static BitField _fStartAt = new BitField(0x10);
    private static BitField _fFormatting = new BitField(0x20);
    int _iStartAt;
    byte _info;
    byte[] _reserved = new byte[3];
    ListLevel _lvl;

    public ListFormatOverrideLevel(byte[] buf, int offset) {
        _iStartAt = LittleEndian.getInt(buf, offset);
        offset += LittleEndianConsts.INT_SIZE;
        _info = buf[offset++];
        System.arraycopy(buf, offset, _reserved, 0, _reserved.length);
        offset += _reserved.length;

        if (_fFormatting.getValue(_info) > 0) {
            _lvl = new ListLevel(buf, offset);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        ListFormatOverrideLevel lfolvl = (ListFormatOverrideLevel) obj;
        boolean lvlEquality = false;
        if (_lvl != null) {
            lvlEquality = _lvl.equals(lfolvl._lvl);
        } else {
            lvlEquality = lfolvl._lvl == null;
        }

        return lvlEquality && lfolvl._iStartAt == _iStartAt && lfolvl._info == _info
                && Arrays.equals(lfolvl._reserved, _reserved);
    }

    public ListLevel getLevel() {
        return _lvl;
    }

    public int getLevelNum() {
        return _ilvl.getValue(_info);
    }

    public int getSizeInBytes() {
        return (_lvl == null ? BASE_SIZE : BASE_SIZE + _lvl.getSizeInBytes());
    }

    public boolean isFormatting() {
        return _fFormatting.getValue(_info) != 0;
    }

    public boolean isStartAt() {
        return _fStartAt.getValue(_info) != 0;
    }

    public byte[] toByteArray() {
        byte[] buf = new byte[getSizeInBytes()];

        int offset = 0;
        LittleEndian.putInt(buf, _iStartAt);
        offset += LittleEndianConsts.INT_SIZE;
        buf[offset++] = _info;
        System.arraycopy(_reserved, 0, buf, offset, 3);
        offset += 3;

        if (_lvl != null) {
            byte[] levelBuf = _lvl.toByteArray();
            System.arraycopy(levelBuf, 0, buf, offset, levelBuf.length);
        }

        return buf;
    }
}

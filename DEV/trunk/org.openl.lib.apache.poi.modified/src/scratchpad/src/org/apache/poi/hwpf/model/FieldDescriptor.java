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

import org.apache.poi.util.BitField;

public class FieldDescriptor {
    private final static BitField fZombieEmbed = new BitField(0x02);
    private final static BitField fResultDiry = new BitField(0x04);
    private final static BitField fResultEdited = new BitField(0x08);
    private final static BitField fLocked = new BitField(0x10);
    private final static BitField fPrivateResult = new BitField(0x20);
    private final static BitField fNested = new BitField(0x40);
    private final static BitField fHasSep = new BitField(0x80);
    byte _fieldBoundaryType;
    byte _info;

    public FieldDescriptor() {
    }
}

/*
 * Copyright 2010-2019 Australian Signals Directorate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package au.gov.asd.tac.constellation.graph.undo.access;

import au.gov.asd.tac.constellation.graph.undo.UndoGraphEditState;

/**
 *
 * @author sirius
 */
public class IdValueUpdater2 implements ValueUpdater32 {

    public static final IdValueUpdater2 INSTANCE = new IdValueUpdater2();

    @Override
    public int store(UndoGraphEditState state, int id) {
        if (id != state.currentId) {
            int idDelta = id - state.currentId;
            state.currentId = id;
            if (idDelta >= Byte.MIN_VALUE && idDelta <= Byte.MAX_VALUE) {
                state.addByte((byte) idDelta);
                return 1;
            } else if (idDelta >= Short.MIN_VALUE && idDelta <= Short.MAX_VALUE) {
                state.addShort((short) idDelta);
                return 2;
            } else {
                state.addInt(idDelta);
                return 3;
            }
        }
        return 0;
    }

    @Override
    public void updateExecute(UndoGraphEditState state, int parameters) {
        ID_GETTERS[parameters & 3].getExecute(state);
    }

    @Override
    public void updateUndo(UndoGraphEditState state, int parameters) {
        ID_GETTERS[parameters & 3].getUndo(state);
    }

    public static final ValueGetter[] ID_GETTERS = new ValueGetter[]{
        new ValueGetter() {
            @Override
            public void getExecute(UndoGraphEditState state) {
            }

            @Override
            public void getUndo(UndoGraphEditState state) {
            }
        },
        new ValueGetter() {
            @Override
            public void getExecute(UndoGraphEditState edit) {
                edit.currentId += edit.byteStack[edit.bytePointer++];
            }

            @Override
            public void getUndo(UndoGraphEditState edit) {
                edit.currentId -= edit.byteStack[--edit.bytePointer];
            }
        },
        new ValueGetter() {
            @Override
            public void getExecute(UndoGraphEditState edit) {
                edit.currentId += edit.shortStack[edit.shortPointer++];
            }

            @Override
            public void getUndo(UndoGraphEditState edit) {
                edit.currentId -= edit.shortStack[--edit.shortPointer];
            }
        },
        new ValueGetter() {
            @Override
            public void getExecute(UndoGraphEditState edit) {
                edit.currentId += edit.intStack[edit.intPointer++];
            }

            @Override
            public void getUndo(UndoGraphEditState edit) {
                edit.currentId -= edit.intStack[--edit.intPointer];
            }
        }
    };
}

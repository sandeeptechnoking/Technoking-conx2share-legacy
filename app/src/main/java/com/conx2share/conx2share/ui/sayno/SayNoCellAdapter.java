package com.conx2share.conx2share.ui.sayno;

import com.conx2share.conx2share.ui.sayno.cell.CellAnonymity;

import io.techery.celladapter.Cell;
import io.techery.celladapter.CellAdapter;

public class SayNoCellAdapter extends CellAdapter {
    private boolean isAnonymous;

    @Override
    public void onBindViewHolder(Cell cell, int position) {
        if (cell instanceof CellAnonymity) {
            ((CellAnonymity) cell).changeAnonymity(isAnonymous);
        }

        super.onBindViewHolder(cell, position);
    }

    void setAnonymousMode(boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
        notifyDataSetChanged();
    }
}
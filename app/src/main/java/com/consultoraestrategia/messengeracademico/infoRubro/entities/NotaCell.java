package com.consultoraestrategia.messengeracademico.infoRubro.entities;

/**
 * Created by Jse on 16/09/2018.
 */

public class NotaCell extends Cell
{
    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    private ColorNota color = ColorNota.BLANCO;

    public ColorNota getColor() {
        return color;
    }

    public void setColor(ColorNota color) {
        this.color = color;
    }
}

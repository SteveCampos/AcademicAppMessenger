package com.consultoraestrategia.messengeracademico.infoRubro.ui;

import com.consultoraestrategia.messengeracademico.infoRubro.InfoRubroPresenter;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.Alumno;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.Cell;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.Column;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.Row;

import java.util.List;

/**
 * Created by Jse on 15/09/2018.
 */

public interface InfoRubroView {
    void setPresenter(InfoRubroPresenter presenter);

    void showTableView(List<List<Cell>> cellListList, List<Column> columnList, List<Row> rowList);

    void setPuntos(String puntos);

    void setAlumno(Alumno alumno);

    void setDesempenio(String desempenio);

    void setNota(String nota);

    void setLogro(String logro);

    void setNombreRubrica(String nombreRubrica);

    void setNombreCurso(String nombreCurso);
}

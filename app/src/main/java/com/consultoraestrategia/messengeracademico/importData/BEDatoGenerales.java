package com.consultoraestrategia.messengeracademico.importData;

import com.consultoraestrategia.messengeracademico.entities.Alumno;
import com.consultoraestrategia.messengeracademico.entities.AnioAcademico;
import com.consultoraestrategia.messengeracademico.entities.CargaCurso;
import com.consultoraestrategia.messengeracademico.entities.Curso;
import com.consultoraestrategia.messengeracademico.entities.Docente;
import com.consultoraestrategia.messengeracademico.entities.Entidad;
import com.consultoraestrategia.messengeracademico.entities.GeorefRolPersona;
import com.consultoraestrategia.messengeracademico.entities.Georeferencia;
import com.consultoraestrategia.messengeracademico.entities.Grado;
import com.consultoraestrategia.messengeracademico.entities.Matricula;
import com.consultoraestrategia.messengeracademico.entities.MatriculaDetalle;
import com.consultoraestrategia.messengeracademico.entities.NivelesAcademicos;
import com.consultoraestrategia.messengeracademico.entities.Organigramas;
import com.consultoraestrategia.messengeracademico.entities.Persona;
import com.consultoraestrategia.messengeracademico.entities.PersonaGeoref;
import com.consultoraestrategia.messengeracademico.entities.Relacion;
import com.consultoraestrategia.messengeracademico.entities.Rol;
import com.consultoraestrategia.messengeracademico.entities.Seccion;
import com.consultoraestrategia.messengeracademico.entities.Tipo;

import java.util.List;

/**
 * Created by kike on 15/06/2017.
 */

public class BEDatoGenerales {
    public List<Alumno> alumno;
    public List<AnioAcademico> anioAcademico;
    public List<CargaCurso> cargacurso;
    public List<Curso> curso;
    public List<Docente> docente;
    public List<Entidad> entidad;
    public List<Grado> grado;
    public List<NivelesAcademicos> nivelesacademicos;
    public List<Matricula> matricula;
    public List<MatriculaDetalle> matriculaDetalle;
    public List<Persona> persona;
    public List<Rol> rol;
    public List<Seccion> seccion;
    public List<Tipo> tipo;
    public List<Georeferencia> georeferencia;
    public List<PersonaGeoref> personaGeoref;
    public List<GeorefRolPersona> georefRolPersona;
    public List<Relacion> relacion;
    public List<Organigramas> organigramas;
    /*  17*/

    public List<Alumno> getAlumno() {
        return alumno;
    }

    public void setAlumno(List<Alumno> alumno) {
        this.alumno = alumno;
    }

    public List<AnioAcademico> getAnioAcademico() {
        return anioAcademico;
    }

    public void setAnioAcademico(List<AnioAcademico> anioAcademico) {
        this.anioAcademico = anioAcademico;
    }

    public List<CargaCurso> getCargacurso() {
        return cargacurso;
    }

    public void setCargacurso(List<CargaCurso> cargacurso) {
        this.cargacurso = cargacurso;
    }

    public List<Curso> getCurso() {
        return curso;
    }

    public void setCurso(List<Curso> curso) {
        this.curso = curso;
    }

    public List<Docente> getDocente() {
        return docente;
    }

    public void setDocente(List<Docente> docente) {
        this.docente = docente;
    }

    public List<Entidad> getEntidad() {
        return entidad;
    }

    public void setEntidad(List<Entidad> entidad) {
        this.entidad = entidad;
    }

    public List<Grado> getGrado() {
        return grado;
    }

    public void setGrado(List<Grado> grado) {
        this.grado = grado;
    }

    public List<NivelesAcademicos> getNivelesacademicos() {
        return nivelesacademicos;
    }

    public void setNivelesacademicos(List<NivelesAcademicos> nivelesacademicos) {
        this.nivelesacademicos = nivelesacademicos;
    }

    public List<Matricula> getMatricula() {
        return matricula;
    }

    public void setMatricula(List<Matricula> matricula) {
        this.matricula = matricula;
    }

    public List<MatriculaDetalle> getMatriculaDetalle() {
        return matriculaDetalle;
    }

    public void setMatriculaDetalle(List<MatriculaDetalle> matriculaDetalle) {
        this.matriculaDetalle = matriculaDetalle;
    }

    public List<Persona> getPersona() {
        return persona;
    }

    public void setPersona(List<Persona> persona) {
        this.persona = persona;
    }

    public List<Rol> getRol() {
        return rol;
    }

    public void setRol(List<Rol> rol) {
        this.rol = rol;
    }

    public List<Seccion> getSeccion() {
        return seccion;
    }

    public void setSeccion(List<Seccion> seccion) {
        this.seccion = seccion;
    }

    public List<Tipo> getTipo() {
        return tipo;
    }

    public void setTipo(List<Tipo> tipo) {
        this.tipo = tipo;
    }

    public List<Georeferencia> getGeoreferencia() {
        return georeferencia;
    }

    public void setGeoreferencia(List<Georeferencia> georeferencia) {
        this.georeferencia = georeferencia;
    }

    public List<PersonaGeoref> getPersonaGeoref() {
        return personaGeoref;
    }

    public void setPersonaGeoref(List<PersonaGeoref> personaGeoref) {
        this.personaGeoref = personaGeoref;
    }

    public List<GeorefRolPersona> getGeorefRolPersona() {
        return georefRolPersona;
    }

    public void setGeorefRolPersona(List<GeorefRolPersona> georefRolPersona) {
        this.georefRolPersona = georefRolPersona;
    }

    public List<Relacion> getRelacion() {
        return relacion;
    }

    public void setRelacion(List<Relacion> relacion) {
        this.relacion = relacion;
    }

    public List<Organigramas> getOrganigramas() {
        return organigramas;
    }

    public void setOrganigramas(List<Organigramas> organigramas) {
        this.organigramas = organigramas;
    }
}

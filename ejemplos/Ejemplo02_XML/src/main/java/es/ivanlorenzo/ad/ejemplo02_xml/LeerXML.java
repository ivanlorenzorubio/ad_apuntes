package es.ivanlorenzo.ad.ejemplo02_xml;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author ivan
 */
public class LeerXML
{
    public static void main(String[] args)
    {
        GestorXML gestor = new GestorXML();
        Path fichero = Paths.get("alumnos.xml");
        if (gestor.abrirXML(fichero))
        {
            System.out.println(gestor.mostrarDOM());
            gestor.insertarAlumno("Dario Diaz", "18");
            System.out.println(gestor.mostrarDOM());
            gestor.guardarDOMcomoFILE("nuevoAlumnos.xml");
        }
    }

}

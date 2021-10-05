package es.ivanlorenzo.ad.ejemplo02_xml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 *
 * @author ivan
 */
public class GestorXML
{
    /**
     * Objeto Document que almacena el DOM del XML seleccionado
     */
    private Document documento;

    public boolean abrirXML(Path fichero)
    {
        documento = null;
        try
        {
            //Se crea un objeto DocumentBuiderFactory.
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            /*Indica que el modelo DOM no debe contemplar los comentarios que tenga el XML. 
            Es decir, cuando se convierte el XML al modelo DOM los comentarios serán ignorados.*/
            factory.setIgnoringComments(true);
            /*Ignora los espacios en blanco. Si no se hace esto entonces los espacios en blanco 
            aparecen como nodos.*/
            factory.setIgnoringElementContentWhitespace(true);

            /*Se crea un objeto DocumentBuilder para cargar en él la estructura de árbol DOM 
            a partir del XML seleccionado.*/
            DocumentBuilder builder = factory.newDocumentBuilder();

            //Interpreta (parsea) el documento XML (file) y genera el DOM equivalente.
            documento = builder.parse(fichero.toFile());
            //Ahora documento apunta al árbol DOM listo para ser recorrido.
            return true;

        }
        catch (IOException | ParserConfigurationException | SAXException e)
        {
            System.err.println("Error al abrir el XML");
            return false;
        }
    }

    public String mostrarDOM()
    {
        //Obtiene el primero nodo del DOM
        Node raiz = documento.getFirstChild();

        //Muestra los atributos de raiz
        List<String> atributos = procesarAtributos(raiz);

        String cadena = "Atributos de alumnos:\n";

        for (int i = 0; i < atributos.size(); i++)
        {
            //si es posición par, es el nombre del atributo. Impar el valor del atributo
            cadena += "\t" + atributos.get(i);
            cadena += (i % 2 == 0) ? "=" : "\n";
        }

        cadena += "\n======= LISTADO DE ALUMNOS =======\n";

        //Obtiene una lista de nodos con todos los nodos hijo del raiz (de alumnos)
        NodeList nodosAlumno = raiz.getChildNodes();
        for (int i = 0; i < nodosAlumno.getLength(); i++) //Proceso los nodos hijo (nodos alumno)
        {
            Node nodoAlumno = nodosAlumno.item(i);
            //Al ejecutar paso a paso este código, se observa como los nodos que encuentra son
            //de tipo 1 (ELEMENT_NODE) y tipo 3 (TEXT_NODE). 
            if (nodoAlumno.getNodeType() == Node.ELEMENT_NODE)
            { //Es un nodo Alumno que hay que procesar si es de tipo Elemento
                String[] datos_nodo = procesarAlumno(nodoAlumno);
                cadena += String.format("| %-20s", datos_nodo[0]);
                cadena += String.format("| Edad: %3s |\n", datos_nodo[1]);
            }
        }
        return cadena;

    }

    private List<String> procesarAtributos(Node nodo)
    {
        List<String> atributos = new ArrayList();

        //Para saber cuantos atributos tiene el nodo
        int num = nodo.getAttributes().getLength();
        for (int i = 0; i < num; i++)
        {
            //De la lista de atributos añade al ArrayList el nombre de cada atributo y su valor
            //Cada vez se añaden dos valores al ArrayList
            // en las posiciones pares habrá nombres de atributos y en las impares valores de atributos
            atributos.add(nodo.getAttributes().item(i).getNodeName());
            atributos.add(nodo.getAttributes().item(i).getNodeValue());
        }
        return atributos;
    }

    private String[] procesarAlumno(Node nodo)
    {
        String[] datos = new String[2];
        int contador = 0;

        //Obtiene los hijos del Alumno. Entre ellos están lo Elements nombre y edad)
        NodeList nodos = nodo.getChildNodes();
        for (int i = 0; i < nodos.getLength(); i++)
        {
            Node nodoHijo = nodos.item(i);
            /*Se debe comprobar el tipo de nodo que se quiere tratar por que es posible que haya
            nodos tipo TEXT que contengan retornos de carro al cambiar de línea en el XML.
            En este ejemplo, cuando detecta un nodo que no es tipo ELEMENT_NODE es porque es tipo 
            TEXT y contiene los saltos de línea o espacios en blanco que se incluyen en el 
            fichero de texto al crear el XML.*/
            if (nodoHijo.getNodeType() == Node.ELEMENT_NODE)
            {
                //IMPORTANTE: para obtener el texto con el título accede al nodo TEXT 
                //hijo de nodoHijo y saca su valor.
                datos[contador] = nodoHijo.getChildNodes().item(0).getNodeValue();
                contador++;
            }
        }
        return datos;
    }

    public boolean insertarAlumno(String nombre, String edad)
    {
        try
        {
            //Se crean los nodos en la estructura DOM apuntada por la variable doc.
            //Se crea un nodo tipo Element con nombre (<nombre>)
            Element nNombre = documento.createElement("nombre");
            //Se crea un nodo tipo texto con el valor del nombre del alumno 
            Text nNombreText = documento.createTextNode(nombre);
            //Se añade el nodo de texto con el nombre como hijo del nodo elemento nombre 
            nNombre.appendChild(nNombreText);

            //Se hace lo mismo con la edad del alumno
            Element nEdad = documento.createElement("edad");
            Text nEdadText = documento.createTextNode(edad);
            nEdad.appendChild(nEdadText);

            //Se crea un nodo de tipo elemento (<alumno>) 
            Element nAlumno = documento.createElement("alumno");

            //Se añade a Alumno el elemento nombre
            nAlumno.appendChild(nNombre);
            //Se añade a Alumno el elemento edad
            nAlumno.appendChild(nEdad);

            //Finalmente, se obtiene el primer nodo del documento y a él se le añade como hijo el 
            //nodo alumno que ya tiene colgando todos sus hijos y atributos creados antes.
            Node raiz = documento.getChildNodes().item(0);
            raiz.appendChild(nAlumno);

            return true;
        }
        catch (DOMException e)
        {
            System.err.println("Error al añadir al DOM");
            return false;
        }
    }

    public boolean guardarDOMcomoFILE(String fichero)
    {
        try
        {
            borrarEspaciosBlanco(documento);
            Source source = new DOMSource(documento);
            // A partir del fuente se crea el resultado en el archivo
            Result result = new StreamResult(Files.newBufferedWriter(Paths.get(fichero)));

            // Ahora hay que crear un objeto TransformerFactory para hacer la transformación
            // y convertir el documento en fichero
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
            return true;

        }
        catch (IOException | TransformerException e)
        {
            System.err.println("Error al guardar el fichero");
            return false;
        }
    }

    private void borrarEspaciosBlanco(Node node)
    {
        NodeList hijos = node.getChildNodes();
        for (int i = 0; i < hijos.getLength(); ++i)
        {
            Node hijo = hijos.item(i);
            if (hijo.getNodeType() == Node.TEXT_NODE)
            {
                hijo.setTextContent(hijo.getTextContent().trim());
            }
            borrarEspaciosBlanco(hijo);
        }
    }

}

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class Main {

  public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
    String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
    String fileName = "data.csv";
    List<Employee> list = parseCSV(columnMapping, fileName);
    String json = listToJson(list);
    writeString(json, "data.json");

    List<Employee> listxml = parseXML("data.xml");
    String jsonToXML = listToJson(listxml);
    writeString(jsonToXML, "data2.json");


  }

  public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
    try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
      ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
      strategy.setType(Employee.class);
      strategy.setColumnMapping(columnMapping);
      CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(csvReader).withMappingStrategy(strategy).build();
      return csvToBean.parse();
    } catch (IOException exception) {
      exception.printStackTrace();
    }
    return null;
  }

  public static String listToJson(List<Employee> list) {
    Type listType = new TypeToken<List<Employee>>() {
    }.getType();
    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();
    return gson.toJson(list, listType);
  }

  public static void writeString(String list, String fileName) {
    try (FileWriter fileWriter = new FileWriter(fileName)) {
      fileWriter.write(list);
      fileWriter.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
    List<Employee> employeeList = new ArrayList<>();
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(new File("data.xml"));

    Node root = document.getDocumentElement();
    NodeList nodeList = root.getChildNodes();
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      if (node instanceof Element) {
        Employee employee = new Employee();
        NodeList childNodes = node.getChildNodes();

        for (int j = 0; j < childNodes.getLength(); j++) {
          Node cNode = childNodes.item(j);
          if (cNode instanceof Element) {
            String points = cNode.getLastChild().getTextContent();

            switch (cNode.getNodeName()) {
              case "id" -> employee.setId(Long.parseLong(points));
              case "firstName" -> employee.setFirstName(points);
              case "lastName" -> employee.setLastName(points);
              case "country" -> employee.setCountry(points);
              case "age" -> employee.setAge(Integer.parseInt(points));
            }
          }
        }
        employeeList.add(employee);
      }
    }
    return employeeList;


  }
}







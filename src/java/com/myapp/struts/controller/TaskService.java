package com.myapp.struts.controller;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.*;

public class TaskService {

    private final String xmlPath;

    public TaskService(String xmlPath) {
        this.xmlPath = xmlPath;
    }

    private Document loadDocument() throws Exception {
        File file = new File(xmlPath);

        if (!file.exists()) {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.newDocument();
            Element root = doc.createElement("Tasks");
            doc.appendChild(root);
            saveDocument(doc);
            return doc;
        }

        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
    }

    private void saveDocument(Document doc) throws Exception {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(doc), new StreamResult(new File(xmlPath)));
    }

    private Task elementToTask(Element e) {
        int id = Integer.parseInt(e.getAttribute("id"));
        String title = e.getElementsByTagName("Title").item(0).getTextContent();
        String description = e.getElementsByTagName("Description").item(0).getTextContent();
        boolean completed = Boolean.parseBoolean(e.getElementsByTagName("Completed").item(0).getTextContent());
        return new Task(id, title, description, completed);
    }

    public List<Task> getAllTasks() {
        List<Task> list = new ArrayList<>();
        try {
            Document doc = loadDocument();
            NodeList nodes = doc.getElementsByTagName("Task");
            for (int i = 0; i < nodes.getLength(); i++) {
                list.add(elementToTask((Element) nodes.item(i)));
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return list;
    }

    public Task findById(int id) {
        return getAllTasks().stream()
                .filter(t -> t.getId() == id)
                .findFirst().orElse(null);
    }

    
    private int getNextId(Document doc) {
        NodeList nodes = doc.getElementsByTagName("Task");
        int maxId = 0;
        for (int i = 0; i < nodes.getLength(); i++) {
            Element e = (Element) nodes.item(i);
            String idStr = e.getAttribute("id");
            try {
                int currentId = Integer.parseInt(idStr);
                if (currentId > maxId) {
                    maxId = currentId;
                }
            } catch (NumberFormatException ignore) {
            }
        }
        return maxId + 1;
    }

    public Task addTask(Task task) {
        try {
            Document doc = loadDocument();
            Element root = doc.getDocumentElement();

            
            int newId = getNextId(doc);
            task.setId(newId);

            Element eTask = doc.createElement("Task");
            eTask.setAttribute("id", String.valueOf(newId));

            Element eTitle = doc.createElement("Title");
            eTitle.appendChild(doc.createTextNode(task.getTitle()));
            eTask.appendChild(eTitle);

            Element eDesc = doc.createElement("Description");
            eDesc.appendChild(doc.createTextNode(task.getDescription()));
            eTask.appendChild(eDesc);

            Element eCompleted = doc.createElement("Completed");
            eCompleted.appendChild(doc.createTextNode(String.valueOf(task.isCompleted())));
            eTask.appendChild(eCompleted);

            root.appendChild(eTask);

            saveDocument(doc);

            return task;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Task updateTask(int id, Task updated) {
        try {
            Document doc = loadDocument();
            NodeList nodes = doc.getElementsByTagName("Task");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element e = (Element) nodes.item(i);
                if (Integer.parseInt(e.getAttribute("id")) == id) {

                    e.getElementsByTagName("Title").item(0).setTextContent(updated.getTitle());
                    e.getElementsByTagName("Description").item(0).setTextContent(updated.getDescription());
                    e.getElementsByTagName("Completed").item(0).setTextContent(String.valueOf(updated.isCompleted()));

                    saveDocument(doc);
                    return elementToTask(e);
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return null;
    }

    public boolean deleteTask(int id) {
        try {
            Document doc = loadDocument();
            Element root = doc.getDocumentElement();
            NodeList nodes = doc.getElementsByTagName("Task");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element e = (Element) nodes.item(i);
                if (Integer.parseInt(e.getAttribute("id")) == id) {
                    root.removeChild(e);
                    saveDocument(doc);
                    return true;
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return false;
    }
}

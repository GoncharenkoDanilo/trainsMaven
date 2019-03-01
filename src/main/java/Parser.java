/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Данил
 */
public class Parser {
    
    public List<Train> parseString(String inputStr) {
        
        Document doc = Jsoup.parse(inputStr);
        Elements mainTable = doc.getElementsByTag("tbody");
        Elements lines = new Elements();
        List<Train> trains = new ArrayList<Train>();
        try {
            lines = mainTable.get(0).getElementsByTag("tr");
        } catch (Exception e) {
            return trains;
        }
        
        Elements[] columns = new Elements[lines.size()];
        
        for (int i = 0; i < columns.length; i++) {
            columns[i] = lines.get(i).getElementsByTag("td");
            Elements elemsForRemove = new Elements();
            elemsForRemove.add(columns[i].get(0));
            elemsForRemove.add(columns[i].get(3));
            elemsForRemove.add(columns[i].get(6));
            elemsForRemove.add(columns[i].get(7));
            elemsForRemove.add(columns[i].get(8));
            columns[i].removeAll(elemsForRemove);
        }
//        
//        for(Elements c : columns)
//            System.out.println(c.html());
        
        for (int i = 0; i < columns.length; i++) {
            String schedule = columns[i].get(0).child(0).text();
            String route = columns[i].get(1).child(0).text();
            String departure = columns[i].get(2).child(0).text();
            String arrival = columns[i].get(3).child(0).text();
            String cancel = columns[i].get(4).child(0).text();
            trains.add(new Train(schedule, route, departure, arrival, cancel));
        }
        
        sortTrainsByTime(trains);
        return trains;
    }
    
    public List<String> getStationList() throws MalformedURLException, IOException {
        Document doc = Jsoup.connect("https://dp.uz.gov.ua/ukr/timetable/search_between_stations").get();
        Elements selectHtml = doc.getElementsByTag("select");
        Elements optionHtml = selectHtml.get(0).getElementsByTag("option");
        
        List<String> listStr = new ArrayList<String>();
        
        for (Element e : optionHtml) {
            listStr.add(e.text());
        }
        
        List<String> toRemove = new ArrayList<String>();
        toRemove.add(listStr.get(0));
        toRemove.add(listStr.get(1));
        toRemove.add(listStr.get(2));
        listStr.removeAll(toRemove);
        
        return listStr;
    }
    
    private void sortTrainsByTime(List<Train> arr) {
        for (int i = arr.size() - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                if (arr.get(j).getDepartureNumEq() > arr.get(j + 1).getDepartureNumEq()) {
                    
                    Collections.swap(arr, j, j+1);
                }
            }
        }
    }
    
}

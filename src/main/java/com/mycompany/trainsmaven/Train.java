/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.trainsmaven;

/**
 *
 * @author Данил
 */
public class Train {
    
    private String schedule;
    private String route;
    private String departure;
    private String arrival;
    private String cancel;

    public Train(String schedule, String route, String departure, String arrival, String cancel) {
        this.schedule = schedule;
        this.route = route;
        this.departure = departure;
        this.arrival = arrival;
        this.cancel = cancel;
    }
    

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public String getCancel() {
        return cancel;
    }

    public void setCancel(String cancel) {
        this.cancel = cancel;
    }
    
    public String getInfo()
    {
        return getSchedule()+"\n"+getRoute()+"\n"+getDeparture()+" - "+getArrival()+"\n"+getCancel()+"\n";
    }
    
    public double getDepartureNumEq()
    {
        return Double.parseDouble(departure);
    }
    
    
}

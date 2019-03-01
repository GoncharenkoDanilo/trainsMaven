/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import org.telegram.telegrambots.meta.api.objects.User;

/**
 *
 * @author Данил
 */
public class ProcRequest {

    User user;
    String startStation;
    String finalStation;
    boolean isCheckListAlreadySent = false;

    public boolean isIsCheckListAlreadySent() {
        return isCheckListAlreadySent;
    }

    public void setIsCheckListAlreadySent(boolean isCheckListAlreadySent) {
        this.isCheckListAlreadySent = isCheckListAlreadySent;
    }

    public ProcRequest(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStartStation() {
        return startStation;
    }

    public void setStartStation(String startStation) {
        this.startStation = startStation;
    }

    public String getFinalStation() {
        return finalStation;
    }

    public void setFinalStation(String finalStation) {
        this.finalStation = finalStation;
    }

    public boolean isStartStationSet() {
        return startStation != null;
    }

    public boolean isFinalStationSet() {
        return finalStation != null;
    }
    
    public Integer getUserId() {
        return user.getId();
    }
    
}

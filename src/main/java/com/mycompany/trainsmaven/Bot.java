/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.trainsmaven;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 *
 * @author Данил
 */
public class Bot extends TelegramLongPollingBot {

    Parser parser;
    LevenstainAlgorytm la;
    List<ProcRequest> requests;
    List<String> stationList;

    public Bot() throws IOException {

        parser = new Parser();
        la = new LevenstainAlgorytm();
        requests = new ArrayList<ProcRequest>();
        stationList = parser.getStationList();
    }

    @Override
    public String getBotUsername() {
        return "trains_ua_bot";
        //возвращаем юзера
    }

    @Override
    public void onUpdateReceived(Update e) {
        // Тут будет то, что выполняется при получении сообщения
        Message msg;
        String textMessage;
        User user;

        try {
            msg = e.getMessage();
            textMessage = msg.getText();
            user = msg.getFrom();
        } catch (Exception ex) {
            msg = e.getCallbackQuery().getMessage();
            textMessage = e.getCallbackQuery().getData();
            user = e.getCallbackQuery().getFrom();
        }

        switch (textMessage) {

            case "/start": {
                sendMsg(msg, "Привіт! Цей бот допоможе тобі дізнатися розклад електропоїздів.");
                break;
            }

            case "/search": {
                if (!isRequestExists(user.getId())) {
                    requests.add(new ProcRequest(msg.getFrom()));
                    sendMsg(msg, "Введи назву станції відправлення:");
                } else {
                    sendMsg(msg, "Пошук вже здійснюється.");
                }
                break;
            }

            default: {
                ProcRequest temp = getPresentRequest(user.getId());
                if (!temp.isStartStationSet()) {
                    if (!temp.isCheckListAlreadySent) {
                        try {
                            sendList(msg, la.compareAll(parser.getStationList(), textMessage, 3));
                            temp.setIsCheckListAlreadySent(true);
                        } catch (IOException ex) {
                            Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        sendMsg(msg, "Введи назву станції прибуття:");
                        temp.setStartStation(textMessage);
                        temp.setIsCheckListAlreadySent(false);
                    }
                } else {
                    if (!temp.isFinalStationSet()) {
                        if (!temp.isCheckListAlreadySent) {
                            try {
                                sendList(msg, la.compareAll(parser.getStationList(), textMessage, 3));
                                temp.setIsCheckListAlreadySent(true);
                            } catch (IOException ex) {
                                Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else {
                            temp.setFinalStation(textMessage);
                            sendMsg(msg, "<b>Розклад між станціями:\n" + temp.getStartStation() + " - " + temp.getFinalStation() + "</b>");
                            sendResult(msg, temp.getStartStation(), temp.getFinalStation());
                            requests.remove(getPresentRequest(user.getId()));
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getBotToken() {
        return "721005586:AAGLGGfU_COqNjmULp3mOyvgKdKNPX3ILzk";
        //Токен бота
    }

    private void sendMsg(Message msg, String text) {
        SendMessage s = new SendMessage();
        s.setChatId(msg.getChatId()); // Боту может писать не один человек, и поэтому чтобы отправить сообщение, грубо говоря нужно узнать куда его отправлять
        s.setText(text);
        s.setParseMode("html");
        try { //Чтобы не крашнулась программа при вылете Exception 
            execute(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendResult(Message msg, String start, String fin) {

        byte[] data = getDocFromUZ(start, fin);
        String outputStr = new String(data, Charset.forName("WINDOWS-1251"));
        List<Train> trains = new ArrayList<>();
        trains = parser.parseString(outputStr);

        SendMessage s = new SendMessage();
        s.setChatId(msg.getChatId()); // Боту может писать не один человек, и поэтому чтобы отправить сообщение, грубо говоря нужно узнать куда его отправлять
        s.setParseMode("html");
        StringBuffer sb = new StringBuffer();
        boolean trainListIsEmpty = true;
        try {
            Train get = trains.get(0);
        } catch (Exception ex) {
            trainListIsEmpty = false;
        }

        if (trainListIsEmpty) {
            for (Train t : trains) {
                sb.append(t.getInfo() + "\n");
                s.setText(sb.toString());
            }

        } else {
            s.setText("Не знайдено.");
        }
        try {
            execute(s);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendList(Message msg, List<String> list) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton()
                    .setText(list.get(i))
                    .setCallbackData(list.get(i));
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            keyboardButtonsRow.add(inlineKeyboardButton);
            rowList.add(keyboardButtonsRow);
        }

        inlineKeyboardMarkup.setKeyboard(rowList);
        SendMessage message = new SendMessage()
                .setChatId(msg.getChatId())
                .setText("Можливо, ти мав на увазі...")
                .setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ProcRequest getPresentRequest(Integer userId) {
        for (ProcRequest r : requests) {
            if (Objects.equals(r.getUserId(), userId)) {
                return r;
            }
        }
        return null;
    }

    private boolean isRequestExists(Integer userId) {
        for (ProcRequest r : requests) {
            if (Objects.equals(r.getUserId(), userId)) {
                return true;
            }
        }
        return false;
    }
    
    public byte[] getDocFromUZ(String s, String f) {

        String myURL = "https://dp.uz.gov.ua/ukr/timetable/search_between_stations";
        String start = s;
        String fin = f;

        try {
            start = URLEncoder.encode(start, "WINDOWS-1251");
            fin = URLEncoder.encode(fin, "WINDOWS-1251");
        } catch (UnsupportedEncodingException ex) {

        }

        String params = "statring_station=" + start + "&station_fin1=" + fin + "&but2= ";

        byte[] data = null;
        InputStream is = null;

        try {

            URL url = new URL(myURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setRequestProperty("Content-Length", "" + Integer.toString(params.getBytes().length));
            OutputStream os = conn.getOutputStream();
            data = params.getBytes("UTF-8");
            os.write(data);
            data = null;

            conn.connect();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            is = conn.getInputStream();

            byte[] buffer = new byte[8192]; // Такого вот размера буфер
            // Далее, например, вот так читаем ответ
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            data = baos.toByteArray();
        } catch (IOException e) {
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ex) {
            }
        }
        return data;
    }
}

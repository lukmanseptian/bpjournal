package com.example.bpjournal.data.remote;

public class Server {

//    private static final String BASE_URL_API = "http://192.168.0.102:8080/WebApiBPJournal/api/";
    private static final String BASE_URL_API = "https://bpjournal.my.id/api/";
    public static Api getAPIService() {
        return Client.getClient(BASE_URL_API).create(Api.class);
    }
}

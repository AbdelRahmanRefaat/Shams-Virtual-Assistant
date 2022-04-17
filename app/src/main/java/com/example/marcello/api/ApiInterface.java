package com.example.marcello.api;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {
    @GET("create_calendar")
    public Call<Command> createCalendar();

    @GET("read_events")
    public Call<Command> getEvents();

    @GET("update_calendar")
    public Call<Command> updateEvent();

    @GET("read_contacts")
    public Call<Command> readContacts();

    @GET("add_contact")
    public Call<Command> addContact();

    @GET("delete_contact")
    public Call<Command> deleteContact();

    @GET("make_call")
    public Call<Command> makeCall();

    @GET("web_search")
    public Call<Command> webSearch();

    @POST("upload/audio")
    public Call<Command> uploadAudio(@Body HashMap<Object, Object> audio);

    @POST("upload/text")
    Call<Command> uploadText(@Body HashMap<Object, Object> text);
}

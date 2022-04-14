package com.example.marcello.dummypackage;

import retrofit2.Call;
import retrofit2.http.GET;

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
}

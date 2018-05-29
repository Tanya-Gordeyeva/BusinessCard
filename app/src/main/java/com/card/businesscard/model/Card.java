package com.card.businesscard.model;

import java.util.List;

public class Card {

    private int _id;
    private String _name;
    private String _surname;
    private String _fathername;
    private List<String> _phone;
    private String _address;
    private List<String> _email;
    private List<String> _site;
    private String _image;
    private String _notes;

    public Card(int id, String name, String surname, String fathername, List<String> phone, String address, List<String> email, List<String> site, String image, String notes) {
        this._id = id;
        this._name = name;
        this._surname = surname;
        this._fathername = fathername;
        this._phone = phone;
        this._address = address;
        this._email = email;
        this._site = site;
        this._image = image;
        this._notes = notes;
    }

    public Card(){}

    public List<String> get_phone() {
        return _phone;
    }

    public void set_phone(List<String> _phone) {
        this._phone = _phone;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_surname() {
        return _surname;
    }

    public void set_surname(String _surname) {
        this._surname = _surname;
    }

    public String get_fathername() {
        return _fathername;
    }

    public void set_fathername(String _fathername) {
        this._fathername = _fathername;
    }

    public List<String> get_email() {
        return _email;
    }

    public void set_email(List<String> _email) {
        this._email = _email;
    }

    public void set_address(String _address) {
        this._address = _address;
    }

    public String get_address() {

        return _address;
    }

    public List<String> get_site() {
        return _site;
    }

    public void set_image(String _image) {
        this._image = _image;
    }

    public String get_notes() {
        return _notes;
    }

    public void set_notes(String _notes) {
        this._notes = _notes;
    }

    public String get_image() {
        return _image;
    }

    public void set_site(List<String> _site) {
        this._site = _site;
    }
}

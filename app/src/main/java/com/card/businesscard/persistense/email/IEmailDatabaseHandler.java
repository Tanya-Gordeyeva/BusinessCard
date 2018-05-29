package com.card.businesscard.persistense.email;


import java.util.List;

public interface IEmailDatabaseHandler {
    public List<String> getEmail(int _id);

    public void addEmail(String email,int id);

    public int updateEmail(String email);

    public void deleteEmail(int card_id);
}

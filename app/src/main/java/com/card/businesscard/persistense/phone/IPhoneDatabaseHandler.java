package com.card.businesscard.persistense.phone;


import java.util.List;

public interface IPhoneDatabaseHandler {
    public List<String> getPhone(int _id);

    public void addPhone(String Phone, int id);

    public int updatePhone(String Phone);

    public void deletePhone(int card_id);
}

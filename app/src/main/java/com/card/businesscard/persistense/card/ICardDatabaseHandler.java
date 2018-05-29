package com.card.businesscard.persistense.card;


import android.content.Context;

import com.card.businesscard.model.Card;

import java.util.List;

public interface ICardDatabaseHandler {
    /*Card methods*/
    public Card getCard(int _id);

    public Long addCard(Card card);

    public List<Card> getAllCards();

    public int updateCard(Card card);

    public void deleteCard(int id);

    public void deleteAllCards();

    public void deleteSomeCards(List<Card> cards);

    /*end of card methods*/
}

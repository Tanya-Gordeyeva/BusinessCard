package com.card.businesscard.persistense.site;


import java.util.List;

public interface ISiteDatabaseHandler {
    public List<String> getSite(int _id);

    public void addSite(String site, int id);

    public int updateSite(String site);

    public void deleteSite(int card_id);
}

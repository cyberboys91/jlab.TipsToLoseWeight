package jlab.TipsToLoseWeight.Activity.Utils;

/*
 * Created by Javier on 21/03/2020.
 */

public class Tip {

    private int id;
    private String title;
    private String description;
    private String curiosity;
    private int TipId;

    public Tip(int id, String title, String description, String curiosity, int tipId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.curiosity = curiosity;
        this.TipId = tipId;
    }

    public int getId() {
        return id;
    }

    public int getTipId() {
        return TipId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCuriosity() {
        return curiosity;
    }
}

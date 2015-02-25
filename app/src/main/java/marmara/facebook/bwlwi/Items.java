package marmara.facebook.bwlwi;

/**
 * Created by SERCAN on 08.02.2015.
 */
public class Items {
    private String title;
    private String description;
    private String tarih;
    private String Yorumsay;
    private String PostID;
    public Items(String title, String description, String tarih, String Yorumsay, String PostID) {
        super();
        this.title = title;
        this.description = description;
        this.tarih = tarih;
        this.Yorumsay = Yorumsay;
        this.PostID = PostID;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getTarih() {
        return tarih;
    }
    public String getYorumsay() {
        return Yorumsay;
    }
    public String getPostID(){ return PostID; }
}

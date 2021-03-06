package eu.anifantakis.neakriti.data.feed;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class ArticlesCollection implements Parcelable {
    private String listName;
    private int listType;
    private String listId;
    private List<Article> articleList;

    public ArticlesCollection(String listName, int listType, String listId) {
        this.listName = listName;
        this.listType = listType;
        this.listId = listId;
        articleList = new ArrayList<>();
    }

    public ArticlesCollection(List<Article> articlesList, String listName, int listType, String listId) {
        this.listName = listName;
        this.listType = listType;
        this.listId = listId;
        articleList = articlesList;
    }

    protected ArticlesCollection(Parcel in) {
        listName = in.readString();
        listType = in.readInt();
        listId = in.readString();
        articleList = in.createTypedArrayList(Article.CREATOR);
    }

    public String getListName(){
        return listName;
    }

    public int getListType(){
        return listType;
    }

    public String getListId(){
        return listId;
    }

    public List<Article> getArticleList(){
        return articleList;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(listName);
        dest.writeInt(listType);
        dest.writeString(listId);
        dest.writeTypedList(articleList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ArticlesCollection> CREATOR = new Creator<ArticlesCollection>() {
        @Override
        public ArticlesCollection createFromParcel(Parcel in) {
            return new ArticlesCollection(in);
        }

        @Override
        public ArticlesCollection[] newArray(int size) {
            return new ArticlesCollection[size];
        }
    };

    public int addArticle(Article article) {
        articleList.add(article);
        return articleList.size();
    }

    public void setArticlesList(List<Article> articlesList){
        this.articleList=articlesList;
    }

    public void clear(){
        articleList.clear();
    }

    public Article getArticle(int location) {
        return articleList.get(location);
    }

    public int getCollectionSize() {
        return articleList.size();
    }
}

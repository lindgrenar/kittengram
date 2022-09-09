package entities;

public class KittenPost {
  private long id;
  private String title;
  private String description;
  private String image;
  private int likes = 0;
  private long published;

  public KittenPost(long id, String title, String description, String image, int likes, long published) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.image = image;
    this.likes = likes;
    this.published = published;
  }

  public KittenPost() {  }
  
  public long getId() {
    return id;
  }
  
  public void setId(long id) {
    this.id = id;
  }
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public String getImage() {
    return image;
  }
  
  public void setImage(String image) {
    this.image = image;
  }
  
  public int getLikes() {
    return likes;
  }
  
  public void setLikes(int likes) {
    this.likes = likes;
  }

  public long getPublished() {
    return published;
  }

  public void setPublished(long published) {
    this.published = published;
  }
}

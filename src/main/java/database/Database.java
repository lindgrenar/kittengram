package database;

import entities.KittenPost;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Database {
  private Connection conn;
  private boolean useMySQL;
  
  public Database(boolean useMySQL, String user, String password) {
    this.useMySQL = useMySQL;
    try {
      String connectString;
      
      if (useMySQL) {
        connectString = "jdbc:mysql://localhost/kittengram?user=" + user + "&password=" + password + "&createDatabaseIfNotExist=true&autoReconnect=true&serverTimezone=UTC";
        Class.forName("com.mysql.cj.jdbc.Driver").newInstance(); // manually add mysql-driver to classpath
      } else {
        connectString = "jdbc:sqlite:data.db";
      }
      
      conn = DriverManager.getConnection(connectString);
      createTables();

    } catch (Exception e) {
      if (e.getMessage().startsWith("Access denied")) {
        System.err.println(e.toString());
        System.exit(1);
      }
      e.printStackTrace();
    }
  }
  
  private void createTables() {
    String kitten_posts_query = "CREATE TABLE IF NOT EXISTS kitten_posts ( " +
        (useMySQL
        ? "id INT PRIMARY KEY AUTO_INCREMENT," 
        : "id INTEGER PRIMARY KEY AUTOINCREMENT,") +
        "title TEXT NOT NULL, " +
        "description TEXT NOT NULL, " +
        "image TEXT NOT NULL, " +
        "likes " + (useMySQL ? "INT" : "INTEGER") + " DEFAULT '0', " +
        "published " + (useMySQL ? "INT" : "INTEGER") + " NOT NULL" +
        ")";
  
    try {
      var stmt = conn.prepareStatement(kitten_posts_query);
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  
  public List<KittenPost> getKittenPosts() {
    List<KittenPost> kittenPosts = new ArrayList<>();
    
    String query = "SELECT * FROM kitten_posts ORDER BY published DESC";
  
    try {
      var stmt = conn.prepareStatement(query);
      var rs = stmt.executeQuery();
      
      while (rs.next()) {
        kittenPosts.add(new KittenPost(
           rs.getLong("id"),
           rs.getString("title"),
           rs.getString("description"),
           rs.getString("image"),
           rs.getInt("likes"),
           rs.getLong("published")
        ));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return kittenPosts;
  }
  
  public KittenPost getKittenPostById(long id) {
    String query = "SELECT * FROM kitten_posts WHERE id = ?";
    
    try {
      var stmt = conn.prepareStatement(query);
      stmt.setLong(1, id);
      var rs = stmt.executeQuery();
      
      rs.next();
      
      return new KittenPost(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getString("description"),
            rs.getString("image"),
            rs.getInt("likes"),
            rs.getLong("published")
        );
    } catch (SQLException e) {
      if (!e.getMessage().equals("ResultSet closed") && !e.getMessage().equals("Illegal operation on empty result set.")) {
        e.printStackTrace();
      }
    }
    return null;
  }
  
  public KittenPost newKittenPost(KittenPost kittenPost) {
    String query = "INSERT INTO kitten_posts VALUES(NULL, ?, ?, ?, ?, ?)";
  
    try {
      var stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      stmt.setString(1, kittenPost.getTitle());
      stmt.setString(2, kittenPost.getDescription());
      stmt.setString(3, kittenPost.getImage());
      stmt.setInt(4, kittenPost.getLikes());
      stmt.setLong(5, (System.currentTimeMillis() / 1000));

      stmt.executeUpdate();
      var rs = stmt.getGeneratedKeys();
      rs.next();
      kittenPost.setId(rs.getLong(1));
      
      return kittenPost;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public int likeKittenPost(long id) {
    String query = "UPDATE kitten_posts SET likes = likes + 1 WHERE id = ?";
  
    try {
      var stmt = conn.prepareStatement(query);
      stmt.setLong(1, id);
      stmt.executeUpdate();
      
      return 1;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return 0;
  }

  public int unlikeKittenPost(long id) {
    String query = "UPDATE kitten_posts SET likes = likes - 1 WHERE id = ? AND likes > 0";

    try {
      var stmt = conn.prepareStatement(query);
      stmt.setLong(1, id);
      stmt.executeUpdate();

      return 1;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return 0;
  }
}

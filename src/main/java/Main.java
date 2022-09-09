import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import database.Database;
import entities.KittenPost;
import express.Express;
import io.javalin.core.util.FileUtil;

import java.nio.file.Paths;
import java.util.List;

public class Main {
  public static void main(String[] args) {
    String envPort = System.getenv("PORT");
    int port = envPort == null ? 4000 : Integer.parseInt(envPort);
    boolean useMySQL = false;
    String user = "user";
    String password = "password";

    for (var arg : args) {
      String[] split = arg.split("=");
      if (split[0].contains("port")) {
        port = Integer.parseInt(split[1]);
      } else if (split[0].contains("mysql")) {
        useMySQL = Boolean.parseBoolean(split[1]);
      } else if (split[0].contains("user")) {
        user = split[1];
      } else if (split[0].contains("password")) {
        password = split[1];
      }
    }

    var app = new Express();
    var db = new Database(useMySQL, user, password);
    
    addSampleData(db);
    
    app.useStatic(Paths.get("www"));
    
    app.get("/rest/kittens", (req, res) -> {
      var kittenPosts = db.getKittenPosts();
      res.json(kittenPosts);
    });
    
    app.get("/rest/kittens/:id", (req, res) -> {
      var id = Long.parseLong(req.params("id"));
      var kittenPost = db.getKittenPostById(id);
      res.json(kittenPost);
    });
    
    app.post("/rest/kittens", (req, res) -> {
      var kittenPost = req.body(KittenPost.class);
      var newKittenPost = db.newKittenPost(kittenPost);
      res.json(newKittenPost);
    });

    app.post("/api/upload-post", (req, res) -> {
      var file = req.formDataFile("file");
      var title = req.formData("title");
      var description = req.formData("description");
      var imageUrl = "/uploads/" + NanoIdUtils.randomNanoId() + "_" + file.getFilename();
      FileUtil.streamToFile(file.getContent(), "www" + imageUrl);

      var kittenPost = new KittenPost();
      kittenPost.setTitle(title);
      kittenPost.setDescription(description);
      kittenPost.setImage(imageUrl);
      kittenPost.setPublished(System.currentTimeMillis() / 1000);

      var newKittenPost = db.newKittenPost(kittenPost);
      res.json(newKittenPost);
    });
    
    app.patch("/api/like-kitten/:id", (req, res) -> {
      var id = Long.parseLong(req.params("id"));
      var liked = db.likeKittenPost(id);
      res.send(liked);
    });

    app.patch("/api/unlike-kitten/:id", (req, res) -> {
      var id = Long.parseLong(req.params("id"));
      var unliked = db.unlikeKittenPost(id);
      res.send(unliked);
    });
    
    app.listen(port);
  }
  
  static void addSampleData(Database db) {
    if (db.getKittenPostById(1) != null) return;
    
    var posts = List.of(
        new KittenPost(1, "Meow", "Cutest kitten ever", "/uploads/kitten.jpg", 1, (System.currentTimeMillis() / 1000)),
        new KittenPost(2, "Cute Kitten", "I'm melting!", "/uploads/cute-kitten.jpg", 2, (System.currentTimeMillis() / 1000) - 1000),
        new KittenPost(3, "Meow cute Kitten", "Isn't this the cutest kitten ever?", "/uploads/kitten.jpg", 3, (System.currentTimeMillis() / 1000) - 2000)
    );
    
    for (var post : posts) db.newKittenPost(post);
  }
}

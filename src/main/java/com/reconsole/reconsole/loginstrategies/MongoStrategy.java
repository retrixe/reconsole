package com.reconsole.reconsole.loginstrategies;

import com.reconsole.reconsole.loginstrategies.LoginStrategy;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;

import org.bson.Document;

import org.bukkit.plugin.java.JavaPlugin;

public class MongoStrategy implements LoginStrategy {
  private JavaPlugin plugin;
  String connectionString = plugin.getConfig().getConfigurationSection("mongodb").getString("connection-url");
  MongoClient mongoClient = MongoClients.create(connectionString);
  MongoDatabase database = mongoClient.getDatabase("reconsole");

  public MongoStrategy(JavaPlugin javaPlugin) { plugin = javaPlugin; }

  public boolean validate (String username, String hashedPass) {
    MongoCollection<Document> users = database.getCollection("users");
    FindIterable<Document> user = users.find(
      Filters.and(Filters.eq("username", username), Filters.eq("password", hashedPass))
    );
    if (user.first() == null) return false;
    return true;
  }
}

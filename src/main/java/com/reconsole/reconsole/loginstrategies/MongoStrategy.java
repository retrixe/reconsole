package com.reconsole.reconsole.loginstrategies;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;

import org.bson.Document;

import org.bukkit.plugin.java.JavaPlugin;

public class MongoStrategy implements LoginStrategy {
    private MongoDatabase database;

    public MongoStrategy(JavaPlugin javaPlugin) {
        String connectionString = javaPlugin
            .getConfig()
            .getConfigurationSection("mongodb")
            .getString("connection-url");
        MongoClient mongoClient = MongoClients.create(connectionString);
        database = mongoClient.getDatabase("reconsole");
    }

    public boolean authenticate (String username, String hashedPass) {
        MongoCollection<Document> users = database.getCollection("users");
        FindIterable<Document> user = users.find(
                Filters.and(Filters.eq("username", username), Filters.eq("password", hashedPass))
        );
        return user.first() != null;
    }
}

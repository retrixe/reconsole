package com.reconsole.reconsole.loginstrategies;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;

import org.bukkit.plugin.java.JavaPlugin;

public class MongoStrategy implements LoginStrategy {
    private MongoDatabase database;

    public MongoStrategy(JavaPlugin javaPlugin) {
        // Get the connection string and create a client.
        String connectionString = javaPlugin
            .getConfig()
            .getConfigurationSection("mongodb")
            .getString("connection-url");
        MongoClient mongoClient = MongoClients.create(connectionString);
        database = mongoClient.getDatabase("reconsole");
    }

    @Override
    public boolean authenticate (String username, String hashedPass) {
        // Get the users collection and find a user with these credentials.
        MongoCollection<Document> users = database.getCollection("users");
        FindIterable<Document> user = users.find(
                Filters.and(Filters.eq("username", username), Filters.eq("password", hashedPass))
        );
        // User with these credentials exists or not is returned.
        return user.first() != null;
    }

    @Override
    public boolean register (String username, String hashedPass) {
        // Get the users and check if a user with this username exists.
        MongoCollection<Document> users = database.getCollection("users");
        FindIterable<Document> possibleUser = users.find(Filters.eq("username", username));
        if (possibleUser.first() != null) return false; // If there is one, we return false to indicate this.
        // Else, we insert a new user and return true.
        Document user = new Document("username", username).append("password", hashedPass);
        users.insertOne(user);
        return true;
    }

    @Override
    public boolean delete (String username) {
        // Get the users and attempt to delete a document.
        MongoCollection<Document> users = database.getCollection("users");
        DeleteResult result = users.deleteOne(Filters.eq("username", username));
        return result.getDeletedCount() > 0; // If a document was deleted, it was successful.
    }

    @Override
    public boolean changepw (String username, String hashedPass) {
        // Get the users and check if a user with this username exists.
        MongoCollection<Document> users = database.getCollection("users");
        FindIterable<Document> possibleUser = users.find(Filters.eq("username", username));
        if (possibleUser.first() == null) return false; // If there is no user, we return false.
        // Else, we update the user and return true.
        Document update = new Document("$set", new Document("password", hashedPass));
        UpdateResult updateResult = users.updateOne(Filters.eq("username", username), update);
        return updateResult.getModifiedCount() > 0;
    }
}

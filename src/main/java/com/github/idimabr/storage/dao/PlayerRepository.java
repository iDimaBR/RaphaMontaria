package com.github.idimabr.storage.dao;

import com.github.idimabr.models.PlayerCache;
import com.github.idimabr.storage.adapter.PlayerAdapter;
import com.henryfabio.sqlprovider.connector.SQLConnector;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import static com.github.idimabr.RaphaMontaria.GSON;

@RequiredArgsConstructor
public class PlayerRepository {

    private final SQLConnector plugin;

    public void createTable() {
        this.executor().updateQuery("CREATE TABLE IF NOT EXISTS players(" +
                "`uuid` varchar(36) PRIMARY KEY NOT NULL, " +
                "`horses` TEXT NOT NULL DEFAULT []," +
                "`friends` TEXT NOT NULL DEFAULT []" +
                ")");
    }

    public void update(PlayerCache data) {
        this.executor().updateQuery(
                "REPLACE INTO players(" +
                        "uuid," +
                        "horses," +
                        "friends" +
                        ") VALUES(?,?,?)",
                statement -> {
                    statement.set(1, data.getUuid().toString());
                    statement.set(2, GSON.toJson(data.getHorseData()));
                    statement.set(3, GSON.toJson(data.getFriends()));
                });
    }

    public PlayerCache load(UUID uuid) {
        return this.executor().resultOneQuery(
                "SELECT * FROM players WHERE uuid = ?;",
                statement -> statement.set(1, uuid.toString()),
                PlayerAdapter.class
        );
    }

    private SQLExecutor executor() {
        return new SQLExecutor(plugin);
    }

}
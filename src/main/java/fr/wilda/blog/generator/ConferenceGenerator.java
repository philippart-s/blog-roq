package fr.wilda.blog.generator;

import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

@ApplicationScoped
public class ConferenceGenerator {
    @Inject
    @Named("conferences")
    JsonObject allConferences;

    String frontMatter = """
---
title: "🎤 Talks donnés à %s 🎤"
description: Liste de talks donnés lors de la conférence %s
image: %s
layout: conference
author: wilda
conference-name: %s
link: %s
---""";

    /** Méthode appelée dès que l’application est prête. */
    void onStart(@Observes StartupEvent ev) throws IOException {
        Log.info("🚀 Conference pages generation...");
        Map<String, Object> mapOfAllConferences = allConferences.getMap();

        for (var entry : mapOfAllConferences.entrySet()) {
            JsonArray conferences = (JsonArray) entry.getValue();
            for (var conference : conferences) {
                JsonObject jsonConf = (JsonObject) conference;
                Path dir = Path.of("./content/posts/conferences/" + jsonConf.getString("postDate") + "-" + jsonConf.getString("talksUrl"));
                if (!Files.isDirectory(dir)) {
                    Files.createDirectories(dir);
                }
                Path file = Path.of(dir + "/index.markdown");
                if (!Files.exists(file)) {
                    Files.createDirectories(dir);
                    Files.write(file,
                            frontMatter.formatted(jsonConf.getString("name"),
                                    jsonConf.getString("name"),
                                    (Files.exists(Path.of("./public/images/conferences/" + jsonConf.getString("talksUrl") + ".png")) ?
                                            "conferences/" + jsonConf.getString("talksUrl") + ".png" :
                                            "conferences/conference.jpg"),
                                    jsonConf.getString("talksUrl"),
                                    jsonConf.getString("talksUrl")).getBytes(),
                            StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING,
                            StandardOpenOption.WRITE);
                }
            }
        }

/*        JsonObject conference = allConferences.getJsonArray("2025").getJsonObject(0);


        Path dir = Path.of("./content/posts/conferences/" + conference.getString("postDate") + "-" + conference.getString("talksUrl"));
        if (!Files.isDirectory(dir)) {
            Files.createDirectories(dir);
        }
        Path file = Path.of(dir + "/index.markdown");
        if (!Files.exists(file)) {
            Files.createDirectories(dir);
            Files.write(file,
                    frontMatter.formatted(conference.getString("name"),
                            conference.getString("name"),
                            (Files.exists(Path.of("./public/images/conferences/" + conference.getString("talksUrl") + ".png")) ?
                                    "conferences/" + conference.getString("talksUrl") + ".png" :
                                    "conferences/conference.jpg"),
                            conference.getString("talksUrl"),
                            conference.getString("talksUrl")).getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
        }*/
        Log.info("✅ Conference pages generated ✅");
    }
}

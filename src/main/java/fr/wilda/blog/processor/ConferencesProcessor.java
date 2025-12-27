package fr.wilda.blog.processor;

import fr.wilda.blog.data.Talk;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.List;
import java.util.Map;

@ApplicationScoped
@Named("myConfs")
public class ConferencesProcessor {


    @Inject
    @Named("conferences")
    JsonObject talks;

    public List<Talk> getByIds(String id) {
        List<Talk> filtered = talks.stream() // stream sur les années
                .map(entry -> (Map.Entry<String, Object>) entry)
                .map(Map.Entry::getValue)
                .map(v -> (JsonArray) v) // chaque valeur = JsonArray d'events
                .flatMap(JsonArray::stream)
                .map(event -> (JsonObject) event)
                .flatMap(event -> event.getJsonArray("talks").stream()
                        .map(t -> (JsonObject) t)
                        .filter(talk -> id.equals(talk.getString("id")))
                        .map(talk -> new Talk(talk.getString("id"),
                                event.getString("name"),
                                talk.getString("date"),
                                event.getString("postDate"),
                                event.getString("talksUrl")))
                )
                .toList();
        //System.out.println(filtered);

        return filtered;
    }

    public List<JsonObject> getByUrl(String url) {
        List<JsonObject> filtered = talks.stream()
                .map(entry -> (Map.Entry<String, Object>) entry)
                .map(Map.Entry::getValue)                        // → JsonArray
                .map(v -> (JsonArray) v)                        // cast sûr
                .flatMap(events -> events.stream())             // stream des events
                .map(event -> (JsonObject) event)
                .filter(event -> url.equals(event.getString("talksUrl")))
                .toList();
        return filtered;
    }

}

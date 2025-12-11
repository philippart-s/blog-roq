package fr.wilda.blog.processor;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@ApplicationScoped
@Named("myConfs")
public class ConferencesProcessor {



  @Inject
  @Named("conferences")
  JsonObject confsMap;

  public Map<String, String> getByIds(String id) {

    System.out.println(confsMap.stream().findFirst().get());
    return Map.of("test", "test");
  }
}

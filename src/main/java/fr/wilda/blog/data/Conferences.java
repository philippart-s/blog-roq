package fr.wilda.blog.data;

import io.quarkiverse.roq.data.runtime.annotations.DataMapping;

import java.util.List;
import java.util.Objects;

@DataMapping(value = "conferences", parentArray = true)
public record Conferences(List<Conference> confs) {
  public record Conference(String name, String postDate, String date, String talksUrl, String excerpt,
                           String categories, List<String> tags, List<Talk> talks) {

    @Override
    public boolean equals(Object o) {
      System.out.println(o);
      if (o == null || String.class != o.getClass()) return false;
      String talksUrl = (String) o;
      return Objects.equals(talksUrl, talksUrl);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(talksUrl);
    }
  }

}

package test.unit;

import com.example.app.Login;
import com.example.app.Page;
import com.example.app.SearchPage;
import com.example.domain.Products;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.UriInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.Mockito.mock;

class SearchPageTest {
    @Test void shouldContainTitle() {
        var document = document(null, null);

        then(document.title()).isEqualTo("Product Search");
        then(document.select("h1").text()).isEqualTo("Product Search");
    }

    @Test void shouldContainEmptySearchField() {
        var document = document(null, null);

        var searchField = document.select("#search-input").first();
        then(searchField).isNotNull();
        then(searchField.attr("name")).isEqualTo("search");
        then(searchField.attr("type")).isEqualTo("text");
        then(searchField.attr("placeholder")).isEqualTo("Search...");
        then(searchField.val()).isEqualTo("");
    }

    @Test void shouldContainSearchFieldWithText() {
        var document = document("throne", null);

        var searchField = document.select("#search-input").first();
        then(searchField).isNotNull();
        then(searchField.val()).isEqualTo("throne");
    }

    @Test void shouldContainFilteredShoeSize() {
        var document = document("shoe", "size=12");

        var resultList = document.select("#result-list").first();
        then(resultList).isNotNull();
        var matches = resultList.select("article.media");
        then(matches).hasSize(3);
        then(matches.get(0).select("div.media-content > div > strong").text())
                .isEqualTo("Da Lil Shoe");
        then(matches.get(1).select("div.media-content > div > strong").text())
                .isEqualTo("Da Ota Shoe");
        then(matches.get(2).select("div.media-content > div > strong").text())
                .isEqualTo("Da Shoe");
    }

    private static Document document(String search, String filterString) {
        var page = new SearchPage(new Page(mock(HttpSession.class), mock(UriInfo.class), mock(Login.class)), new Products());
        var rendered = page.searchPage(search, filterString).render();
        return Jsoup.parse(rendered);
    }
}

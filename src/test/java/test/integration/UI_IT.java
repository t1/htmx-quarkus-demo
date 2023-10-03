package test.integration;

import com.microsoft.playwright.Locator;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.BDDAssertions.then;

@QuarkusIntegrationTest
class UI_IT {
    @RegisterExtension static Player player = new Player();

    private Locator searchInput;
    private Locator resultList;
    private Locator firstBrand;

    @BeforeEach
    void setUp() {
        // we can't initialize these on the vars, because the player didn't yet run the beforeEach
        this.firstBrand = player.locator(".brand\\:first-brand").first();
        this.searchInput = player.locator("#search-input").first();
        this.resultList = player.locator("#result-list");
    }

    @Test
    void shouldPing() {
        player.navigate("http://localhost:8081");
        player.screenshot("search-page-1.png");
        then(player.title()).isEqualTo("Product Search");

        searchInput.fill("sh");
        searchInput.press("Meta+Enter");
        player.screenshot("search-page-2.png");
        assertThat(resultList).containsText("Da Lil Shoe");
        assertThat(resultList).containsText("Da Ota Shoe");
        assertThat(resultList).containsText("Da Shoe");

        then(firstBrand.getAttribute("class")).doesNotContain("is-active");
        firstBrand.click();
        player.screenshot("search-page-3.png");
        assertThat(firstBrand).hasClass(Pattern.compile(".*is-active.*"));
        assertThat(resultList).containsText("Da Lil Shoe");
        then(resultList.textContent()).doesNotContain("Da Ota Shoe");
        assertThat(resultList).containsText("Da Shoe");
    }
}

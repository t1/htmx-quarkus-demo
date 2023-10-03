package test.integration;

import com.microsoft.playwright.Locator;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.stream.Stream;

import static test.integration.CustomAssertions.then;

@QuarkusIntegrationTest
class SearchPageIT {
    @RegisterExtension static Player player = new Player();

    private Locator searchInput;
    private Locator resultList;
    private Locator firstBrand;
    private Locator otaBrand;
    private Locator size10;
    private Locator size11;
    private Locator size12;

    @BeforeEach
    void setUp() {
        // we can't initialize these on the vars, because the player didn't yet run the beforeEach
        this.searchInput = player.locator("#search-input").first();
        this.resultList = player.locator("#result-list");
        this.firstBrand = player.locator(".brand\\:first-brand").first();
        this.otaBrand = player.locator(".brand\\:ota-brand").first();
        this.size10 = player.locator(".size\\:10").first();
        this.size11 = player.locator(".size\\:11").first();
        this.size12 = player.locator(".size\\:12").first();
    }

    @Test
    void shouldFindShoes() {
        player.navigate("http://localhost:8081");
        player.screenshot("search-page-1.png");
        then(player).hasTitle("Product Search");

        searchInput.fill("shoe");
        searchInput.press("Meta+Enter");
        player.screenshot("search-page-2.png");
        then(resultList).contains("Da Lil Shoe", "Da Ota Shoe", "Da Shoe");
        thenActiveBrand(null);
        thenActiveSize(null);

        firstBrand.click();
        player.screenshot("search-page-3.png");
        thenActiveBrand(firstBrand);
        then(resultList).contains("Da Lil Shoe", "Da Shoe");
        then(resultList).doesNotContain("Da Ota Shoe");

        size10.click();
        player.screenshot("search-page-4.png");
        thenActiveBrand(firstBrand);
        thenActiveSize(size10);
        then(resultList).contains("Da Lil Shoe");
        then(resultList).doesNotContain("Da Ota Shoe", "Da Shoe");
    }

    private void thenActiveSize(Locator size) {
        Stream.of(size10, size11, size12).forEach(s -> then(s).isActive(s == size));
    }

    private void thenActiveBrand(Locator brand) {
        Stream.of(firstBrand, otaBrand).forEach(b -> then(b).isActive(b == brand));
    }
}

package test.integration;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.assertions.PageAssertions;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.impl.LocatorAssertionsImpl;
import org.assertj.core.api.BDDAssertions;

import java.util.regex.Pattern;
import java.util.stream.Stream;

public class CustomAssertions extends BDDAssertions {
    public static ExtendedLocatorAssertions then(Locator locator) {
        return new ExtendedLocatorAssertions(locator);
    }

    public static PageAssertions then(Player player) {
        return PlaywrightAssertions.assertThat(player.getPage());
    }

    public static class ExtendedLocatorAssertions extends LocatorAssertionsImpl {

        public ExtendedLocatorAssertions(Locator locator) {super(locator);}

        public ExtendedLocatorAssertions contains(String... strings) {
            Stream.of(strings).forEach(this::containsText);
            return this;
        }

        public ExtendedLocatorAssertions doesNotContain(String... strings) {
            Stream.of(strings).forEach(expected -> not().containsText(expected, null));
            return this;
        }

        public ExtendedLocatorAssertions isNotActive() {
            not().hasClass(IS_ACTIVE_PATTERN);
            return this;
        }

        public ExtendedLocatorAssertions isActive() {
            hasClass(IS_ACTIVE_PATTERN);
            return this;
        }

        public ExtendedLocatorAssertions isActive(boolean active) {
            return active ? isActive() : isNotActive();
        }

        // TODO this would also match `not-is-active` or `is-active-fool`
        private static final Pattern IS_ACTIVE_PATTERN = Pattern.compile(".*is-active.*");
    }
}

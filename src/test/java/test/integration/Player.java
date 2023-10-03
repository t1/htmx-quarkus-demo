package test.integration;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Page.ScreenshotOptions;
import com.microsoft.playwright.Playwright;
import lombok.experimental.Delegate;
import org.junit.jupiter.api.extension.*;

import java.nio.file.Paths;

import static com.microsoft.playwright.BrowserType.LaunchOptions;
import static com.microsoft.playwright.Tracing.StartOptions;
import static com.microsoft.playwright.Tracing.StopOptions;

public class Player implements Extension, BeforeAllCallback, BeforeEachCallback, AfterEachCallback, AfterAllCallback {
    private static final LaunchOptions SLOW_MO = new LaunchOptions().setHeadless(false).setSlowMo(1000);

    private Playwright playwright;
    private Browser browser;

    private BrowserContext context;
    @Delegate private Page page;

    @Override public void beforeAll(ExtensionContext jupiterContext) {
        playwright = Playwright.create();
        browser = playwright.webkit().launch(Boolean.getBoolean("slowmo") ? SLOW_MO : null);
    }

    @Override public void beforeEach(ExtensionContext jupiterContext) {
        context = browser.newContext();
        page = context.newPage();
        context.tracing().start(new StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(System.getenv("PLAYWRIGHT_JAVA_SRC") != null));
    }

    @Override public void afterEach(ExtensionContext jupiterContext) {
        context.tracing().stop(new StopOptions().setPath(Paths.get("target/playwright-trace.zip")));
        context.close();
    }

    @Override public void afterAll(ExtensionContext jupiterContext) {
        playwright.close();
    }

    public void screenshot(String fileName) {
        page.screenshot(new ScreenshotOptions().setPath(Paths.get("target/screenshots/" + fileName)));
    }
}

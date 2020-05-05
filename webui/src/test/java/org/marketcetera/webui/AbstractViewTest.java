package org.marketcetera.webui;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.vaadin.flow.theme.AbstractTheme;
import com.vaadin.testbench.ScreenshotOnFailureRule;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.parallel.ParallelTest;

/**
 * Base class for TestBench IntegrationTests on chrome.
 * <p>
 * The tests use Chrome driver (see pom.xml for integration-tests profile) to
 * run integration tests on a headless Chrome. If a property {@code test.use
 * .hub} is set to true, {@code AbstractViewTest} will assume that the
 * TestBench test is running in a CI environment. In order to keep the this
 * class light, it makes certain assumptions about the CI environment (such
 * as available environment variables). It is not advisable to use this class
 * as a base class for you own TestBench tests.
 * <p>
 * To learn more about TestBench, visit
 * <a href="https://vaadin.com/docs/v10/testbench/testbench-overview.html">Vaadin TestBench</a>.
 */
public abstract class AbstractViewTest extends ParallelTest {
    private static final int SERVER_PORT = 8080;

    private final String route;
    private final By rootSelector;

    @Rule
    public ScreenshotOnFailureRule rule = new ScreenshotOnFailureRule(this,
            false);

    public AbstractViewTest() {
        this("", By.tagName("body"));
    }

    protected AbstractViewTest(String route, By rootSelector) {
        this.route = route;
        this.rootSelector = rootSelector;
    }

    @Before
    public void setup() throws Exception {
        if (isUsingHub()) {
            super.setup();
        } else {
            setDriver(TestBench.createDriver(new ChromeDriver()));
        }
        getDriver().get(getURL(route));
    }

    /**
     * Convenience method for getting the root element of the view based on
     * the selector passed to the constructor.
     *
     * @return the root element
     */
    protected WebElement getRootElement() {
        return findElement(rootSelector);
    }

    /**
     * Asserts that the given {@code element} is rendered using a theme
     * identified by {@code themeClass}. If the theme is not found, JUnit
     * assert will fail the test case.
     *
     * @param element       web element to check for the theme
     * @param themeClass    theme class (such as {@code Lumo.class}
     */
    protected void assertThemePresentOnElement(
            WebElement element, Class<? extends AbstractTheme> themeClass) {
        String themeName = themeClass.getSimpleName().toLowerCase();
        Boolean hasStyle = (Boolean) executeScript("" +
                "var styles = Array.from(arguments[0]._template.content" +
                ".querySelectorAll('style'))" +
                ".filter(style => style.textContent.indexOf('" +
                themeName + "') > -1);" +
                "return styles.length > 0;", element);

        Assert.assertTrue("Element '" + element.getTagName() + "' should have" +
                        " had theme '" + themeClass.getSimpleName() + "'.",
                hasStyle);
    }

    /**
     * Property set to true when running on a test hub.
     */
    private static final String USE_HUB_PROPERTY = "test.use.hub";

    /**
     * Returns deployment host name concatenated with route.
     *
     * @return URL to route
     */
    private static String getURL(String route) {
        return String.format("http://%s:%d/%s", getDeploymentHostname(),
                SERVER_PORT, route);
    }

    /**
     * Returns whether we are using a test hub. This means that the starter
     * is running tests in Vaadin's CI environment, and uses TestBench to
     * connect to the testing hub.
     *
     * @return whether we are using a test hub
     */
    private static boolean isUsingHub() {
        return Boolean.TRUE.toString().equals(
                System.getProperty(USE_HUB_PROPERTY));
    }

    /**
     * If running on CI, get the host name from environment variable HOSTNAME
     *
     * @return the host name
     */
    private static String getDeploymentHostname() {
        return isUsingHub() ? System.getenv("HOSTNAME") : "localhost";
    }
}

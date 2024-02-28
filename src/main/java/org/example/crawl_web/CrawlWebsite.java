package org.example.crawl_web;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;


public class CrawlWebsite {
    private String url;

    public CrawlWebsite(String url) {
        this.url = url;
    }

    public String getHTMLSource() throws Exception {
        String pathChromeDriver = null;
        String osName = System.getProperty("os.name").toLowerCase();

        if(osName.contains("linux")) {
            pathChromeDriver = "chromedriver/chromedriver_linux";
        } else if(osName.contains("win")) {
            pathChromeDriver = "chromedriver\\chromedriver_win.exe";
        } else if(osName.contains("mac")) {
            pathChromeDriver = "chromedriver/chromedriver_mac";
        } else {
            throw new Exception("Not support for this OS");
        }

        System.setProperty("webdriver.chrome.driver", pathChromeDriver);

        WebDriver driver = new ChromeDriver(this.getOptions());
        System.out.println("Getting page HTML...");
        driver.get(this.url);
        String html = driver.getPageSource();
        driver.quit();
        return html;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private ChromeOptions getOptions() {
        ChromeOptions options = new ChromeOptions();

//        options.addArguments("headless");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36");

        return options;
    }
}

package webscraper;

import java.io.IOException;
import java.io.PrintWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MultiScraper {

    private static final String QUOTES_URL = "http://quotes.toscrape.com/";
    private static final String BOOKS_URL = "http://books.toscrape.com/";
    private static final String EXAMPLE_URL = "http://example.com";

    private static final int QUOTES_LIMIT = 20; // Max number of quotes to scrape
    private static final int BOOKS_LIMIT = 10;  // Max number of books to scrape

    public static void main(String[] args) {
        scrapeQuotes(QUOTES_URL, QUOTES_LIMIT);
        printSeparator();
        scrapeBooks(BOOKS_URL, BOOKS_LIMIT);
        printSeparator();
        scrapeExample(EXAMPLE_URL);
    }

    // -------------------- Quotes Scraper --------------------
    public static void scrapeQuotes(String url, int maxQuotes) {
        highlightMessage("Scraping Quotes from: " + url);
        int count = 0;
        int page = 1;

        try (PrintWriter writer = new PrintWriter("quotes.csv")) {
            writer.println("Quote,Author");

            while (count < maxQuotes) {
                Document doc = Jsoup.connect(url + "page/" + page + "/")
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                        .get();
                Elements quotes = doc.select(".quote");

                if (quotes.isEmpty()) break;

                for (Element quote : quotes) {
                    if (count >= maxQuotes) break;

                    String text = quote.select(".text").text();
                    String author = quote.select(".author").text();

                    System.out.println(colorText("Quote: ", "cyan") + text);
                    System.out.println(colorText("Author: ", "yellow") + author + "\n");

                    writer.println("\"" + text.replace("\"", "\"\"") + "\"," + author);
                    count++;
                }
                page++;
            }

            highlightMessage("Quotes saved to quotes.csv");
        } catch (IOException e) {
            System.out.println("Error scraping Quotes: " + e.getMessage());
        }
    }

    // -------------------- Books Scraper --------------------
    public static void scrapeBooks(String url, int maxBooks) {
        highlightMessage("Scraping Books from: " + url);
        int count = 0;
        int page = 1;

        try (PrintWriter writer = new PrintWriter("books.csv")) {
            writer.println("Title,Price");

            while (count < maxBooks) {
                Document doc = Jsoup.connect(url + "catalogue/page-" + page + ".html")
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                        .get();
                Elements books = doc.select(".product_pod");

                if (books.isEmpty()) break;

                for (Element book : books) {
                    if (count >= maxBooks) break;

                    String title = book.select("h3 > a").attr("title");
                    String price = book.select(".price_color").text();

                    System.out.println(colorText("Title: ", "cyan") + title);
                    System.out.println(colorText("Price: ", "yellow") + price + "\n");

                    writer.println("\"" + title.replace("\"", "\"\"") + "\"," + price);
                    count++;
                }
                page++;
            }

            highlightMessage("Books saved to books.csv");
        } catch (IOException e) {
            System.out.println("Error scraping Books: " + e.getMessage());
        }
    }

    // -------------------- Example Domain Scraper --------------------
    public static void scrapeExample(String url) {
        highlightMessage("Scraping Example Domain: " + url);

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .get();

            String heading = doc.select("h1").text();
            String paragraph = doc.select("p").text();
            Elements links = doc.select("a");

            System.out.println(colorText("Heading: ", "cyan") + heading);
            System.out.println(colorText("Paragraph: ", "yellow") + paragraph);
            System.out.println("\nLinks on the page:");
            for (Element link : links) {
                String linkText = link.text();
                String linkHref = link.attr("href");
                System.out.println(colorText("Text: ", "green") + linkText + " | URL: " + linkHref);
            }
            System.out.println();
        } catch (IOException e) {
            System.out.println("Error scraping Example Domain: " + e.getMessage());
        }
    }

    // -------------------- Utility Methods --------------------
    public static void highlightMessage(String message) {
        System.out.println("\u001B[32m" + message + "\u001B[0m"); // Green
    }

    public static void printSeparator() {
        System.out.println("\n===============================\n");
    }

    public static String colorText(String text, String color) {
        switch (color.toLowerCase()) {
            case "red": return "\u001B[31m" + text + "\u001B[0m";
            case "green": return "\u001B[32m" + text + "\u001B[0m";
            case "yellow": return "\u001B[33m" + text + "\u001B[0m";
            case "blue": return "\u001B[34m" + text + "\u001B[0m";
            case "cyan": return "\u001B[36m" + text + "\u001B[0m";
            default: return text;
        }
    }
}

package org.example;

import org.example.html_handler.HtmlHandler;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        HtmlHandler htmlHandler = new HtmlHandler();
        try {
            htmlHandler.extractContent();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
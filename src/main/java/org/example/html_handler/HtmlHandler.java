package org.example.html_handler;

import org.example.crawl_web.CrawlWebsite;
import org.example.dto.ElementInfo;
import org.example.support.ValidateElement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HtmlHandler {

    ElementAddition elementAddition = new ElementAddition();

    float threshold = Float.MAX_VALUE;

    List<String> content = new ArrayList<>();

    public void extractContent() throws Exception {
        CrawlWebsite crawlWebsite = new CrawlWebsite("https://vnexpress.net/cac-tinh-tiec-nuoi-vi-phai-dung-tuyen-lop-10-bang-ielts-4714931.html");
        String htmlSource = crawlWebsite.getHTMLSource();
        Document doc = Jsoup.parse(htmlSource);
        Element element = doc.selectXpath("//body").get(0);
        element.select("script, style, link, img, input, header, footer, audio, video, button, nav, svg, path, title" +
                "[class*=footer], [class*=header], [id*=footer], [id*=header]").remove();
//        System.out.println(element);
//        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        ElementInfo body = elementAddition.computeInformation(element);
        computeThreshold(elementAddition.getElementWithMaxDensitySum());
//        System.out.println(threshold);
        this.markContent(body);
        this.traverseFromRoot(body);

        for (String str : this.content) {
            System.out.println(str);
        }
    }

    private void markContent(ElementInfo elementInfo) {
        if (containsAllATag(elementInfo.getElement()) && ValidateElement.isValidTagName(elementInfo) && elementInfo.getTextDensity() >= threshold) {
            ElementInfo maxDensitySumTag = elementWithMaxDensitySumWithinNode(elementInfo);
            maxDensitySumTag.setContent(true);
//            System.out.println(elementInfo);
//            System.out.println(maxDensitySumTag);
//            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        }


        if (elementInfo.hasChild()) {
            for (ElementInfo child : elementInfo.getChildren()) {
                markContent(child);
            }
        }

    }

    private void traverseFromRoot(ElementInfo elementInfo) {
//        System.out.println(elementInfo);
//        System.out.println(elementInfo.getElement().text());
//        System.out.println(elementInfo.getElement().children());
//        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        if (elementInfo.isContent()) {
//            System.out.println(elementInfo);
//            System.out.println(elementInfo.getElement().text());
//            System.out.println(elementInfo.getChildren());
//            System.out.println(elementInfo.getParent());
//            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//            content.add(elementInfo.getElement().text());
            this.extractContentFromElementInfo(elementInfo);
            return;
        }
        if (elementInfo.hasChild()) {
            for (ElementInfo child : elementInfo.getChildren()) {
                traverseFromRoot(child);
            }
        }
    }

    private void extractContentFromElementInfo(ElementInfo elementInfo) {
        StringBuilder result = new StringBuilder();
        this.extractContentFromElement(elementInfo.getElement(), result);
        content.add(result.toString().trim());
    }

    private void extractContentFromElement(Element element, StringBuilder stringBuilder) {
        if (!ValidateElement.isValidTagName(element)) {
            return;
        }
        if (element.childrenSize() == 0) {
            stringBuilder.append(element.text().trim()).append(" ");
        }
//        Elements elementChildren = element.children();
//        for (Element child : elementChildren) {
//            if (!ValidateElement.isValidTagName(child)) {
//                continue;
//            }
//            if (child.tagName().equals("#text")) {
//                stringBuilder.append(child.ownText());
//            } else {
//                this.extractContentFromElement(child, stringBuilder);
//            }
//        }
        List<Node> elementChildren = element.childNodes();
        for (Node child : elementChildren) {
            if (child instanceof Element childElement) {
                if (!ValidateElement.isValidTagName(childElement)) {
                    if (childElement.tagName().equals("a") && isTextBlock(child.previousSibling(), child.nextSibling())) {
                        stringBuilder.append(childElement.ownText().trim()).append(" ");
                    }
                } else {
                    this.extractContentFromElement(childElement, stringBuilder);
                }
            } else if (child instanceof TextNode childTextNode) {
                stringBuilder.append(childTextNode.text().trim()).append(" ");
            }
        }
    }

    private ElementInfo elementWithMaxDensitySumWithinNode(ElementInfo elementInfo) {
        return traverseNodesFindMaxDensitySum(elementInfo);
    }

    private ElementInfo traverseNodesFindMaxDensitySum(ElementInfo elementInfo) {
        if (!elementInfo.hasChild()) {
            return elementInfo;
        }

        List<ElementInfo> children = new ArrayList<>(elementInfo.getChildren());
        for (ElementInfo child : children) {
            ElementInfo computedChild = traverseNodesFindMaxDensitySum(child);
            if (elementInfo.getDensitySum() < computedChild.getDensitySum()) {
                elementInfo = computedChild;
            }
        }

        return elementInfo;
    }

    private void computeThreshold(ElementInfo elementInfo) {
//        System.out.println(this.threshold);
//        System.out.println(elementInfo);
        if (elementInfo.isBodyTag()) {
            return;
        }
        if (this.threshold > elementInfo.getTextDensity()) {
            this.threshold = elementInfo.getTextDensity();
        }
        if (!elementInfo.hasParent()) {
            return;
        }
        this.computeThreshold(elementInfo.getParent());
    }

    private boolean isTextBlock(Node previousSibling, Node nextSibling) {
        if (previousSibling == null && nextSibling == null) {
            return false;
        } else if (previousSibling != null && nextSibling == null) {
            return (previousSibling instanceof TextNode ||
                    (previousSibling instanceof Element previousSiblingElement && !this.containsAllATag(previousSibling)));
        } else if (previousSibling == null && nextSibling != null) {
            return (nextSibling instanceof TextNode ||
                    (nextSibling instanceof Element nextSiblingElement && !this.containsAllATag(nextSibling)));
        } else {
            return (previousSibling instanceof TextNode && nextSibling instanceof TextNode) ||
                    (previousSibling instanceof TextNode && nextSibling instanceof Element nextSiblingElement && !this.containsAllATag(nextSibling)) ||
                    (nextSibling instanceof TextNode && previousSibling instanceof Element previousSiblingElement && !this.containsAllATag(previousSibling)) ||
                    (previousSibling instanceof Element previousSiblingElement1 && !this.containsAllATag(previousSibling) && nextSibling instanceof Element nextSiblingElement1 && !this.containsAllATag(nextSibling));
        }
    }

    private boolean containsAllATag(Node node) {
        int aTag = 0;
        int text = 0;
        for (Node child : node.childNodes()) {
            if (child instanceof Element childElement) {
                if (!childElement.tagName().equals("a")) {
                    aTag++;
                    break;
                }
            } else if (child instanceof TextNode childTextNode) {
                text++;
            }
        }
        return text > aTag;
    }
}

package org.example.support;

import org.example.dto.ElementInfo;
import org.jsoup.nodes.Element;

public class ValidateElement {
    public static boolean isValidTagName(ElementInfo elementInfo) {
        String tagName = elementInfo.getTagName();
        if (tagName.equals("a") ||
                tagName.equals("h1") ||
                tagName.equals("h2") ||
                tagName.equals("h3") ||
                tagName.equals("h4") ||
                tagName.equals("h5") ||
                tagName.equals("h6") ||
                (elementInfo.hasParent() && elementInfo.getParent().getTagName().equals("a"))) {
            return false;
        }
        return true;
    }
    public static boolean isValidTagName(Element element) {
        String tagName = element.tagName();
        if (tagName.equals("a") ||
                tagName.equals("h1") ||
                tagName.equals("h2") ||
                tagName.equals("h3") ||
                tagName.equals("h4") ||
                tagName.equals("h5") ||
                tagName.equals("h6") ||
                (element.hasParent() && element.parent().tagName().equals("a"))) {
            return false;
        }
        return true;
    }
}

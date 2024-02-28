package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class ElementInfo {

    private Element element;
    private int chars;
    private int tags;
    private int tagsForCompute;
    private float textDensity;
    private float densitySum;

    private boolean isContent;

    private ElementInfo parent;
    private List<ElementInfo> children;

    final private Pattern tagPattern = Pattern.compile("<[^\\/][^>]*>");

    public ElementInfo shallowCopy() {
        return ElementInfo.builder()
                .element(this.element)
                .chars(this.chars)
                .tags(this.tags)
                .tagsForCompute(this.tagsForCompute)
                .textDensity(this.textDensity)
                .densitySum(this.densitySum)
                .parent(this.parent)
                .children(this.children)
                .build();
    }

    public void addChild(ElementInfo elementInfo) {
        this.children.add(elementInfo);
    }

    public boolean hasChild() {
        return this.children != null && this.children.size() > 0;
    }

    public boolean hasParent() {
        return this.parent != null;
    }

    public String getTagName() {
        return this.element.tagName().toLowerCase();
    }

    public boolean isBodyTag() {
        return this.element.tagName().equals("body");
    }

    @Override
    public String toString() {
        if (this.element == null) {
            return "No";
        }
        try {
            return String.format("%s   :::   Chars=%d, Tags=%d, Density=%f, DensitySum=%f",
                    this.getElementFullTag(),
                    this.chars,
                    this.tags,
                    this.textDensity,
                    this.densitySum);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getElementFullTag() throws Exception {
        Matcher matcher = tagPattern.matcher(this.element.outerHtml());
        if (matcher.find()) {
            return matcher.group(0);
        } else {
            throw new Exception("Element has 0 tag");
        }
    }
}

package org.example.html_handler;

import lombok.Getter;
import lombok.Setter;
import org.example.dto.BasicInfo;
import org.example.dto.ElementInfo;
import org.example.support.ValidateElement;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ElementAddition {

    private List<ElementInfo> densityHistogram = new ArrayList<>();

    private ElementInfo elementWithMaxDensitySum;

    public ElementInfo computeInformation(Element element) {
        if (!ValidateElement.isValidTagName(element)) {
            return null;
        }
        int charCount = element.hasText() ? element.text().length() : 0;
        ElementInfo elementInfo = ElementInfo.builder()
                .element(element)
                .chars(charCount)
                .children(new ArrayList<>())
                .build();

        Elements children = element.children();
        if (children.size() == 0) {
            this.enrichElementInfo(elementInfo, 1, 0);
            return elementInfo;
        }

        int numberOfChildHasTag = 1;
        float sumChildrenTextDensity = 0;
        for (Element child : children) {
            if (this.elementHasTag(child)) {
                ElementInfo childElementInfo = this.computeInformation(child);
                if (childElementInfo == null) {
                    continue;
                }

                childElementInfo.setParent(elementInfo);
                numberOfChildHasTag += childElementInfo.getTagsForCompute();
                sumChildrenTextDensity += childElementInfo.getTextDensity();
                elementInfo.addChild(childElementInfo);
            }
        }

        this.enrichElementInfo(elementInfo, numberOfChildHasTag, sumChildrenTextDensity);

        if (elementWithMaxDensitySum == null) {
            elementWithMaxDensitySum = elementInfo;
        }

        if (elementWithMaxDensitySum.getDensitySum() < sumChildrenTextDensity) {
            elementWithMaxDensitySum = elementInfo;
        }

        return elementInfo;
    }

    private boolean elementHasTag(Element element) {
        return !element.tagName().equals("#text");
    }

    private void enrichElementInfo(ElementInfo elementInfo,
                                  int tags,
                                  float densitySum) {
        elementInfo.setTags(tags == 1 ? 1 : tags - 1);
        elementInfo.setTagsForCompute(tags);
        elementInfo.setTextDensity(this.computeDensity(elementInfo.getChars(), elementInfo.getTags()));
        elementInfo.setDensitySum(densitySum);
        densityHistogram.add(elementInfo);
    }

    private float computeDensity(int charCount, int tagCount) {
        return (((float)charCount)/((float)tagCount));
    }
}

package org.songfamily.tiem.nanodegree.app1.helpers;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Image;

import static org.junit.Assert.assertEquals;

public class ImageHelperTest {

    @Test
    public void testGetImageCommon() throws Exception {
        List<Image> noImages = new ArrayList<>(0);
        assertEquals(null, ImageHelper.getImageUrl(noImages));

        Image image1 = new Image();
        image1.url = "foo";
        List<Image> oneImage = new ArrayList<>(1);
        oneImage.add(image1);
        assertEquals("foo", ImageHelper.getImageUrl(oneImage));

        Image image2 = new Image();
        Image image3 = new Image();
        image2.url = "bar";
        image3.url = "baz";
        List<Image> twoImages = new ArrayList<>(2);
        twoImages.add(image2);
        twoImages.add(image3);
        assertEquals("bar", ImageHelper.getImageUrl(twoImages));
    }
}
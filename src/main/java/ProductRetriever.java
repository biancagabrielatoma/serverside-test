import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class ProductRetriever {
    private static final String sainsburysURL = "https://jsainsburyplc.github.io/" +
            "serverside-test/site/www.sainsburys.co.uk/" +
            "webapp/wcs/stores/servlet/gb/groceries/";
    private static final String index = "berries-cherries-currants6039.html";

    private static final String productNameAndPromotions = "productNameAndPromotions";
    private static final String tagNameProduct = "a";
    private static final String attributeKeyProduct = "href";
    private static final String tagNameParagraphProduct = "p";
    private static final String classNameCalories = "nutritionLevel1";
    private static final String classNameDescription = "productText";
    private static final String classNamePrice = "pricePerUnit";

    private static final String gross = "gross";
    private static final String vat = "vat";

    private static final String resultsKey = "results";
    private static final String totalKey = "total";

    @SuppressWarnings("unchecked")
    static JSONObject buildSainsburyProductsJSON() {
        JSONObject results = new JSONObject();
        JSONArray productArray = new JSONArray();
        JSONObject total = new JSONObject();

        double totalPrice = 0;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(sainsburysURL + index, String.class);
        String sainsburyURLResponse = responseEntity.getBody();

        if (sainsburyURLResponse == null || sainsburyURLResponse.isEmpty()) {
            return null;
        }

        Document document = Jsoup.parse(sainsburyURLResponse);
        Elements productNamesAndPromotions = document.getElementsByClass(productNameAndPromotions);

        for (Element product : productNamesAndPromotions) {

            String productURL = product.getElementsByTag(tagNameProduct).attr(attributeKeyProduct);
            String productURLContent = restTemplate.getForEntity(sainsburysURL + productURL, String.class).getBody();

            if (productURLContent == null || productURLContent.isEmpty()) {
                return null;
            }

            Document productDocument = Jsoup.parse(productURLContent);

            String productName = product.getElementsByTag(tagNameProduct).text();
            String calories = retrieveCalories(productDocument);
            String productDescription = retrieveDescription(productDocument);
            String productPricePerUnit = retrievePrice(productDocument);

            if (!productPricePerUnit.isEmpty()) {
                totalPrice += Double.valueOf(productPricePerUnit);
            }

            JSONObject productJson = retrieveJSONProduct(
                    productName,
                    calories,
                    productPricePerUnit,
                    productDescription);

            productArray.add(productJson);
        }

        total.put(gross, totalPrice);
        total.put(vat, totalPrice * 0.2);

        results.put(resultsKey, productArray);
        results.put(totalKey, total);

        return results;
    }

    private static String retrieveCalories(Document document) {
        Elements caloriesElements = document.getElementsByClass(classNameCalories);
        if (caloriesElements.size() <= 0) {
            return null;
        }
        return caloriesElements
                .get(0)
                .text()
                .replace("kcal", "");
    }

    private static String retrieveDescription(Document document) {
        Elements productDescriptionElements = document.getElementsByClass(classNameDescription);
        if (productDescriptionElements.size() <= 0) {
            return null;
        }

        String productDescription = productDescriptionElements
                .get(0)
                .getElementsByTag(tagNameParagraphProduct)
                .get(0)
                .text();

        if (productDescription.isEmpty())
            productDescription = productDescriptionElements
                    .get(0)
                    .child(1)
                    .child(1)
                    .text();

        return productDescription;
    }

    private static String retrievePrice(Document document) {
        return document.getElementsByClass(classNamePrice)
                .get(0)
                .text()
                .replace("£", "")
                .replace("/unit", "");
    }

    @SuppressWarnings("unchecked")
    private static JSONObject retrieveJSONProduct(String name,
                                                  String calories,
                                                  String price,
                                                  String description) {
        JSONObject productJson = new JSONObject();

        productJson.put("title", name);
        if (calories != null) {
            productJson.put("kcal_per_100g", calories);
        }
        productJson.put("unit_price", price);
        productJson.put("description", description);

        return productJson;
    }
}

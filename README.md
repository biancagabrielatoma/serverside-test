# serverside-test

The Launch.java class runs the application and prints out the JSON containing the products from the link: https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/webapp/wcs/stores/servlet/gb/groceries/berries-cherries-currants6039.html

ProductRetriever.java class retrieves the html pages and the internal referenced link of each product, gets the desired information (title, unit_price, kcal_per_100g and description) of each product, calculates the gross and vat and returns the JSON as specified in the task (https://jsainsburyplc.github.io/serverside-test/).

Unfortunately, it does not contain any unit/behavioral tests. Also, in scenarios such as the ones from below, exceptions can be thrown and eventually logged.

Possible test cases/scenarios:
 - custom product list
 - empty product list
 - main link does not return anything
 - product page link does not return anything

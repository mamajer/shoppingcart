import static java.lang.System.out;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ShoppingCart {

	static String APPLE = "Apple";
	static String ORANGE = "Orange";
	static String PLUM = "Plum";

	public static Map<String, Double> prices = new HashMap<>();

	static {
		prices.put(APPLE, 0.60);
		prices.put(ORANGE, 0.25);
	}

	static final DecimalFormat DECIMAL_FORMATTER = new DecimalFormat("Total Cost £###,###,##0.00 \n");
	static final DecimalFormat ITEM_DECIMAL_FORMATTER = new DecimalFormat("£###,###,##0.00");

	// Used for full shopping cart report . Intended for non-junit test
	public static void fullReport(List<String> items) {

		if (items == null || items.isEmpty()) {
			out.println("Error: Empty shopping cart\n");
			out.println(DECIMAL_FORMATTER.format(0.00));
		} else if (!(prices.keySet().containsAll(items))) {
			out.println("Error: Not all items stocked\n");
		} else {
			printShoppingList(items);
		}
		System.out.println(
				"-------------------------------------------------------------------------------------------------------------------");
	}

	private static List<ProductInformation> getListOfITems(List<String> items) {

		List<ProductInformation> shoppingList = items.stream()
				.collect(Collectors.groupingBy(e -> e.toString(), Collectors.counting())).entrySet().stream()
				.map(e -> new ProductInformation(prices.get(e.getKey()), e.getKey(), e.getValue().intValue()))
				.collect(Collectors.toList());

		return shoppingList;

	}

	private static Double getTotalFromList(List<ProductInformation> shoppingList) {

		return shoppingList.stream().collect(Collectors.summingDouble(o -> o.total));
	}

	public static Double getTotal(List<String> shoppingList) {
		if (shoppingList == null || shoppingList.isEmpty()) {
			return new Double(0.00);
		} else if (!(prices.keySet().containsAll(shoppingList))) {
			throw new IllegalArgumentException("Error: Not all items stocked");
		}
		return ShoppingCart.getTotalFromList(ShoppingCart.getListOfITems(shoppingList));

	}

	private static void printShoppingList(List<String> items) {
		List<ProductInformation> shoppingList = getListOfITems(items);
		Double overallTotal = getTotalFromList(shoppingList);
		String format = "%-20s%25s%25s%25s%20s%n";
		System.out.printf(format, "item", "original quantity", "discounted quantity", "per/unit price", "Total");
		shoppingList.forEach(item -> System.out.printf(format, item.name, item.quantity, item.quantityDiscounted,
				ITEM_DECIMAL_FORMATTER.format(item.price), ITEM_DECIMAL_FORMATTER.format(item.total)));

		out.println(DECIMAL_FORMATTER.format(overallTotal));

	}

	static Function<Integer, Integer> getDiscount(String item) {
		return discounts.get(item);
	}

	static Function<Integer, Integer> buyOneGetOneFree = new Function<Integer, Integer>() {
		public Integer apply(Integer itemCount) {
			return Math.floorDiv(itemCount, 2) + (itemCount % 2);
		}
	};

	static Function<Integer, Integer> threeForTwo = new Function<Integer, Integer>() {
		public Integer apply(Integer itemCount) {
			return Math.floorDiv(itemCount, 3) * 2 + (itemCount % 3);
		}
	};

	static final Map<String, Function<Integer, Integer>> discounts = new HashMap<>();

	static {
		discounts.put(APPLE, buyOneGetOneFree);
		discounts.put(ORANGE, threeForTwo);
	}

	static class ProductInformation {

		Double price;
		String name;
		Integer quantity;
		Double total;
		Integer quantityDiscounted;

		ProductInformation(Double price, String name, Integer quantity) {
			this.price = price;
			this.name = name;
			this.quantity = quantity;
			this.quantityDiscounted = (discounts.containsKey(name)) ? getDiscount(name).apply(quantity) : quantity;
			this.total = this.price * this.quantityDiscounted;
		}

	}

	// run tests
	public static void main(String args[]) {
		fullReport(Arrays.asList(APPLE));
		fullReport(Arrays.asList(APPLE, APPLE));
		fullReport(Arrays.asList(ORANGE));
		fullReport(Arrays.asList(ORANGE, ORANGE));
		fullReport(Arrays.asList(ORANGE, ORANGE, ORANGE));
		fullReport(Arrays.asList(APPLE, ORANGE, APPLE, ORANGE, ORANGE, APPLE, ORANGE, ORANGE, APPLE, ORANGE, ORANGE,
				APPLE, ORANGE, ORANGE, APPLE, ORANGE, ORANGE, APPLE, ORANGE, ORANGE));
		fullReport(null);
		fullReport(Arrays.asList());
		fullReport(Arrays.asList(PLUM));
	}

}
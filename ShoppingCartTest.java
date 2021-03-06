import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * 
 * Needs JUnit 5 on class path
 *
 */
public class ShoppingCartTest {

	static String APPLE = "Apple";
	static String ORANGE = "Orange";
	static String PLUM = "Plum";
	static BigDecimal APPLE_PRICE = ShoppingCart.convertIntToBigDecimal(60);
	static BigDecimal ORANGE_PRICE = ShoppingCart.convertIntToBigDecimal(25);
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

	@BeforeEach
	private void setUp() {
		System.setOut(new PrintStream(outContent));
		System.setErr(new PrintStream(errContent));
	}

	@ParameterizedTest
	@MethodSource("shoppingDataWithTotals")
	void checkGetTotal(List<String> shoppingList, BigDecimal expectedTotal) {
		assertEquals(expectedTotal, ShoppingCart.getTotal(shoppingList));
	}

	private static Stream<Arguments> shoppingDataWithTotals() {
		return Stream.of(Arguments.of(Arrays.asList(APPLE), APPLE_PRICE),
				Arguments.of(Arrays.asList(APPLE, APPLE), APPLE_PRICE),
				Arguments.of(Arrays.asList(ORANGE), ORANGE_PRICE),
				Arguments.of(Arrays.asList(ORANGE, ORANGE), ShoppingCart.convertIntToBigDecimal(50)),
				Arguments.of(Arrays.asList(ORANGE, ORANGE, ORANGE), ShoppingCart.convertIntToBigDecimal(50)),
				Arguments.of(
						Arrays.asList(APPLE, ORANGE, APPLE, ORANGE, ORANGE, APPLE, ORANGE, ORANGE, APPLE, ORANGE,
								ORANGE, APPLE, ORANGE, ORANGE, APPLE, ORANGE, ORANGE, APPLE, ORANGE, ORANGE),
						ShoppingCart.convertIntToBigDecimal(465)),
				Arguments.of(Arrays.asList(), ShoppingCart.convertIntToBigDecimal(0)),
				Arguments.of(null, ShoppingCart.convertIntToBigDecimal(0)));
	}

	@ParameterizedTest
	@MethodSource("shoppingDataForReport")
	void testFullReport(List<String> shoppingList, String expectedReport) {
		ShoppingCart.fullReport(shoppingList);
		assertTrue(outContent.toString().contains(expectedReport));
	}

	private static Stream<Arguments> shoppingDataForReport() {
		return Stream.of(Arguments.of(Arrays.asList(APPLE), "Total Cost �0.60"),
				Arguments.of(Arrays.asList(APPLE, APPLE), "Total Cost �0.60"),
				Arguments.of(Arrays.asList(ORANGE), "Total Cost �0.25"),
				Arguments.of(Arrays.asList(ORANGE, ORANGE), "Total Cost �0.50"),
				Arguments.of(Arrays.asList(ORANGE, ORANGE, ORANGE), "Total Cost �0.50"),
				Arguments.of(
						Arrays.asList(APPLE, ORANGE, APPLE, ORANGE, ORANGE, APPLE, ORANGE, ORANGE, APPLE, ORANGE,
								ORANGE, APPLE, ORANGE, ORANGE, APPLE, ORANGE, ORANGE, APPLE, ORANGE, ORANGE),
						"Total Cost �4.65"),
				Arguments.of(Arrays.asList(), "Total Cost �0.00"), Arguments.of(null, "Total Cost �0.00"),
				Arguments.of(Arrays.asList(PLUM), "Not all items stocked"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkIllegalArgumentException() {
		ShoppingCart.getTotal(Arrays.asList(PLUM));
	}

}

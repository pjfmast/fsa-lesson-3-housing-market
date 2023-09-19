import HousingMarket.*
import HousingMarket.energyFactor
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.IllegalArgumentException

class PropertyTest {


    @Test
    fun `energyFactor of terraced house should be 11 (Double)`() {
        // arrange
        val house = House("", 0, null, HousingType.TERRACED, 0)
        // act
        val eFactor = house.energyFactor()
        // assert
        assertEquals(11.0, eFactor)

    }

    @Test
    fun `getMonthlyPayments of teracced house from 100m2 and area 200m2 should be 17200 divded by 12`() {
        // arrange
        val house = House("", 100, 300000, HousingType.TERRACED, 200)
        // act
        val montlyPayments = house.getMonthlyPayments()
        // assert: (0,04 * 300.000 + 0,01 * 300.000 + 11 * 100 + 5 * 200) / 12 = 17.100 / 12
        assertEquals(17100/12, montlyPayments)
    }

    @Test
    fun `doOffer with lower bid should not be accepted`() {
        val house = House("", 0, 300000, HousingType.TERRACED, 0)
        val henk = Customer("Henk", "")
        val anne = Customer("Anne", "")

        house.doOffer(henk, 500000)
        house.doOffer(anne, 510000)
        house.doOffer(henk, 505000)

        val bids = house.allBids

        // last bid is not accepted so we expect two bids
        assertEquals(bids.size, 2)
    }

    @Test
    fun `doOffer with negative bid should throw IllegalArgumentException`() {
        // arrange:
        val house = House("", 0, 300000, HousingType.TERRACED, 0)
        val henk = Customer("Henk", "")


        //act and assert:
        assertThrows(IllegalArgumentException::class.java) {
            house.doOffer(henk, -1)
        }
    }
}
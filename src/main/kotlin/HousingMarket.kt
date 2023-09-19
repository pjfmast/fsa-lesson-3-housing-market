package HousingMarket

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.lang.IllegalArgumentException
import kotlin.random.Random

data class Bid(val priceOffered: Int, val customer: Customer, val timeOfBid: Instant)
data class Customer(val name: String, val email: String)
data class Picture(val description: String, val imageUrl: String)

interface Locatable {
    fun getLocation(): LatAndLong
}

data class LatAndLong(val latitude: Double, val longitude: Double)

class HousingMarket {
    private val allHouses: MutableList<Property> = mutableListOf()
    fun advertise(property: Property) = allHouses.add(property)
    fun advertise(properties: List<Property>) = allHouses.addAll(properties)

    fun search(minPrice: Int = 0, maxPrice: Int = Int.MAX_VALUE) =
        allHouses.filter { it.priceAsked in minPrice..maxPrice }

    fun search(query: (Property) -> Boolean): List<Property> =
        allHouses.filter { query(it) }

    companion object {
        var interest = 0.04
        fun showAdvertisements(properties: List<Property>, includeBids: Boolean = false) {
            println("All advertisements:")
            println("=".repeat(80))
            properties.forEach { property ->
                println(property)
                if (includeBids) println(property.allBids.joinToString(prefix = "\t\t", separator = "\n\t\t"))
                println("-".repeat(80))
            }
        }
    }
}

sealed class Property(
    val address: String,
    val livingArea: Int,
    var priceAsked: Int?
) : Locatable {
    // because the Bid instances lifetime is dependent on Property: 'composition relation between Property and Bid'
    // the mutable collection _bids is protected because adding a Bid needs checks.
    // A private MutableList with public immutable List can be defined more elegantly in K2
    //      see: https://github.com/Kotlin/KEEP/blob/explicit-backing-fields-re/proposals/explicit-backing-fields.md
    protected val _bids: MutableList<Bid> = mutableListOf()
    val allBids: List<Bid>
        get() = _bids

    // because a Picture can be added / removed: 'an aggregation relation between Property and Image'
    val images: MutableList<Picture> = mutableListOf()

    override fun getLocation(): LatAndLong {
        return LatAndLong(
            51.58494229691791 + Random.nextDouble(-0.1, 0.1),
            4.797559120743779 + Random.nextDouble(-0.1, 0.1)
        )
    }

    override fun toString(): String {
        return "${this.javaClass.simpleName} at $address" +
                " price: ${priceAsked ?: "prijs op aanvraag"} living area: $livingArea" +
                "\n\testimated monthly costs (mortgage, energy, maintenance): ${getMonthlyPayments()}"
    }

    abstract fun getMonthlyPayments(): Int?

    fun doOffer(customer: Customer, priceOffered: Int) {
        if (priceOffered <= 0) throw IllegalArgumentException("argument priceOfferd shoud be positive.")
        if (isAccepted(priceOffered)) {
            // because a Bid instance is created here a composition relation between Property and Bid exists:
            _bids.add(Bid(priceOffered, customer, Clock.System.now()))
            // simulate some time before new Bid is accepted:
            Thread.sleep(100)
        }
    }

    private fun isAccepted(priceOffered: Int): Boolean {
        return priceOffered > (_bids.maxOfOrNull { it.priceOffered } ?: Int.MIN_VALUE)
    }
}

class Garage(
    address: String,
    livingArea: Int,
    priceAsked: Int? = null,
    val hasElectricity: Boolean = false
) : Property(address, livingArea, priceAsked) {
    override fun getMonthlyPayments(): Int? = priceAsked?.times(HousingMarket.interest)?.div(12)?.toInt()
    override fun toString(): String {
        return super.toString() + if (hasElectricity) "\n\t with electricity!" else ""
    }
}

class Apartment(
    address: String,
    livingArea: Int,
    priceAsked: Int? = null,
    val paymentVVE: Int,
    val floor: Int
) : Property(address, livingArea, priceAsked) {
    override fun getMonthlyPayments(): Int? {
//        val price = priceAsked
//        return if (price != null) {
//            val mortgageYear = price * HousingMarket.interest
//            val energyCostsYear = livingArea * this.energyFactor()
//            (paymentVVE + (mortgageYear + energyCostsYear) / 12).toInt()
//        } else {
//            null
//        }
        val mortgage = priceAsked?.times(HousingMarket.interest)
        val energyCostsYear = livingArea * this.energyFactor()
        return (mortgage?.plus(energyCostsYear)?.plus(paymentVVE)?.div(12))?.toInt()
    }

    override fun toString(): String {
        return super.toString() +
                "\n\tlocated at ${floor.toOrdinal()} floor"
    }
}

class House(
    address: String,
    livingArea: Int,
    priceAsked: Int? = null,
    val type: HousingType,
    val plotArea: Int
) : Property(address, livingArea, priceAsked) {
    override fun getMonthlyPayments(): Int? {
        val price = priceAsked
        return if (price != null) {
            val mortgageYear = price * HousingMarket.interest
            val maintenanceCostYear = price * 0.01 + plotArea * 5
            val energyCostsYear = livingArea * this.energyFactor()
            ((mortgageYear + maintenanceCostYear + energyCostsYear) / 12).toInt()
        } else {
            null
        }
    }

    override fun toString(): String {
        return super.toString() +
                "\n\tthis ${type.toString().lowercase()} house is situated at $plotArea m2 plot area"
    }
}

// defined internal for testing purpose
internal fun Property.energyFactor(): Double =
    when (this) {
        is Apartment -> 9.0
        is House -> when (this.type) {
            HousingType.DETACHED, HousingType.BUNGALOW -> 15.0
            HousingType.SEMI_DETACHED -> 13.0
            HousingType.TERRACED -> 11.0
        }

        is Garage -> 0.0
    }

private fun Int.toOrdinal(): String =
    when (this) {
        1 -> "first"
        2 -> "second"
        3 -> "third"
        else -> "${this}th"
    }

enum class HousingType {
    DETACHED, SEMI_DETACHED, TERRACED, BUNGALOW
}


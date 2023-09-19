import HousingMarket.*

fun main() {
    val funda = HousingMarket()

    val henk = Customer("Henk", "Henk@breda.nl")
    val anne = Customer("Anne", "Anne@avans.nl")

    val house1 = House("Gastakker 12", 130, 380000, HousingType.TERRACED, 210)
    val house2 = House("Singel 123", 120, 650000, HousingType.TERRACED, 180)
    val house3 = House("Hogeschoollaan 1", 40000, null, HousingType.DETACHED, 25000)
    val house4 = House("Bosrijk 10", 110, 510000, HousingType.BUNGALOW, 380)

    val apartment1 = Apartment("Teteringsdijks 110", 65, 260000, 99, 3)
    val apartment2 = Apartment("Tuinzigtlaan 117", 90, 290000, 130, 5)

    val garage1 = Garage("Hofjes 11", 19, 21000, true)
    val garage2 = Garage("Hofjes 13", 19, 19000)
    val garage3 = Garage("Hofjes 13", 19, priceAsked = null, hasElectricity = true)

    val allProperties = listOf(house1, house2, house3, house4, apartment1, apartment2, garage1, garage2, garage3)

    funda.advertise(allProperties)

    house4.doOffer(henk, 500000)
    house4.doOffer(anne, 510000)
    house4.doOffer(henk, 505000)

    val found1 = funda.search(maxPrice = 400000)
    HousingMarket.showAdvertisements(found1)

    val found2 = funda.search { p -> p is Garage && p.hasElectricity }
    HousingMarket.showAdvertisements(found2)
}
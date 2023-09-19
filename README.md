# fsa-lesson-3-housing-market
Demo of realising a UML class diagram in Kotlin.
The class diagram is added as a PlantUML file: HousingMarket.puml
In the realisation the Kotlin code demonstrates:
- enum class HousingType
- data class LatAndLong
- sealed (abstract) class Property with abstract method: abstract fun getMonthlyPayments(): Int?
- inheritence: subclasses Apartment, House, Garage
- class HousingMarket
- example of extension functions
- some unit testing

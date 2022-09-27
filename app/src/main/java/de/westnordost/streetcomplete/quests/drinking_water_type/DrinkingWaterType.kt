package de.westnordost.streetcomplete.quests.drinking_water_type

import de.westnordost.streetcomplete.osm.Tags

enum class DrinkingWaterType(val osmKey: String, val osmValue: String, val providesDrinkingWater: Boolean = true) {
    WATER_FOUNTAIN_GENERIC("man_made", "drinking_fountain"),
    WATER_FOUNTAIN_JET("fountain", "bubbler"),
    WATER_FOUNTAIN_BOTTLE_REFILL_ONLY("fountain", "bottle_refill"),
    WATER_TAP("man_made", "water_tap"),
    //WATER_TAP_UNDRINKABLE("man_made", "water_tap", false),
    WATER_WELL("man_made", "water_well"),
    SPRING("natural", "spring"),
    DISUSED_DRINKING_WATER("disued:amenity", "drinking_water", false),
}

fun DrinkingWaterType.applyTo(tags: Tags) {
    tags[this.osmKey] = this.osmValue
    if (this == DrinkingWaterType.WATER_FOUNTAIN_JET || this == DrinkingWaterType.WATER_FOUNTAIN_GENERIC) {
        tags["man_made"] = "drinking_fountain"
    }
    if (this.providesDrinkingWater) {
        if (tags["disused:amenity"] == "drinking_water") {
            tags.remove("disused:amenity")
            tags["amenity"] = "drinking_water"
        }
    } else {
        if (tags["amenity"] == "drinking_water") {
            tags.remove("amenity")
        }
    }
}

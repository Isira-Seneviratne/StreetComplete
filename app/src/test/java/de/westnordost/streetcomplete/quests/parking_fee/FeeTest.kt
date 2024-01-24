package de.westnordost.streetcomplete.quests.parking_fee

import ch.poole.openinghoursparser.Rule
import de.westnordost.streetcomplete.data.osm.edits.update_tags.StringMapChangesBuilder
import de.westnordost.streetcomplete.data.osm.edits.update_tags.StringMapEntryAdd
import de.westnordost.streetcomplete.data.osm.edits.update_tags.StringMapEntryChange
import de.westnordost.streetcomplete.data.osm.edits.update_tags.StringMapEntryDelete
import de.westnordost.streetcomplete.data.osm.edits.update_tags.StringMapEntryModify
import de.westnordost.streetcomplete.osm.nowAsCheckDateString
import de.westnordost.streetcomplete.osm.opening_hours.parser.OpeningHoursRuleList
import kotlin.test.assertEquals
import kotlin.test.Test

class FeeTest {
    private val oh = OpeningHoursRuleList(listOf(Rule().apply { isTwentyfourseven = true }))
    private val ohStr = "24/7"


    @Test fun `apply HasNoFee`() {
        assertEquals(
            setOf(
                StringMapEntryAdd("fee", "no")
            ),
            HasNoFee.appliedTo(mapOf())
        )

        assertEquals(
            setOf(
                StringMapEntryModify("fee", "no", "no"),
                StringMapEntryAdd("check_date:fee", nowAsCheckDateString())
            ),
            HasNoFee.appliedTo(mapOf("fee" to "no"))
        )

        assertEquals(
            setOf(
                StringMapEntryAdd("fee", "no"),
                StringMapEntryDelete("fee:conditional", "no @ (24/7)")
            ),
            HasNoFee.appliedTo(mapOf("fee:conditional" to "no @ (24/7)"))
        )
    }

    @Test fun `apply HasFee`() {
        assertEquals(
            setOf(
                StringMapEntryAdd("fee", "yes")
            ),
            HasFee.appliedTo(mapOf())
        )

        assertEquals(
            setOf(
                StringMapEntryModify("fee", "yes", "yes"),
                StringMapEntryAdd("check_date:fee", nowAsCheckDateString())
            ),
            HasFee.appliedTo(mapOf("fee" to "yes"))
        )

        assertEquals(
            setOf(
                StringMapEntryAdd("fee", "yes"),
                StringMapEntryDelete("fee:conditional", "no @ (24/7)")
            ),
            HasFee.appliedTo(mapOf("fee:conditional" to "no @ (24/7)"))
        )
    }

    @Test fun `apply HasFeeAtHours`() {
        assertEquals(
            setOf(
                StringMapEntryAdd("fee", "no"),
                StringMapEntryAdd("fee:conditional", "yes @ ($ohStr)"),
            ),
            HasFeeAtHours(oh).appliedTo(mapOf())
        )

        assertEquals(
            setOf(
                StringMapEntryModify("fee", "no", "no"),
                StringMapEntryModify("fee:conditional", "yes @ ($ohStr)", "yes @ ($ohStr)"),
                StringMapEntryAdd("check_date:fee", nowAsCheckDateString())
            ),
            HasFeeAtHours(oh).appliedTo(mapOf(
                "fee" to "no",
                "fee:conditional" to "yes @ ($ohStr)"
            ))
        )
    }

    @Test fun `apply HasFeeExceptAtHours`() {
        assertEquals(
            setOf(
                StringMapEntryAdd("fee", "yes"),
                StringMapEntryAdd("fee:conditional", "no @ ($ohStr)"),
            ),
            HasFeeExceptAtHours(oh).appliedTo(mapOf())
        )

        assertEquals(
            setOf(
                StringMapEntryModify("fee", "yes", "yes"),
                StringMapEntryModify("fee:conditional", "no @ ($ohStr)", "no @ ($ohStr)"),
                StringMapEntryAdd("check_date:fee", nowAsCheckDateString())
            ),
            HasFeeExceptAtHours(oh).appliedTo(mapOf(
                "fee" to "yes",
                "fee:conditional" to "no @ ($ohStr)"
            ))
        )
    }
}

private fun Fee.appliedTo(tags: Map<String, String>): Set<StringMapEntryChange> {
    val cb = StringMapChangesBuilder(tags)
    applyTo(cb)
    return cb.create().changes
}

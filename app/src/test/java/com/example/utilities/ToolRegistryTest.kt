package com.example.utilities

import com.example.data.model.Language
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ToolRegistryTest {
    @Test
    fun everyToolHasNamesInBothLanguages() {
        ToolRegistry.tools.forEach { tool ->
            Language.entries.forEach { language ->
                assertNotEquals("${tool.id} name", "tool_${tool.id}_name", tool.name(language))
                assertNotEquals("${tool.id} desc", "tool_${tool.id}_desc", tool.description(language))
            }
        }
        assertEquals(ToolRegistry.tools.size, ToolRegistry.tools.map { it.id }.toSet().size)
    }

    @Test
    fun mergedToolIdsStillOpen() {
        assertEquals("emi", ToolRegistry.resolve("emi_planner")?.id)
        assertEquals("gst", ToolRegistry.resolve("gst_split")?.id)
        assertEquals("bmi", ToolRegistry.resolve("bmi_healthy_weight")?.id)
        assertEquals("percentage", ToolRegistry.resolve("discount")?.id)
        assertEquals(null, ToolRegistry.resolve("unknown_tool"))
    }

    @Test
    fun searchFindsTeluguAndEnglishNamesAndKeywords() {
        assertTrue(ToolRegistry.search("వడ్డీ").any { it.id == "vaddi" })
        assertTrue(ToolRegistry.search("tola").any { it.id == "unit_converter" })
        assertTrue(ToolRegistry.search("cheque").any { it.id == "number_words" })
        assertTrue(ToolRegistry.search("EMI").any { it.id == "emi" })
        assertTrue(ToolRegistry.search("zzz").isEmpty())
    }
}

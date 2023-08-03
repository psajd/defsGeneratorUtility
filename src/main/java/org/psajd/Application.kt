package org.psajd

fun generateStub(storyText: String): String {
    var stubCode = ""
    val lines = storyText.lines()

    var lastMethod: String? = null
    var lastStepType: String? = null

    for (line in lines) {
        val trimmedLine = line.trim()
        when {
            trimmedLine.startsWith("Given") -> {
                lastMethod = processStep(trimmedLine, "Given")
                lastStepType = "Given"
                stubCode += lastMethod
            }

            trimmedLine.startsWith("When") -> {
                lastMethod = processStep(trimmedLine, "When")
                lastStepType = "When"
                stubCode += lastMethod
            }

            trimmedLine.startsWith("Then") -> {
                lastMethod = processStep(trimmedLine, "Then")
                lastStepType = "Then"
                stubCode += lastMethod
            }

            trimmedLine.startsWith("And") -> {
                if (lastMethod != null && lastStepType != null) {
                    lastMethod = processStep(trimmedLine, lastStepType, true)
                    stubCode += lastMethod
                }
            }
        }
    }

    return stubCode
}

private fun processStep(trimmedLine: String, stepType: String, isAnd: Boolean = false): String {
    val step = if (isAnd) {
        trimmedLine.substring("And".length).trim()
    } else {
        trimmedLine.substring(stepType.length).trim()
    }

    val variables = extractVariables(trimmedLine)
    var variablesStr = variables.joinToString(": Unit, ") { it }
    if (variablesStr.isNotBlank()) {
        variablesStr = variablesStr.plus(": Unit")
    }

    return """
        @$stepType("$step")
        fun `$step step`($variablesStr) {
            // Implement $stepType step here
        }
        
    """.trimIndent()
}

private fun extractVariables(line: String): List<String> {
    val regex = Regex("<([^>]+)>")
    val matches = regex.findAll(line)
    return matches.map { matchResult ->
        matchResult.groupValues[1]
    }.toList()
}

fun generateClass(className: String, storyText: String): String {
    val generatedMethods = generateStub(storyText).replace("\n", "\n\t")

    return """
class $className {
    $generatedMethods   
}
    """
}

fun main() {
    // Пример использования
    val storyContent = """
        Given some preconditions with <temp> and <account> with: 
        |adsf|asdf|asdf|
        |1312|432 |3423|
        And some additional conditions with <salary>
        And another some additional conditions with <salary>
        When some actions with <id>
        Then some outcomes with <uiid> and <hz>
    """.trimIndent()

    val generatedStub = generateClass(className = "TestClass", storyContent)
    println(generatedStub)
}

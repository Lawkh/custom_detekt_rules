package org.example.detekt

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtClass

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.isPublic

class FunctionOnlyInterfaceRule(config: Config) : Rule(config) {

    override val issue = Issue(
        id = "FunctionOnlyInterface",
        severity = Severity.Style,
        description = "Interface with only 1 function should be declared as 'fun interface'",
        debt = Debt.FIVE_MINS
    )

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)

        if (!klass.isInterface() || klass.isFun) return

        val functions = klass.getBody()
            ?.declarations
            ?.filterIsInstance<KtNamedFunction>()
            ?.filter { it.hasModifier(KtTokens.ABSTRACT_KEYWORD) || it.bodyExpression == null }
            ?: return

        val properties = klass.getBody()
            ?.declarations
            ?.filterIsInstance<KtProperty>()
            ?.filter { it.hasModifier(KtTokens.ABSTRACT_KEYWORD) }
            ?: return

        if (functions.size == 1 && properties.isEmpty()) {
            report(
                CodeSmell(
                    issue,
                    Entity.atName(klass),
                    message = "This interface must be 'fun interface'."
                )
            )
        }
    }
}
